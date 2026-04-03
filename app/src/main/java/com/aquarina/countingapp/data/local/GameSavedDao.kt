package com.aquarina.countingapp.data.local

import androidx.room.*
import com.aquarina.countingapp.domain.model.GameSaved
import kotlinx.coroutines.flow.Flow

@Dao
interface GameSavedDao {
    @Query("SELECT * FROM GameSaved ORDER BY createdAt DESC")
    fun getGames(): Flow<List<GameSaved>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameSaved): Long

    @Update
    suspend fun updateGame(game: GameSaved)

    @Delete
    suspend fun deleteGame(game: GameSaved)

    @Query("SELECT * FROM GameSaved WHERE id = :id")
    suspend fun getGameById(id: Int): GameSaved?
}
