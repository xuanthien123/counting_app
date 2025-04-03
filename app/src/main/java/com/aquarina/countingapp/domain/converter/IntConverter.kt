package com.aquarina.countingapp.domain.converter

import androidx.room.TypeConverter

class IntListConverter {
    @TypeConverter
    fun fromList(list: List<Int>): String {
        return list.joinToString(",")  // Convert List → String
    }

    @TypeConverter
    fun toList(data: String): List<Int> {
        return if (data.isEmpty()) emptyList() else data.split(",").map { it.toInt() }
    }
}