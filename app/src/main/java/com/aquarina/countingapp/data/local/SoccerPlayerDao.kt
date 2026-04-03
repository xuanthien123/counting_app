package com.aquarina.countingapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aquarina.countingapp.domain.model.SoccerPlayer
import com.aquarina.countingapp.domain.model.SoccerPlayerList
import kotlinx.coroutines.flow.Flow

@Dao
interface SoccerPlayerDao {
    // SoccerPlayerList operations
    @Query("SELECT * FROM SoccerPlayerList")
    fun getAllSoccerPlayerLists(): Flow<List<SoccerPlayerList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoccerPlayerList(soccerPlayerList: SoccerPlayerList): Long

    @Delete
    suspend fun deleteSoccerPlayerList(soccerPlayerList: SoccerPlayerList)

    @Query("SELECT * FROM SoccerPlayerList WHERE id = :id")
    suspend fun getSoccerPlayerListById(id: Int): SoccerPlayerList?

    // SoccerPlayer operations filtered by listId
    @Query("SELECT * FROM SoccerPlayer WHERE listId = :listId")
    fun getListSoccerPlayer(listId: Int): Flow<List<SoccerPlayer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoccerPlayer(soccerPlayer: SoccerPlayer)

    @Query("SELECT * FROM SoccerPlayer WHERE id = :id")
    suspend fun getSoccerPlayerById(id: Int): SoccerPlayer?

    @Delete
    suspend fun deletePerson(soccerPlayer: SoccerPlayer)

    @Query("DELETE FROM SoccerPlayer WHERE listId = :listId")
    suspend fun deleteAllSoccerPlayerInList(listId: Int)

    @Update
    suspend fun updateSoccerPlayer(soccerPlayer: SoccerPlayer)
}
