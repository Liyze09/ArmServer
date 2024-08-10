package io.github.liyze09.arms.registry

import io.github.liyze09.arms.common.Identifier
import net.minecraftarm.api.nbt.NbtCompound
import net.minecraftarm.api.nbt.NbtInt
import net.minecraftarm.api.nbt.NbtString

class PaintVariant(
    val assetId: Identifier,
    val height: Int,
    val width: Int,
) : Registries.Registry {
    override fun toNbt() = NbtCompound(
        "asset_id" to NbtString(assetId),
        "height" to NbtInt(height),
        "width" to NbtInt(width),
    )
}