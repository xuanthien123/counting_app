package com.aquarina.countingapp.domain.usecase.person_usecase

import com.aquarina.countingapp.domain.model.GameSaved
import com.aquarina.countingapp.domain.repository.PersonRepository
import kotlinx.coroutines.flow.Flow

class GetSavedGames(
    private val repository: PersonRepository
) {
    operator fun invoke(): Flow<List<GameSaved>> {
        return repository.getGames()
    }
}
