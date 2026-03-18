package com.aquarina.countingapp.di

import android.app.Application
import androidx.room.Room
import com.aquarina.countingapp.data.local.PersonDatabase
import com.aquarina.countingapp.data.local.SoccerPlayerDatabase
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
        // Build Person Database
        return Room.databaseBuilder(
            app,
            PersonDatabase::class.java,
            PersonDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideSoccerPlayerDatabase(app: Application): SoccerPlayerDatabase {
        // Build Soccer Database
        return Room.databaseBuilder(
            app,
            SoccerPlayerDatabase::class.java,
            SoccerPlayerDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun providePersonRepository(db: PersonDatabase): PersonRepository {
        return PersonRepositoryImpl(db.personDao, db.gameInfoDao, db.userTagDao)
    }

    @Provides
    @Singleton
    fun provideSoccerPlayerRepository(db: SoccerPlayerDatabase): SoccerRepository {
        return SoccerPlayerRepositoryImpl(db.soccerPlayerDao)
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
            deleteUserTag = DeleteUserTag(repository)
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
            updateSoccerPlayer = UpdateSoccerPlayer(repository)
        )
    }
}