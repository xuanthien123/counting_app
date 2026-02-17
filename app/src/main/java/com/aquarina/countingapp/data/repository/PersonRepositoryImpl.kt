package com.aquarina.countingapp.data.repository

import androidx.room.Transaction
import com.aquarina.countingapp.data.local.GameInfoDao
import com.aquarina.countingapp.data.local.PersonDao
import com.aquarina.countingapp.domain.model.GameInfo
import com.aquarina.countingapp.domain.model.Person
import com.aquarina.countingapp.domain.repository.PersonRepository
import kotlinx.coroutines.flow.Flow

class PersonRepositoryImpl(
    private val personDao: PersonDao,
    private val gameInfoDao: GameInfoDao
) : PersonRepository {

    override fun getPersons(): Flow<List<Person>> {
        return personDao.getListPerson()
    }

    override suspend fun getPersonById(id: Int): Person? {
        return personDao.getPersonById(id)
    }

    override suspend fun insertPerson(person: Person) {
        personDao.insertPerson(person) // Bỏ `return`
    }

    override suspend fun deletePerson(person: Person) {
        personDao.deletePerson(person)
    }

    override suspend fun deleteAllPerson() {
        personDao.deleteAllPerson()
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
        gameInfoDao.updateGame(gameInfo) // Đánh dấu Transaction
    }
}
