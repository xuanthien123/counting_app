package com.aquarina.countingapp.data.local

import androidx.room.*
import com.aquarina.countingapp.domain.converter.IntListConverter
import com.aquarina.countingapp.domain.model.GameInfo
import com.aquarina.countingapp.domain.model.Person

@Database(entities = [Person::class, GameInfo::class], version = 2) // Tăng version
@TypeConverters(IntListConverter::class)
abstract class PersonDatabase : RoomDatabase() {
    abstract val personDao: PersonDao
    abstract val gameInfoDao: GameInfoDao  // Thêm DAO mới

    companion object {
        const val DATABASE_NAME = "persons_db"
    }
}