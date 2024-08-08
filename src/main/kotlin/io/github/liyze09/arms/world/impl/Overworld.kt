package io.github.liyze09.arms.world.impl

import io.github.liyze09.arms.common.Identifier
import io.github.liyze09.arms.world.Dimension
import io.github.liyze09.arms.world.gen.impl.ArmWorldgen

class Overworld : Dimension(ArmWorldgen) {
    override val name: Identifier
        get() = Identifier("minecraft", "overworld")

    override fun getProtocolId() = 0
}