package com.aquarina.countingapp.domain.usecase.person_usecase

import com.aquarina.countingapp.domain.model.GameSaved
import com.aquarina.countingapp.domain.repository.PersonRepository

class UpdateGameSaved(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(game: GameSaved) {
        repository.updateGame(game)
    }
}
