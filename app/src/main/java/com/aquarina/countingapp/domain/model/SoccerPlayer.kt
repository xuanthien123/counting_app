package com.aquarina.countingapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity
data class SoccerPlayer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Int,
    val note: String,
    val timestamp: Long = System.currentTimeMillis()
)
class InvalidSoccerPlayerException(message: String) : Exception(message)