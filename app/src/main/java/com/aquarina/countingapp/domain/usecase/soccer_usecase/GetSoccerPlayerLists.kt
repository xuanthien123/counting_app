package com.aquarina.countingapp.domain.usecase.soccer_usecase

import com.aquarina.countingapp.domain.model.SoccerPlayerList
import com.aquarina.countingapp.domain.repository.SoccerRepository
import kotlinx.coroutines.flow.Flow

class GetSoccerPlayerLists(
    private val repository: SoccerRepository
) {
    operator fun invoke(): Flow<List<SoccerPlayerList>> {
        return repository.getSoccerPlayerLists()
    }
}
