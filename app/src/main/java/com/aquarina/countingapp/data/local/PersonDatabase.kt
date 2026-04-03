package com.aquarina.countingapp.data.local

import androidx.room.*
import com.aquarina.countingapp.domain.converter.ConfigConverter
import com.aquarina.countingapp.domain.converter.IntListConverter
import com.aquarina.countingapp.domain.model.GameInfo
import com.aquarina.countingapp.domain.model.GameSaved
import com.aquarina.countingapp.domain.model.Person
import com.aquarina.countingapp.domain.model.UserTag

@Database(entities = [Person::class, GameInfo::class, UserTag::class, GameSaved::class], version = 9)
@TypeConverters(IntListConverter::class, ConfigConverter::class)
abstract class PersonDatabase : RoomDatabase() {
    abstract val personDao: PersonDao
    abstract val gameInfoDao: GameInfoDao
    abstract val userTagDao: UserTagDao
    abstract val gameSavedDao: GameSavedDao

    companion object {
        const val DATABASE_NAME = "persons_db"
    }
}
