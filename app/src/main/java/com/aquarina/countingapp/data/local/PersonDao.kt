package com.aquarina.countingapp.data.local

import androidx.room.*
import com.aquarina.countingapp.domain.model.GameInfo
import com.aquarina.countingapp.domain.model.Person
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM Person")
    fun getListPerson() : Flow<List<Person>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: Person) : Unit

    @Query("SELECT * FROM Person WHERE id = :id")
    suspend fun getPersonById(id: Int): Person?

    @Delete
    suspend fun deletePerson(person: Person)

    @Query("DELETE FROM Person")
    suspend fun deleteAllPerson()

    @Update
    suspend fun updatePerson(person: Person)
}