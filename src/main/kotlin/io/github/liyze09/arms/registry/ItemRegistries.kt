package io.github.liyze09.arms.registry

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

class Item(val settings: ItemSettings) : Registries.Registry() {}
class ItemSettings {
    var maxCount: Int = 64
    var fireproof: Boolean = false
    fun maxCount(maxStackSize: Int): ItemSettings {
        this.maxCount = maxStackSize
        return this
    }

    fun fireproof(fireproof: Boolean): ItemSettings {
        this.fireproof = fireproof
        return this
    }
}

val itemById = mutableMapOf<Int, Item>()
val idByItem = mutableMapOf<Item, Int>()