package io.github.liyze09.arms.world.impl

import io.github.liyze09.arms.common.Identifier
import io.github.liyze09.arms.registry.DimensionType
import io.github.liyze09.arms.world.Dimension
import io.github.liyze09.arms.world.gen.impl.ArmWorldgen

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