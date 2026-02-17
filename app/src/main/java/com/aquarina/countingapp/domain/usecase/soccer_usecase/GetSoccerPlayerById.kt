package com.aquarina.countingapp.domain.usecase.soccer_usecase

import com.aquarina.countingapp.domain.model.SoccerPlayer
import com.aquarina.countingapp.domain.repository.SoccerRepository

class GetSoccerPlayerById(
    private val repository: SoccerRepository
) {
    suspend operator fun invoke(id: Int): SoccerPlayer? {
        return repository.getSoccererById(id)
    }
}