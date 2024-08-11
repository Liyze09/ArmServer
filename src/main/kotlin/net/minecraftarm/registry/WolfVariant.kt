package net.minecraftarm.registry

import net.minecraftarm.common.Identifier
import net.minecraftarm.common.Identifier.Companion.mc
import net.minecraftarm.nbt.NbtArray
import net.minecraftarm.nbt.NbtCompound
import net.minecraftarm.nbt.NbtString
import net.minecraftarm.nbt.NbtTag

class WolfVariant(
    val wildTexture: Identifier,
    val tameTexture: Identifier,
    val angryTexture: Identifier,
    vararg val biomes: Identifier
) : Registries.Registry {
    override fun toNbt() = NbtCompound(
        "wild_texture" to NbtString(wildTexture),
        "tame_texture" to NbtString(tameTexture),
        "angry_texture" to NbtString(angryTexture),
        // TODO: biomes
        "biomes" to /* if (biomes.size == 1) NbtString(biomes[0])
        else NbtArray(biomes.map { NbtString(it) })*/ NbtArray<NbtString>(NbtTag.NbtType.STRING)
    )

    companion object {
        val ASHEN = WolfVariant(
            mc("entity/wolf/wolf_ashen"),
            mc("entity/wolf/wolf_ashen_angry"),
            mc("entity/wolf/wolf_ashen_tame"),
            mc("snowy_taiga")
        )
        val BLACK = WolfVariant(
            mc("entity/wolf/wolf_black"),
            mc("entity/wolf/wolf_black_angry"),
            mc("entity/wolf/wolf_black_tame"),
            mc("old_growth_pine_taiga")
        )
        val CHESTNUT = WolfVariant(
            mc("entity/wolf/wolf_chestnut"),
            mc("entity/wolf/wolf_chestnut_angry"),
            mc("entity/wolf/wolf_chestnut_tame"),
            mc("old_growth_spruce_taiga")
        )
        val PALE = WolfVariant(
            mc("entity/wolf/wolf_pale"),
            mc("entity/wolf/wolf_pale_angry"),
            mc("entity/wolf/wolf_pale_tame"),
            mc("taiga")
        )
        val RUSTY = WolfVariant(
            mc("entity/wolf/wolf_rusty"),
            mc("entity/wolf/wolf_rusty_angry"),
            mc("entity/wolf/wolf_rusty_tame"),
            Identifier("#minecraft", "is_jungle")
        )
        val SNOWY = WolfVariant(
            mc("entity/wolf/wolf_snowy"),
            mc("entity/wolf/wolf_snowy_angry"),
            mc("entity/wolf/wolf_snowy_tame"),
            mc("grove")
        )
        val SPOTTED = WolfVariant(
            mc("entity/wolf/wolf_spotted"),
            mc("entity/wolf/wolf_spotted_angry"),
            mc("entity/wolf/wolf_spotted_tame"),
            Identifier("#minecraft", "is_savanna")
        )
        val STRIPED = WolfVariant(
            mc("entity/wolf/wolf_striped"),
            mc("entity/wolf/wolf_striped_angry"),
            mc("entity/wolf/wolf_striped_tame"),
            Identifier("#minecraft", "is_badlands")
        )
        val WOODS = WolfVariant(
            mc("entity/wolf/wolf_woods"),
            mc("entity/wolf/wolf_woods_angry"),
            mc("entity/wolf/wolf_woods_tame"),
            Identifier("#minecraft", "forest")
        )
        val wolfVariants = arrayOf(
            mc("ashen") to ASHEN,
            mc("black") to BLACK,
            mc("chestnut") to CHESTNUT,
            mc("pale") to PALE,
            mc("rusty") to RUSTY,
            mc("snowy") to SNOWY,
            mc("spotted") to SPOTTED,
            mc("striped") to STRIPED,
            mc("woods") to WOODS,
        )
    }
}