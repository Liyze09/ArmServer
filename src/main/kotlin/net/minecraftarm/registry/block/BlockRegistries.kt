package net.minecraftarm.registry.block

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraftarm.registry.Registries

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

internal val blocksByProtocolId = mutableMapOf<Int, Block>()
internal val idByBlock = mutableMapOf<Block, Int>()

internal val blockStatesByProtocolId = mutableMapOf<Int, BlockState>()
internal val idByBlockState = mutableMapOf<BlockState, Int>()