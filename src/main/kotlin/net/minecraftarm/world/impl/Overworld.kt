package net.minecraftarm.world.impl

import net.minecraftarm.common.Identifier
import net.minecraftarm.registry.DimensionType
import net.minecraftarm.world.Dimension
import net.minecraftarm.world.gen.impl.ArmWorldgen

class Overworld : Dimension(ArmWorldgen) {
    override val dimensionType: DimensionType
        get() = overworld
    override val name: Identifier
        get() = Identifier("minecraft", "overworld")

    override fun getProtocolId() = 0

    companion object {
        val overworld = DimensionType(
            hasSkylight = true,
            hasCeiling = false,
            hasRaids = true,
            natural = true,
            piglinSafe = false,
            respawnAnchorWorks = false,
            bedWorks = true,
            logicalHeight = 256,
            minY = 0,
            height = 256,
            coordinateScale = 1.0,
            ultrawarm = false,
            infiniburn = Identifier("#minecraft", "infiniburn_overworld"),
            effects = Identifier("#minecraft", "overworld"),
            ambientLight = 0F,
            monsterSpawnLightLevel = 0,
            monsterSpawnBlockLightLimit = 0,
        )
    }
}