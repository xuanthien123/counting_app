package com.aquarina.countingapp.domain.usecase

import com.aquarina.countingapp.domain.model.InvalidPersonException
import com.aquarina.countingapp.domain.model.Person
import com.aquarina.countingapp.domain.repository.PersonRepository
import kotlin.jvm.Throws

class InsertPerson (
    private val repository: PersonRepository
) {
    @Throws(InvalidPersonException::class)
    suspend operator fun invoke(person: Person) : Unit {
        if ((person.name?.isBlank()) == true) {
            throw InvalidPersonException("Tên không được để trống!")
        }
        repository.insertPerson(person)
    }
}