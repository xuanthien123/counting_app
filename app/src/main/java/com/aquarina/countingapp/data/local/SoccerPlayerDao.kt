package com.aquarina.countingapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aquarina.countingapp.domain.model.SoccerPlayer
import kotlinx.coroutines.flow.Flow

@Dao
interface SoccerPlayerDao {
    @Query("SELECT * FROM SoccerPlayer")
    fun getListSoccerPlayer() : Flow<List<SoccerPlayer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoccerPlayer(soccerPlayer: SoccerPlayer) : Unit

    @Query("SELECT * FROM SoccerPlayer WHERE id = :id")
    suspend fun getSoccerPlayerById(id: Int): SoccerPlayer?

    @Delete
    suspend fun deletePerson(soccerPlayer: SoccerPlayer)

    @Query("DELETE FROM SoccerPlayer")
    suspend fun deleteAllSoccerPlayer()

    @Update
    suspend fun updateSoccerPlayer(soccerPlayer: SoccerPlayer)
}