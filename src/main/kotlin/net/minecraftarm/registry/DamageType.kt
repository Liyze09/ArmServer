package net.minecraftarm.registry

import com.google.gson.JsonParser
import net.minecraftarm.common.Identifier
import net.minecraftarm.nbt.NbtCompound
import net.minecraftarm.nbt.NbtFloat
import net.minecraftarm.nbt.NbtString
import java.io.FileNotFoundException

class DamageType(
    val messageId: Identifier,
    val effects: String?,
    val deathMessageType: String?,
    val exhaustion: Float,
    val scaling: String
) : Registries.Registry {
    override fun toNbt(): NbtCompound {
        val nbt = mutableListOf(
            "message_id" to NbtString(messageId),
            "exhaustion" to NbtFloat(exhaustion),
            "scaling" to NbtString(scaling)
        )
        if (effects != null) nbt.add("effects" to NbtString(effects))
        if (deathMessageType != null) nbt.add("death_message_type" to NbtString(deathMessageType))
        return NbtCompound(*nbt.toTypedArray())
    }

    companion object {
        val damageTypes: Map<Identifier, DamageType> = mutableMapOf()
        init {
            damageTypes as MutableMap<Identifier, DamageType>
            JsonParser.parseReader(
                DamageType::class.java
                    .getResourceAsStream("/registries/damage_types.json")
                    ?.bufferedReader() ?: throw FileNotFoundException("Could not find vanilla damage types json")
            ).asJsonObject.entrySet().forEach {
                val value = it.value.asJsonObject
                damageTypes[Identifier(it.key)] = DamageType(
                    Identifier(value.get("message_id").asString),
                    if (value.has("effects")) it.value.asJsonObject.get("effects").asString else null,
                    if (value.has("death_message_type")) it.value.asJsonObject.get("death_message_type").asString else null,
                    value.get("exhaustion").asFloat,
                    value.get("scaling").asString
                )
            }
        }
    }
}