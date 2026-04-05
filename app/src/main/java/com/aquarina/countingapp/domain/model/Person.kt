package com.aquarina.countingapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.aquarina.countingapp.domain.converter.IntListConverter

@Entity
@TypeConverters(IntListConverter::class)
data class Person(
    var name: String,
    var total: Int,
    val stages: List<Int>,
    val gameSavedId: Int, // Link to GameSaved
    @PrimaryKey(autoGenerate = true) val id: Int? = null
)

class InvalidPersonException(message: String) : Exception(message)
