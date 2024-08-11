package net.minecraftarm.world.gen

import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import net.minecraftarm.common.Identifier
import net.minecraftarm.nbt.*
import net.minecraftarm.registry.Registries
import net.minecraftarm.registry.block.gson
import java.io.FileNotFoundException


data class Biome(
    val effects: Effects,
    @SerializedName("has_precipitation")
    val hasPrecipitation: Byte,
    val temperature: Float,
    val downfall: Float,
    @SerializedName("temperature_modifier")
    val temperatureModifier: String?,
) : Registries.Registry {

    override fun toNbt(): NbtCompound {
        val root = mutableMapOf(
            "has_precipitation" to NbtByte(hasPrecipitation),
            "temperature" to NbtFloat(temperature),
            "downfall" to NbtFloat(downfall),
        )
        if (temperatureModifier != null) root["temperature_modifier"] = NbtString(temperatureModifier)
        val effectsMap = mutableMapOf<String, NbtTag>(
            "sky_color" to NbtInt(effects.skyColor),
            "water_fog_color" to NbtInt(effects.waterFogColor),
            "water_color" to NbtInt(effects.waterColor),
            "fog_color" to NbtInt(effects.fogColor),
        )
        if (effects.grassColorModifier != null) effectsMap["grass_color_modifier"] =
            NbtString(effects.grassColorModifier)
        if (effects.grassColor != null) effectsMap["grass_color"] = NbtInt(effects.grassColor)
        if (effects.foliageColor != null) effectsMap["foliage_color"] = NbtInt(effects.foliageColor)
        if (effects.music != null) effectsMap["music"] = NbtCompound(
            "replace_current_music" to NbtByte(effects.music.replaceCurrentMusic),
            "max_delay" to NbtInt(effects.music.maxDelay),
            "min_delay" to NbtInt(effects.music.minDelay),
            "sound" to NbtString(effects.music.sound),
        )
        if (effects.moodSound != null) effectsMap["mood_sound"] = NbtCompound(
            "tick_delay" to NbtInt(effects.moodSound.tickDelay),
            "offset" to NbtDouble(effects.moodSound.offset),
            "sound" to NbtString(effects.moodSound.sound),
            "block_search_extent" to NbtInt(effects.moodSound.blockSearchExtent),
        )
        if (effects.particle != null) effectsMap["particle"] = NbtCompound(
            "probability" to NbtFloat(effects.particle.probability),
            "options" to NbtCompound(
                "type" to NbtString(effects.particle.options.type),
            )
        )
        if (effects.ambientSound != null) effectsMap["ambient_sound"] = NbtString(effects.ambientSound)
        if (effects.additionsSound != null) {
            val it = mutableMapOf<String, NbtTag>(
                "sound_id" to NbtString(effects.additionsSound.sound),
            )
            if (effects.additionsSound.tickChance != null) it["tick_chance"] =
                NbtDouble(effects.additionsSound.tickChance)
        }
        root["effects"] = NbtCompound(effectsMap)
        return NbtCompound(root)
    }

    data class Effects(
        @SerializedName("sky_color")
        val skyColor: Int, @SerializedName("water_fog_color")
        val waterFogColor: Int, @SerializedName("water_color")
        val waterColor: Int, @SerializedName("fog_color")
        val fogColor: Int, @SerializedName("grass_color_modifier")

        val grassColorModifier: String?,
        val music: Music?, @SerializedName("grass_color")
        val grassColor: Int?, @SerializedName("foliage_color")
        val foliageColor: Int?, @SerializedName("mood_sound")
        val moodSound: MoodSound?, @SerializedName("additions_sound")
        val additionsSound: AdditionsSounds?, @SerializedName("ambient_sound")
        val ambientSound: String?,
        val particle: Particles?
    )

    data class Music(
        @SerializedName("replace_current_music")
        val replaceCurrentMusic: Byte,
        @SerializedName("max_delay")
        val maxDelay: Int,
        val sound: String,
        @SerializedName("min_delay")
        val minDelay: Int,
    )

    data class MoodSound(
        @SerializedName("tick_delay")
        val tickDelay: Int,
        val offset: Double,
        val sound: String,
        @SerializedName("block_search_extent")
        val blockSearchExtent: Int,
    )

    data class AdditionsSounds(
        @SerializedName("sound")
        val sound: String, @SerializedName("tick_chance")
        val tickChance: Double?
    )

    data class Particles(
        val probability: Float,
        val options: ParticleOptions
    )

    open class ParticleOptions(
        val type: String,
    )

    companion object {
        val biomes: Map<Identifier, Biome> = mutableMapOf()

        init {
            biomes as MutableMap<Identifier, Biome>
            JsonParser.parseReader(
                Biome::class.java.getResourceAsStream("/registries/biomes.json")
                    ?.bufferedReader() ?: throw FileNotFoundException("Failed to load biomes.json")
            ).asJsonObject.entrySet().forEach { (id, json) ->
                biomes[Identifier(id)] = gson.fromJson(json, Biome::class.java)
            }
        }
    }
}
