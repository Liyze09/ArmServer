package net.minecraftarm.registry

import net.minecraftarm.entity.Entity

fun entityRegistryInit() {
    val json = Registries.jsonRegistries
        .getAsJsonObject("minecraft:entity_type")
        .getAsJsonObject("entries")
    Registries.registries[Registries.RegistryTypes.ENTITY]?.forEach { (id, registry) ->
        registry as Entity
        json.getAsJsonObject(id.toString()).get("protocol_id").asInt.let {
            entityById[it] = registry
            idByEntity[registry] = it
        }
    }
}

val entityById = mutableMapOf<Int, Entity>()
val idByEntity = mutableMapOf<Entity, Int>()