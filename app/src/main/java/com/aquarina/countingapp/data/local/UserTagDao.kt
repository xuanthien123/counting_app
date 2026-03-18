package com.aquarina.countingapp.data.local

import androidx.room.*
import com.aquarina.countingapp.domain.model.UserTag
import kotlinx.coroutines.flow.Flow

@Dao
interface UserTagDao {
    @Query("SELECT * FROM UserTag")
    fun getUserTags(): Flow<List<UserTag>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserTag(userTag: UserTag)

    @Delete
    suspend fun deleteUserTag(userTag: UserTag)
}
