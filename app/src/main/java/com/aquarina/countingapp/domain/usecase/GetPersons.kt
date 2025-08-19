package com.aquarina.countingapp.domain.usecase

import com.aquarina.countingapp.domain.model.Person
import com.aquarina.countingapp.domain.repository.PersonRepository
import kotlinx.coroutines.flow.Flow

class GetPersons(
    private val repository: PersonRepository
) {
    operator fun invoke() : Flow<List<Person>> {
        return repository.getPersons()
    }
}