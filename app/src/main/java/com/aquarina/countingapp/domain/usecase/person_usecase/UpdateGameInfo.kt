package com.aquarina.countingapp.domain.usecase.person_usecase

import com.aquarina.countingapp.domain.model.GameInfo
import com.aquarina.countingapp.domain.repository.PersonRepository

class UpdateGameInfo (
    private val repository: PersonRepository
) {
    suspend operator fun invoke(gameInfo: GameInfo) : Unit {
        repository.updateGameInfo(gameInfo)
    }
}