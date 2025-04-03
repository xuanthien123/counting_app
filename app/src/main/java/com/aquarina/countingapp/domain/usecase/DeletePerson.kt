package com.aquarina.countingapp.domain.usecase

import com.aquarina.countingapp.domain.model.Person
import com.aquarina.countingapp.domain.repository.PersonRepository

class DeletePerson(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(person: Person) : Unit {
        repository.deletePerson(person)
    }
}