package com.aquarina.countingapp.domain.converter

import androidx.room.TypeConverter
import com.aquarina.countingapp.domain.model.SoundConfig
import com.aquarina.countingapp.domain.model.MilestoneConfig
import com.aquarina.countingapp.domain.model.AchievementConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ConfigConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromSoundConfigList(value: List<SoundConfig>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toSoundConfigList(value: String?): List<SoundConfig>? {
        val listType = object : TypeToken<List<SoundConfig>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromMilestoneConfigList(value: List<MilestoneConfig>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMilestoneConfigList(value: String?): List<MilestoneConfig>? {
        val listType = object : TypeToken<List<MilestoneConfig>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromAchievementConfigList(value: List<AchievementConfig>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toAchievementConfigList(value: String?): List<AchievementConfig>? {
        val listType = object : TypeToken<List<AchievementConfig>>() {}.type
        return gson.fromJson(value, listType)
    }
}
