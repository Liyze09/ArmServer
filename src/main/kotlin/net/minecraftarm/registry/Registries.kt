package net.minecraftarm.registry

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.packet.clientbound.RegistryData
import io.github.liyze09.arms.network.packet.clientbound.RegistryDataPacket
import net.minecraftarm.block.Air
import net.minecraftarm.block.GrassBlock
import net.minecraftarm.common.Identifier
import net.minecraftarm.common.Identifier.Companion.mc
import net.minecraftarm.nbt.NbtCompound
import net.minecraftarm.world.impl.Overworld

object Registries {
    val jsonRegistries: JsonObject = JsonParser.parseReader(
        Registries::class.java.getResourceAsStream("/registries/registries.json")
            ?.bufferedReader() ?: throw RuntimeException("Failed to load registries.json")
    ).asJsonObject

    enum class RegistryTypes(val id: Identifier? = null) {
        BLOCK,
        ITEM,
        BLOCK_ENTITY,
        ENTITY,
        FLUID,
        PARTICLE,
        RECIPE_TYPE,
        RECIPE,
        POTION,
        EFFECT,
        ENCHANTMENT,
        SOUND_EVENT,
        VILLAGER_PROFESSION,
        STRUCTURE,
        BIOME(Identifier("minecraft", "worldgen/biome")),
        DAMAGE_TYPE(Identifier("minecraft", "damage_type")),
        CHAT_TYPE(Identifier("minecraft", "chat_type")),
        DIMENSION(Identifier("minecraft", "dimension_type")),
        BANNER_PATTERN(Identifier("minecraft", "banner_pattern")),
        ARMOR_TRIM_PATTERN(Identifier("minecraft", "trim_pattern")),
        ARMOR_TRIM_MATERIAL(Identifier("minecraft", "trim_material")),
        WOLF_VARIANT(Identifier("minecraft", "wolf_variant")),
        PAINTING_VARIANT(Identifier("minecraft", "painting_variant"))
    }

    internal val registries =
        mutableMapOf<RegistryTypes, MutableMap<Identifier, Registry>>(
            RegistryTypes.BLOCK to mutableMapOf(),
            RegistryTypes.ITEM to mutableMapOf(),
            RegistryTypes.BLOCK_ENTITY to mutableMapOf(),
            RegistryTypes.ENTITY to mutableMapOf(),
            RegistryTypes.FLUID to mutableMapOf(),
            RegistryTypes.PARTICLE to mutableMapOf(),
            RegistryTypes.RECIPE_TYPE to mutableMapOf(),
            RegistryTypes.POTION to mutableMapOf(),
            RegistryTypes.EFFECT to mutableMapOf(),
            RegistryTypes.ENCHANTMENT to mutableMapOf(),
            RegistryTypes.SOUND_EVENT to mutableMapOf(),
            RegistryTypes.VILLAGER_PROFESSION to mutableMapOf(),
            RegistryTypes.BIOME to mutableMapOf(),
            RegistryTypes.STRUCTURE to mutableMapOf(),
            RegistryTypes.DAMAGE_TYPE to mutableMapOf(),
            RegistryTypes.CHAT_TYPE to mutableMapOf(),
            RegistryTypes.DIMENSION to mutableMapOf(),
            RegistryTypes.RECIPE to mutableMapOf(),
            RegistryTypes.BANNER_PATTERN to mutableMapOf(),
            RegistryTypes.ARMOR_TRIM_PATTERN to mutableMapOf(),
            RegistryTypes.ARMOR_TRIM_MATERIAL to mutableMapOf(),
            RegistryTypes.WOLF_VARIANT to mutableMapOf(),
            RegistryTypes.PAINTING_VARIANT to mutableMapOf()
        )

    fun register(
        type: RegistryTypes,
        name: Identifier,
        obj: Registry
    ) {
        registries[type]!![name] = obj
    }

    fun register(
        type: RegistryTypes,
        vararg registries: Pair<Identifier, Registry>
    ) {
        registries.forEach { (name, registry) ->
            register(type, name, registry)
        }
    }

    fun register(
        type: RegistryTypes,
        map: Map<Identifier, Registry>
    ) {
        map.forEach { (name, registry) ->
            register(type, name, registry)
        }
    }

    interface Registry {
        fun toNbt(): NbtCompound {
            throw UnsupportedOperationException()
        }
    }

    fun sendRegistryData(connection: Connection) {
        registries.forEach { (type, registries) ->
            if (type.id != null && registries.isNotEmpty()) {
                connection.sendPacket(
                    RegistryData(
                        type.id,
                        registries.map { (name, registry) ->
                            name to registry.toNbt()
                        }.toMap()
                    ),
                    RegistryDataPacket
                )
            }
        }
    }

    fun vanillaRegistries() {
        register(
            RegistryTypes.BLOCK,
            mc("grass_block"),
            GrassBlock()
        )
        register(
            RegistryTypes.BLOCK,
            mc("air"),
            Air()
        )
        register(
            RegistryTypes.DIMENSION,
            mc("overworld"),
            Overworld.overworld
        )
        register(
            RegistryTypes.WOLF_VARIANT,
            *WolfVariant.wolfVariants
        )
        register(
            RegistryTypes.PAINTING_VARIANT,
            *PaintVariant.paintVariants
        )
        register(
            RegistryTypes.DAMAGE_TYPE,
            DamageType.damageTypes
        )
    }
}