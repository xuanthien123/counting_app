package com.aquarina.countingapp.domain.usecase.person_usecase

data class PersonUseCases(
    val getPersons: GetPersons,
    val insertPerson: InsertPerson,
    val deletePerson: DeletePerson,
    val deleteAllPerson: DeleteAllPerson,
    val updatePerson: UpdatePerson,
    val getGameInfo: GetGameInfo,
    val insertGameInfo: InsertGameInfo,
    val updateGameInfo: UpdateGameInfo
)
