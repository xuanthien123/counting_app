package com.aquarina.countingapp.domain.repository

import com.aquarina.countingapp.domain.model.GameInfo
import com.aquarina.countingapp.domain.model.Person
import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    fun getPersons() : Flow<List<Person>>

    suspend fun getPersonById(id: Int) : Person?

    suspend fun insertPerson(person: Person)

    suspend fun deletePerson(person: Person)

    suspend fun deleteAllPerson()

    suspend fun updatePerson(person: Person)

    suspend fun getGameInfo() : GameInfo?

    suspend fun addGameInfo(gameInfo: GameInfo)

    suspend fun updateGameInfo(gameInfo: GameInfo)
}