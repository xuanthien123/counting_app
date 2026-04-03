package com.aquarina.countingapp.data.repository

import com.aquarina.countingapp.data.local.SoccerPlayerDao
import com.aquarina.countingapp.domain.model.SoccerPlayer
import com.aquarina.countingapp.domain.model.SoccerPlayerList
import com.aquarina.countingapp.domain.repository.SoccerRepository
import kotlinx.coroutines.flow.Flow

class SoccerPlayerRepositoryImpl (
    private val soccerPlayerDao: SoccerPlayerDao
) : SoccerRepository {
    override fun getSoccerPlayerLists(): Flow<List<SoccerPlayerList>> {
        return soccerPlayerDao.getAllSoccerPlayerLists()
    }

    override suspend fun getSoccerPlayerListById(id: Int): SoccerPlayerList? {
        return soccerPlayerDao.getSoccerPlayerListById(id)
    }

    override suspend fun insertSoccerPlayerList(soccerPlayerList: SoccerPlayerList): Long {
        return soccerPlayerDao.insertSoccerPlayerList(soccerPlayerList)
    }

    override suspend fun deleteSoccerPlayerList(soccerPlayerList: SoccerPlayerList) {
        soccerPlayerDao.deleteSoccerPlayerList(soccerPlayerList)
    }

    override fun getSoccerers(listId: Int): Flow<List<SoccerPlayer>> {
        return soccerPlayerDao.getListSoccerPlayer(listId)
    }

    override suspend fun getSoccererById(id: Int): SoccerPlayer? {
        return soccerPlayerDao.getSoccerPlayerById(id)
    }

    override suspend fun insertSoccerer(soccerPlayer: SoccerPlayer) {
        soccerPlayerDao.insertSoccerPlayer(soccerPlayer)
    }

    override suspend fun deleteSoccerer(soccerPlayer: SoccerPlayer) {
        soccerPlayerDao.deletePerson(soccerPlayer)
    }

    override suspend fun updateSoccerer(soccerPlayer: SoccerPlayer) {
        soccerPlayerDao.updateSoccerPlayer(soccerPlayer)
    }

    override suspend fun deleteAllSoccerPlayerInList(listId: Int) {
        soccerPlayerDao.deleteAllSoccerPlayerInList(listId)
    }
}
