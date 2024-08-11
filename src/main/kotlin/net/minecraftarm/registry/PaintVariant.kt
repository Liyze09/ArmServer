package net.minecraftarm.registry

import net.minecraftarm.common.Identifier
import net.minecraftarm.common.Identifier.Companion.mc
import net.minecraftarm.nbt.NbtCompound
import net.minecraftarm.nbt.NbtInt
import net.minecraftarm.nbt.NbtString

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

    companion object {
        val paintVariants = arrayOf(
            mc("fern") to PaintVariant(
                mc("fern"), 4, 3
            ),
            mc("backyard") to PaintVariant(
                mc("backyard"), 4, 3
            )
            // TODO More vanilla paintings
        )
    }
}