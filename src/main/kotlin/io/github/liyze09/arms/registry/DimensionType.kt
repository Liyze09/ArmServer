package io.github.liyze09.arms.registry

import io.github.liyze09.arms.common.Identifier
import net.minecraftarm.api.nbt.*

class DimensionType(
    val hasSkylight: Boolean,
    val hasCeiling: Boolean,
    val ultrawarm: Boolean,
    val natural: Boolean,
    val coordinateScale: Double,
    val bedWorks: Boolean,
    val respawnAnchorWorks: Boolean,
    val hasRaids: Boolean,
    val minY: Int,
    val height: Int,
    val logicalHeight: Int,
    val infiniburn: Identifier,
    val effects: Identifier,
    val ambientLight: Float,
    val piglinSafe: Boolean,
    val monsterSpawnLightLevel: Int,
    val monsterSpawnBlockLightLimit: Int
) : Registries.Registry {
    override fun toNbt() =
        NbtCompound(
            "has_skylight" to NbtByte(hasSkylight),
            "has_ceiling" to NbtByte(hasCeiling),
            "ultrawarm" to NbtByte(ultrawarm),
            "natural" to NbtByte(natural),
            "coordinate_scale" to NbtDouble(coordinateScale),
            "bed_works" to NbtByte(bedWorks),
            "respawn_anchor_works" to NbtByte(respawnAnchorWorks),
            "has_raids" to NbtByte(hasRaids),
            "min_y" to NbtInt(minY),
            "height" to NbtInt(height),
            "logical_height" to NbtInt(logicalHeight),
            "infiniburn" to NbtString(infiniburn),
            "effects" to NbtString(effects),
            "ambient_light" to NbtFloat(ambientLight),
            "piglin_safe" to NbtByte(piglinSafe),
            "monster_spawn_light_level" to NbtInt(monsterSpawnLightLevel),
            "monster_spawn_block_light_limit" to NbtInt(monsterSpawnBlockLightLimit)
        )
}