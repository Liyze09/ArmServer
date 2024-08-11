package net.minecraftarm.registry.block

import net.minecraftarm.common.Identifier
import net.minecraftarm.registry.Registries

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