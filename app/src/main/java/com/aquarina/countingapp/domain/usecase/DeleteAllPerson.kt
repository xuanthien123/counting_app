package com.aquarina.countingapp.domain.usecase

import com.aquarina.countingapp.domain.model.Person
import com.aquarina.countingapp.domain.repository.PersonRepository

class DeleteAllPerson(
    private val repository: PersonRepository
) {
    suspend operator fun invoke() : Unit {
        repository.deleteAllPerson()
    }
}