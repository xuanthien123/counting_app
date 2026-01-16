package com.aquarina.countingapp.domain.usecase.person_usecase

import com.aquarina.countingapp.domain.model.GameInfo
import com.aquarina.countingapp.domain.model.InvalidPersonException
import com.aquarina.countingapp.domain.repository.PersonRepository
import kotlin.jvm.Throws


class InsertGameInfo (
    private val repository: PersonRepository
) {
    @Throws(InvalidPersonException::class)
    suspend operator fun invoke(gameInfo: GameInfo) : Unit {
        repository.addGameInfo(gameInfo)
    }
}