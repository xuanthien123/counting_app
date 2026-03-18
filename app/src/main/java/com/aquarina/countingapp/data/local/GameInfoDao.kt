package com.aquarina.countingapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aquarina.countingapp.domain.model.GameInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface GameInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(gameInfo: GameInfo)

    @Query("SELECT * FROM GameInfo LIMIT 1")
    suspend fun getFirstGame(): GameInfo?

    @Update
    suspend fun updateGame(gameInfo: GameInfo)

    @Query("SELECT * FROM GameInfo")
    fun getAllGames(): Flow<List<GameInfo>>
}