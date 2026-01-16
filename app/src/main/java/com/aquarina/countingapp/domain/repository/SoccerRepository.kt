package com.aquarina.countingapp.domain.repository

import com.aquarina.countingapp.domain.model.SoccerPlayer
import kotlinx.coroutines.flow.Flow

interface SoccerRepository {
    fun getSoccerers() : Flow<List<SoccerPlayer>>

    suspend fun getSoccererById(id: Int) : SoccerPlayer?

    suspend fun insertSoccerer(soccerPlayer: SoccerPlayer)

    suspend fun deleteSoccerer(soccerPlayer: SoccerPlayer)

    suspend fun updateSoccerer(soccerPlayer: SoccerPlayer)
}