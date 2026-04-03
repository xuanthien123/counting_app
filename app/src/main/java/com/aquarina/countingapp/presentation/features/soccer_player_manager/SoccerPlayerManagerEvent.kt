package com.aquarina.countingapp.presentation.features.soccer_player_manager

import com.aquarina.countingapp.domain.model.SoccerPlayer
import com.aquarina.countingapp.domain.model.SoccerPlayerList
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
    
    // List management events
    data class SelectList(val listId: Int) : SoccerPlayerManagerEvent()
    data class AddSoccerPlayerList(val name: String) : SoccerPlayerManagerEvent()
    data class UpdateSoccerPlayerList(val soccerPlayerList: SoccerPlayerList) : SoccerPlayerManagerEvent()
    data class DeleteSoccerPlayerList(val soccerPlayerList: SoccerPlayerList) : SoccerPlayerManagerEvent()
}
