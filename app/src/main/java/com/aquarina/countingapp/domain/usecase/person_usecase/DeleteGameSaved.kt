package com.aquarina.countingapp.domain.usecase.person_usecase

import com.aquarina.countingapp.domain.model.GameSaved
import com.aquarina.countingapp.domain.repository.PersonRepository

class DeleteGameSaved(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(game: GameSaved) {
        // Also delete all persons associated with this game
        game.id?.let { repository.deletePersonsByGameId(it) }
        repository.deleteGame(game)
    }
}
