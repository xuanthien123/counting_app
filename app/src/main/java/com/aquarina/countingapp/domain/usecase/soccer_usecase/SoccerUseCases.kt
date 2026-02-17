package com.aquarina.countingapp.domain.usecase.soccer_usecase

data class SoccerUseCases (
    val getSoccerPlayer: GetSoccerPlayer,
    val getSSoccerPlayerById: GetSoccerPlayerById,
    val insertSoccerPlayer: InsertSoccerPlayer,
    val deleteSoccerPlayer: DeleteSoccerPlayer,
    val updateSoccerPlayer: UpdateSoccerPlayer
)
