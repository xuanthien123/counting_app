package com.aquarina.countingapp.domain.usecase.person_usecase

import com.aquarina.countingapp.domain.model.GameSaved
import com.aquarina.countingapp.domain.repository.PersonRepository

class InsertGameSaved(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(game: GameSaved): Long {
        return repository.insertGame(game)
    }
}
