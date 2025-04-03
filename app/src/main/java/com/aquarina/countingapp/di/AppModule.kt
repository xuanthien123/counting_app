package com.aquarina.countingapp.di

import android.app.Application
import androidx.room.Room
import com.aquarina.countingapp.data.local.PersonDatabase
import com.aquarina.countingapp.data.repository.PersonRepositoryImpl
import com.aquarina.countingapp.domain.repository.PersonRepository
import com.aquarina.countingapp.domain.usecase.DeleteAllPerson
import com.aquarina.countingapp.domain.usecase.DeletePerson
import com.aquarina.countingapp.domain.usecase.GetGameInfo
import com.aquarina.countingapp.domain.usecase.GetPersons
import com.aquarina.countingapp.domain.usecase.InsertGameInfo
import com.aquarina.countingapp.domain.usecase.InsertPerson
import com.aquarina.countingapp.domain.usecase.PersonUseCases
import com.aquarina.countingapp.domain.usecase.UpdateGameInfo
import com.aquarina.countingapp.domain.usecase.UpdatePerson
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

    @Provides
    @Singleton
    fun providePersonRepository(db: PersonDatabase): PersonRepository {
        return PersonRepositoryImpl(db.personDao, db.gameInfoDao)
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
            updateGameInfo = UpdateGameInfo(repository)
        )
    }
}