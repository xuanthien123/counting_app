package com.aquarina.countingapp.domain.usecase

import com.aquarina.countingapp.domain.model.GameInfo
import com.aquarina.countingapp.domain.repository.PersonRepository

class GetGameInfo(
    private val repository: PersonRepository
) {
    suspend operator fun invoke() : GameInfo? {
        return repository.getGameInfo()
    }
}