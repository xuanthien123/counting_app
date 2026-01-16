package com.aquarina.countingapp.presentation.features.soccer_player_manager

import com.aquarina.countingapp.domain.model.SoccerPlayer
import com.aquarina.countingapp.domain.util.SoccerPlayerOrder

sealed class SoccerPlayerManagerEvent {
    data class Order(val soccerPlayerOrder: SoccerPlayerOrder) : SoccerPlayerManagerEvent()
    data class DeleteSoccerPlayer(val soccerPlayer: SoccerPlayer) : SoccerPlayerManagerEvent()
    data class AddSoccerPlayer(val soccerPlayer: SoccerPlayer) : SoccerPlayerManagerEvent()
    data class EditSoccerPlayer(val soccerPlayer: SoccerPlayer) : SoccerPlayerManagerEvent()
    object RestoreSoccerPlayer : SoccerPlayerManagerEvent()
    object ToggleOrderSection : SoccerPlayerManagerEvent()
    object OpenAddPlayerDialog : SoccerPlayerManagerEvent()
    data class OpenEditPlayerDialog(val soccerPlayer: SoccerPlayer) : SoccerPlayerManagerEvent()
    object CloseDialogAddOrEdit : SoccerPlayerManagerEvent()
    object CloseDialogConfirm : SoccerPlayerManagerEvent()
    data class OpenConfirmDialog(val title: String = "Xác nhận", val description: String, val function: () -> Unit) : SoccerPlayerManagerEvent()
}