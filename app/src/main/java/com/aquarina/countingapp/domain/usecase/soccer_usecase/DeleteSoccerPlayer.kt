package com.aquarina.countingapp.domain.usecase.soccer_usecase

import com.aquarina.countingapp.domain.model.SoccerPlayer
import com.aquarina.countingapp.domain.repository.SoccerRepository

class DeleteSoccerPlayer(
    private val repository: SoccerRepository
) {
    suspend operator fun invoke(soccerPlayer: SoccerPlayer) {
        repository.deleteSoccerer(soccerPlayer)
    }
}