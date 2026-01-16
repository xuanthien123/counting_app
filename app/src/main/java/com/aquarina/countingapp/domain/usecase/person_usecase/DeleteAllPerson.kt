package com.aquarina.countingapp.domain.usecase.person_usecase

import com.aquarina.countingapp.domain.repository.PersonRepository

class DeleteAllPerson(
    private val repository: PersonRepository
) {
    suspend operator fun invoke() : Unit {
        repository.deleteAllPerson()
    }
}