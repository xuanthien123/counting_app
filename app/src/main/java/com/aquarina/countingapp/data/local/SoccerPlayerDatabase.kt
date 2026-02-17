package com.aquarina.countingapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aquarina.countingapp.domain.converter.IntListConverter
import com.aquarina.countingapp.domain.model.SoccerPlayer

@Database(entities = [SoccerPlayer::class], version = 1) // Tăng version
@TypeConverters(IntListConverter::class)
abstract class SoccerPlayerDatabase : RoomDatabase() {
    abstract val soccerPlayerDao: SoccerPlayerDao

    companion object {
        const val DATABASE_NAME = "soccer_db"
    }
}