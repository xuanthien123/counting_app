package com.aquarina.countingapp.domain.repository

import com.aquarina.countingapp.domain.model.SoccerPlayer
import com.aquarina.countingapp.domain.model.SoccerPlayerList
import kotlinx.coroutines.flow.Flow

interface SoccerRepository {
    // SoccerPlayerList operations
    fun getSoccerPlayerLists(): Flow<List<SoccerPlayerList>>
    suspend fun getSoccerPlayerListById(id: Int): SoccerPlayerList?
    suspend fun insertSoccerPlayerList(soccerPlayerList: SoccerPlayerList): Long
    suspend fun deleteSoccerPlayerList(soccerPlayerList: SoccerPlayerList)

    // SoccerPlayer operations
    fun getSoccerers(listId: Int): Flow<List<SoccerPlayer>>
    suspend fun getSoccererById(id: Int): SoccerPlayer?
    suspend fun insertSoccerer(soccerPlayer: SoccerPlayer)
    suspend fun deleteSoccerer(soccerPlayer: SoccerPlayer)
    suspend fun updateSoccerer(soccerPlayer: SoccerPlayer)
    suspend fun deleteAllSoccerPlayerInList(listId: Int)
}
