package com.aquarina.countingapp.domain.repository

import com.aquarina.countingapp.domain.model.GameInfo
import com.aquarina.countingapp.domain.model.GameSaved
import com.aquarina.countingapp.domain.model.Person
import com.aquarina.countingapp.domain.model.UserTag
import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    fun getPersons(gameId: Int) : Flow<List<Person>>

    suspend fun getPersonById(id: Int) : Person?

    suspend fun insertPerson(person: Person)

    suspend fun deletePerson(person: Person)

    suspend fun deletePersonsByGameId(gameId: Int)

    suspend fun updatePerson(person: Person)

    suspend fun getGameInfo() : GameInfo?

    suspend fun addGameInfo(gameInfo: GameInfo)

    suspend fun updateGameInfo(gameInfo: GameInfo)

    fun getUserTags(): Flow<List<UserTag>>

    suspend fun insertUserTag(userTag: UserTag)

    suspend fun deleteUserTag(userTag: UserTag)

    // GameSaved methods
    fun getGames(): Flow<List<GameSaved>>

    suspend fun insertGame(game: GameSaved): Long

    suspend fun updateGame(game: GameSaved)

    suspend fun deleteGame(game: GameSaved)

    suspend fun getGameById(id: Int): GameSaved?
}
