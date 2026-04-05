package com.aquarina.countingapp.data.local

import androidx.room.*
import com.aquarina.countingapp.domain.model.Person
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM Person WHERE gameSavedId = :gameSavedId")
    fun getListPerson(gameSavedId: Int) : Flow<List<Person>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: Person)

    @Query("SELECT * FROM Person WHERE id = :id")
    suspend fun getPersonById(id: Int): Person?

    @Delete
    suspend fun deletePerson(person: Person)

    @Query("DELETE FROM Person WHERE gameSavedId = :gameSavedId")
    suspend fun deletePersonsByGameId(gameSavedId: Int)

    @Update
    suspend fun updatePerson(person: Person)
}
