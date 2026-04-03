package com.aquarina.countingapp.domain.usecase.soccer_usecase

import com.aquarina.countingapp.domain.model.SoccerPlayerList
import com.aquarina.countingapp.domain.repository.SoccerRepository

class DeleteSoccerPlayerList(
    private val repository: SoccerRepository
) {
    suspend operator fun invoke(soccerPlayerList: SoccerPlayerList) {
        // Also delete all players in this list
        repository.deleteAllSoccerPlayerInList(soccerPlayerList.id)
        repository.deleteSoccerPlayerList(soccerPlayerList)
    }
}
