package com.aquarina.countingapp.domain.usecase.soccer_usecase

import com.aquarina.countingapp.domain.model.SoccerPlayerList
import com.aquarina.countingapp.domain.repository.SoccerRepository

class InsertSoccerPlayerList(
    private val repository: SoccerRepository
) {
    suspend operator fun invoke(soccerPlayerList: SoccerPlayerList): Long {
        if (soccerPlayerList.name.isBlank()) {
            throw Exception("Tên danh sách không được để trống!")
        }
        return repository.insertSoccerPlayerList(soccerPlayerList)
    }
}
