package io.github.liyze09.arms.registry

import io.github.liyze09.arms.common.Identifier
import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.packet.clientbound.RegistryData
import io.github.liyze09.arms.network.packet.clientbound.RegistryDataPacket
import net.benwoodworth.knbt.Nbt
import net.benwoodworth.knbt.NbtCompression
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.NbtVariant

object Registries {
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

    internal val registries = mutableMapOf<RegistryTypes, MutableMap<Identifier, Registry>>(
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

    fun register(type: RegistryTypes, name: Identifier, obj: Registry) {
        registries[type]!![name] = obj
    }

    abstract class Registry {
        open fun toNbt(): NbtTag {
            throw UnsupportedOperationException()
        }
    }

    fun sendRegistryData(connection: Connection) {
        registries.forEach { (type, registries) ->
            if (type.id != null) {
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

    val nbtSerializer = Nbt {
        variant = NbtVariant.Java
        compression = NbtCompression.None
    }
}