package io.github.liyze09.arms.registry

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.liyze09.arms.common.Identifier
import io.github.liyze09.arms.common.Position
import io.github.liyze09.arms.world.Dimension

val gson = Gson()

val blockRegistriesJson: JsonObject = JsonParser.parseReader(
    Registries::class.java.getResourceAsStream("/registries/blocks.json")
        ?.bufferedReader() ?: throw RuntimeException("Failed to load blocks.json")
).asJsonObject

internal fun blockRegistryInit() {
    val json = Registries.jsonRegistries
        .getAsJsonObject("minecraft:block")
        .getAsJsonObject("entries")
    Registries.registries[Registries.RegistryTypes.BLOCK]?.forEach { (id, registry) ->
        registry as Block
        registry.getStates().forEach {
            blockStatesByProtocolId[it.protocolId] = it
            idByBlockState[it] = it.protocolId
        }
        json.getAsJsonObject(id.toString()).get("protocol_id").asInt.let {
            blocksByProtocolId[it] = registry
            idByBlock[registry] = it
        }
    }
}

abstract class Block(val blockSettings: BlockSettings) : Registries.Registry() {
    abstract fun getStates(): List<BlockState>
    abstract fun getDefaultState(): BlockState?
    open fun beforeBlockActionApply(action: BlockAction, dimension: Dimension, position: Position): Any? = null
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
    BLOCK_UPDATE
}

abstract class BlockState {
    var protocolId: Int = -1
}

abstract class AutoBlock(
    identifier: Identifier,
    blockSettings: BlockSettings,
    stateType: Class<out BlockState>,
    autoRegistry: Boolean = true
) : Block(blockSettings) {
    private val states = mutableListOf<BlockState>()
    private var defaultState: BlockState? = null

    init {
        blockRegistriesJson
            .get(identifier.toString()).asJsonObject
            .get("states").asJsonArray
            .forEach {
                it.asJsonObject.let { obj ->
                    val state = gson.fromJson(obj.get("properties").asJsonObject, stateType)
                    state.protocolId = obj.get("id").asInt
                    if (obj.has("default") && obj.get("default").asBoolean) defaultState = state
                    states.add(state)
                }
            }
        @Suppress("LeakingThis")
        if (autoRegistry) Registries.register(Registries.RegistryTypes.BLOCK, identifier, this)
    }

    override fun getDefaultState() = defaultState

    override fun getStates() = states
}

internal val blocksByProtocolId = mutableMapOf<Int, Block>()
internal val idByBlock = mutableMapOf<Block, Int>()

internal val blockStatesByProtocolId = mutableMapOf<Int, BlockState>()
internal val idByBlockState = mutableMapOf<BlockState, Int>()