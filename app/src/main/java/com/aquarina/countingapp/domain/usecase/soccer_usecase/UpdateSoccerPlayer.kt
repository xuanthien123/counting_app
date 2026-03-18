package com.aquarina.countingapp.domain.usecase.soccer_usecase

import com.aquarina.countingapp.domain.model.InvalidPersonException
import com.aquarina.countingapp.domain.model.InvalidSoccerPlayerException
import com.aquarina.countingapp.domain.model.SoccerPlayer
import com.aquarina.countingapp.domain.repository.SoccerRepository
import kotlin.jvm.Throws

class UpdateSoccerPlayer(
    private val repository: SoccerRepository
) {
    @Throws(InvalidSoccerPlayerException::class)
    suspend operator fun invoke(soccerPlayer: SoccerPlayer) {
        if (soccerPlayer.name.isBlank()) {
            throw InvalidPersonException("Tên không được để trống!")
        }
        if (soccerPlayer.price <= 0) {
            throw InvalidPersonException("Giá không hợp lệ!")
        }
        repository.updateSoccerer(soccerPlayer)
    }
}