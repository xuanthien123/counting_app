package com.aquarina.countingapp.data.repository

import androidx.room.Transaction
import com.aquarina.countingapp.data.local.GameInfoDao
import com.aquarina.countingapp.data.local.GameSavedDao
import com.aquarina.countingapp.data.local.PersonDao
import com.aquarina.countingapp.data.local.UserTagDao
import com.aquarina.countingapp.domain.model.GameInfo
import com.aquarina.countingapp.domain.model.GameSaved
import com.aquarina.countingapp.domain.model.Person
import com.aquarina.countingapp.domain.model.UserTag
import com.aquarina.countingapp.domain.repository.PersonRepository
import kotlinx.coroutines.flow.Flow

class PersonRepositoryImpl(
    private val personDao: PersonDao,
    private val gameInfoDao: GameInfoDao,
    private val userTagDao: UserTagDao,
    private val gameSavedDao: GameSavedDao
) : PersonRepository {

    override fun getPersons(gameId: Int): Flow<List<Person>> {
        return personDao.getListPerson(gameId)
    }

    override suspend fun getPersonById(id: Int): Person? {
        return personDao.getPersonById(id)
    }

    override suspend fun insertPerson(person: Person) {
        personDao.insertPerson(person)
    }

    override suspend fun deletePerson(person: Person) {
        personDao.deletePerson(person)
    }

    override suspend fun deletePersonsByGameId(gameId: Int) {
        personDao.deletePersonsByGameId(gameId)
    }

    override suspend fun updatePerson(person: Person) {
        personDao.updatePerson(person)
    }

    override suspend fun getGameInfo(): GameInfo? {
        return gameInfoDao.getFirstGame()
    }

    override suspend fun addGameInfo(gameInfo: GameInfo) {
        gameInfoDao.insertGame(gameInfo)
    }

    @Transaction
    override suspend fun updateGameInfo(gameInfo: GameInfo) {
        gameInfoDao.updateGame(gameInfo)
    }

    override fun getUserTags(): Flow<List<UserTag>> {
        return userTagDao.getUserTags()
    }

    override suspend fun insertUserTag(userTag: UserTag) {
        userTagDao.insertUserTag(userTag)
    }

    override suspend fun deleteUserTag(userTag: UserTag) {
        userTagDao.deleteUserTag(userTag)
    }

    // GameSaved methods
    override fun getGames(): Flow<List<GameSaved>> {
        return gameSavedDao.getGames()
    }

    override suspend fun insertGame(game: GameSaved): Long {
        return gameSavedDao.insertGame(game)
    }

    override suspend fun updateGame(game: GameSaved) {
        gameSavedDao.updateGame(game)
    }

    override suspend fun deleteGame(game: GameSaved) {
        gameSavedDao.deleteGame(game)
    }

    override suspend fun getGameById(id: Int): GameSaved? {
        return gameSavedDao.getGameById(id)
    }
}
