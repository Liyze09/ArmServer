package io.github.liyze09.arms.registry


internal fun blockRegistryInit() {
    // TODO BlockState
    val json = Registries.jsonRegistries
        .getAsJsonObject("minecraft:block")
        .getAsJsonObject("entries")
    Registries.registries[Registries.RegistryTypes.BLOCK]?.forEach { (id, registry) ->
        registry as Block
        registry.getStates().forEach {
            blockStatesByProtocolId[it.getProtocolId()] = it
            idByBlockState[it] = it.getProtocolId()
        }
        json.getAsJsonObject(id.toString()).get("protocol_id").asInt.let {
            blocksByProtocolId[it] = registry
            idByBlock[registry] = it
        }
    }
}

abstract class Block(val blockSettings: BlockSettings) : Registries.Registry() {
    abstract fun getStates(): List<BlockState>
    abstract fun getDefaultState(): BlockState
    open fun beforeBlockActionApply(action: BlockAction): Any? = null
    open fun duringBlockActionApply(action: BlockAction, msg: Any?) {}
}

class BlockSettings {
    internal var hardness: Float = 2.0F
    internal var resistance: Float = 6.0F
    internal var type = BlockType.SOLID
    fun hardness(hardness: Float): BlockSettings {
        this.hardness = hardness
        return this
    }

    fun resistance(resistance: Float): BlockSettings {
        this.resistance = resistance
        return this
    }

    fun strength(strength: Float): BlockSettings {
        this.hardness = strength
        this.resistance = strength
        return this
    }

    fun type(type: BlockType): BlockSettings {
        this.type = type
        return this
    }
}

enum class BlockType {
    AIR,
    SOLID,
    LIQUID,
}

enum class BlockAction {
    BREAK,
    PLACE,
    INTERACT,
    STEP_ON,
    STEP_OFF,
    FALL,
    BLOCK_ACTION,
    BLOCK_UPDATE,
    EXPLODE,
    BREAK_ANIMATION,
    PLACE_ANIMATION,
    BLOCK_DESTROY_ANIMATION,
    BLOCK_SYNC,
    BLOCK_CHANGE,
    BLOCK_UPDATE_NEIGHBORS,
    BLOCK_UPDATE_NEIGHBORS_AND_SELF,
}

abstract class BlockState {
    abstract fun getProtocolId(): Int
}

internal val blocksByProtocolId = mutableMapOf<Int, Block>()
internal val idByBlock = mutableMapOf<Block, Int>()

internal val blockStatesByProtocolId = mutableMapOf<Int, BlockState>()
internal val idByBlockState = mutableMapOf<BlockState, Int>()