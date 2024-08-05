package io.github.liyze09.arms.registry

import com.google.gson.JsonParser


internal fun blockRegistryInit() {
    val json = JsonParser.parseReader(
        Registries::class.java.getResourceAsStream("/registries/registries.json")
            ?.bufferedReader() ?: throw RuntimeException("Failed to load blocks.json")
    ).asJsonObject
        .getAsJsonObject("minecraft:block")
        .getAsJsonObject("entries")
    Registries.registries[Registries.RegistryTypes.BLOCK]?.forEach { (id, registry) ->
        registry as Block
        json.getAsJsonObject(id.toString()).get("protocol_id").asInt.let {
            registry.protocolId = it
        }
    }
}

abstract class Block(val blockSettings: BlockSettings) : Registries.Registry() {
    var protocolId = -1
    // TODO 24/08/04
}

class BlockSettings {
    // TODO 24/08/04
}

abstract class BlockState {
    abstract var protocolId: Int
    // TODO 24/08/04
}