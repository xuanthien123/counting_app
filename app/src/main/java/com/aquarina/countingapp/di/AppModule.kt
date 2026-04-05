package com.aquarina.countingapp.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aquarina.countingapp.data.local.PersonDatabase
import com.aquarina.countingapp.data.local.SoccerPlayerDatabase
import com.aquarina.countingapp.data.local.SoccerPreferences
import com.aquarina.countingapp.data.repository.PersonRepositoryImpl
import com.aquarina.countingapp.data.repository.SoccerPlayerRepositoryImpl
import com.aquarina.countingapp.domain.repository.PersonRepository
import com.aquarina.countingapp.domain.repository.SoccerRepository
import com.aquarina.countingapp.domain.usecase.person_usecase.*
import com.aquarina.countingapp.domain.usecase.soccer_usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePersonDatabase(app: Application): PersonDatabase {
        return Room.databaseBuilder(
            app,
            PersonDatabase::class.java,
            PersonDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `SoccerPlayerList` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)"
            )
            db.execSQL(
                "ALTER TABLE `SoccerPlayer` ADD COLUMN `listId` INTEGER NOT NULL DEFAULT 1"
            )
            db.execSQL(
                "INSERT OR IGNORE INTO `SoccerPlayerList` (id, name, timestamp) VALUES (1, 'Danh sách mặc định', ${System.currentTimeMillis()})"
            )
        }
    }

    @Provides
    @Singleton
    fun provideSoccerPlayerDatabase(app: Application): SoccerPlayerDatabase {
        return Room.databaseBuilder(
            app,
            SoccerPlayerDatabase::class.java,
            SoccerPlayerDatabase.DATABASE_NAME
        )
            .addMigrations(MIGRATION_3_4)
            .build()
    }

    @Provides
    @Singleton
    fun providePersonRepository(db: PersonDatabase): PersonRepository {
        return PersonRepositoryImpl(db.personDao, db.gameInfoDao, db.userTagDao, db.gameSavedDao)
    }

    @Provides
    @Singleton
    fun provideSoccerPlayerRepository(db: SoccerPlayerDatabase): SoccerRepository {
        return SoccerPlayerRepositoryImpl(db.soccerPlayerDao)
    }

    @Provides
    @Singleton
    fun provideSoccerPreferences(app: Application): SoccerPreferences {
        return SoccerPreferences(app)
    }

    @Provides
    @Singleton
    fun providePersonUseCases(repository: PersonRepository): PersonUseCases {
        return PersonUseCases(
            getPersons = GetPersons(repository),
            deletePerson = DeletePerson(repository),
            insertPerson = InsertPerson(repository),
            deleteAllPerson = DeleteAllPerson(repository),
            updatePerson = UpdatePerson(repository),
            getGameInfo = GetGameInfo(repository),
            insertGameInfo = InsertGameInfo(repository),
            updateGameInfo = UpdateGameInfo(repository),
            getUserTags = GetUserTags(repository),
            insertUserTag = InsertUserTag(repository),
            deleteUserTag = DeleteUserTag(repository),
            getSavedGames = GetSavedGames(repository),
            insertGameSaved = InsertGameSaved(repository),
            deleteGameSaved = DeleteGameSaved(repository),
            updateGameSaved = UpdateGameSaved(repository)
        )
    }

    @Provides
    @Singleton
    fun provideSoccerPlayerUseCases(repository: SoccerRepository): SoccerUseCases {
        return SoccerUseCases(
            getSoccerPlayer = GetSoccerPlayer(repository),
            getSSoccerPlayerById = GetSoccerPlayerById(repository),
            insertSoccerPlayer = InsertSoccerPlayer(repository),
            deleteSoccerPlayer = DeleteSoccerPlayer(repository),
            updateSoccerPlayer = UpdateSoccerPlayer(repository),
            getSoccerPlayerLists = GetSoccerPlayerLists(repository),
            insertSoccerPlayerList = InsertSoccerPlayerList(repository),
            deleteSoccerPlayerList = DeleteSoccerPlayerList(repository)
        )
    }
}
