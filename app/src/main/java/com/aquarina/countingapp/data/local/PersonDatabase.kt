package com.aquarina.countingapp.data.local

import androidx.room.*
import com.aquarina.countingapp.domain.converter.IntListConverter
import com.aquarina.countingapp.domain.model.GameInfo
import com.aquarina.countingapp.domain.model.Person
import com.aquarina.countingapp.domain.model.UserTag

@Database(entities = [Person::class, GameInfo::class, UserTag::class], version = 3) // Tăng version
@TypeConverters(IntListConverter::class)
abstract class PersonDatabase : RoomDatabase() {
    abstract val personDao: PersonDao
    abstract val gameInfoDao: GameInfoDao
    abstract val userTagDao: UserTagDao

    companion object {
        const val DATABASE_NAME = "persons_db"
    }
}