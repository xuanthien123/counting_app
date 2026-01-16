package com.aquarina.countingapp.data.repository

import com.aquarina.countingapp.data.local.SoccerPlayerDao
import com.aquarina.countingapp.domain.model.SoccerPlayer
import com.aquarina.countingapp.domain.repository.SoccerRepository
import kotlinx.coroutines.flow.Flow

class SoccerPlayerRepositoryImpl (
    private val soccerPlayerDao: SoccerPlayerDao
) : SoccerRepository {
    override fun getSoccerers(): Flow<List<SoccerPlayer>> {
        return soccerPlayerDao.getListSoccerPlayer()
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

}