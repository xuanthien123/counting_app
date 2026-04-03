package com.aquarina.countingapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SoccerPlayerList(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val timestamp: Long = System.currentTimeMillis()
)
