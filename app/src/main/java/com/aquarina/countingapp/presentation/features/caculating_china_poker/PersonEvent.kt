package com.aquarina.countingapp.presentation.features.caculating_china_poker

import com.aquarina.countingapp.domain.model.Person

sealed class PersonEvent {
    object OrderPersons : PersonEvent()
    data class UpdatePerson(val person: Person) : PersonEvent()
    data class DeletePerson(val person: Person) : PersonEvent()
    object RestorePerson : PersonEvent()
    data class CreatePerson(val person: Person) : PersonEvent()
    object DeleteAllPerson : PersonEvent()

}
