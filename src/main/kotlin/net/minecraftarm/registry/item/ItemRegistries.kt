package net.minecraftarm.registry.item

import net.minecraftarm.registry.Registries

fun itemRegistriesInit() {
    val json = Registries.jsonRegistries
        .getAsJsonObject("minecraft:item")
        .getAsJsonObject("entries")
    Registries.registries[Registries.RegistryTypes.ITEM]?.forEach { (id, registry) ->
        registry as Item
        json.getAsJsonObject(id.toString()).get("protocol_id").asInt.let {
            itemById[it] = registry
            idByItem[registry] = it
        }
    }
}

val itemById = mutableMapOf<Int, Item>()
val idByItem = mutableMapOf<Item, Int>()