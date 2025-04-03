package com.aquarina.countingapp.domain.model

import androidx.collection.IntList
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.aquarina.countingapp.domain.converter.IntListConverter

@Entity
@TypeConverters(IntListConverter::class)  // Sử dụng TypeConverter
data class Person(
    var name: String,
    var total: Int,
    val stages: List<Int>,  // List<Int> cần TypeConverter
    @PrimaryKey val id: Int? = null
)

class InvalidPersonException(message: String) : Exception(message)
