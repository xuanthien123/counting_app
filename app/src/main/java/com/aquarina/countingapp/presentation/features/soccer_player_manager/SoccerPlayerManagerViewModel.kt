package com.aquarina.countingapp.presentation.features.soccer_player_manager

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquarina.countingapp.data.local.SoccerPreferences
import com.aquarina.countingapp.domain.model.SoccerPlayer
import com.aquarina.countingapp.domain.model.SoccerPlayerList
import com.aquarina.countingapp.domain.usecase.soccer_usecase.SoccerUseCases
import com.aquarina.countingapp.domain.util.OrderType
import com.aquarina.countingapp.domain.util.SoccerPlayerOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoccerPlayerManagerViewModel @Inject constructor(
    private val soccerUseCases: SoccerUseCases,
    private val preferences: SoccerPreferences
) : ViewModel() {
    private val _state = mutableStateOf(SoccerPlayerManagerState())
    val state: State<SoccerPlayerManagerState> = _state
    private var deletedSoccerPlayer: SoccerPlayer? = null
    private var getSoccerPlayersJob: Job? = null
    private var getSoccerPlayerListsJob: Job? = null

    private val _showDialogAddOrEdit = mutableStateOf(false)
    val showDialogAddOrEdit: State<Boolean> = _showDialogAddOrEdit
    private val _showDialogConfirm = mutableStateOf(false)
    val showDialogConfirm: State<Boolean> = _showDialogConfirm
    var title: String = "Xác nhận"
    var content: String = ""
    var function: () -> Unit = {}

    private var _selectingPlayer: MutableState<SoccerPlayer?> = mutableStateOf(null)
    val selectingPlayer: State<SoccerPlayer?> = _selectingPlayer

    init {
        val savedOrder = preferences.getOrder()
        val savedListId = preferences.getSelectedListId()
        _state.value = _state.value.copy(
            soccerPlayerOrder = savedOrder,
            selectedListId = savedListId
        )
        getSoccerPlayerLists()
    }

    private fun getSoccerPlayerLists() {
        getSoccerPlayerListsJob?.cancel()
        getSoccerPlayerListsJob = soccerUseCases.getSoccerPlayerLists().onEach { lists ->
            if (lists.isEmpty()) {
                // If no lists exist (first time or update), create a default one
                val defaultListId = soccerUseCases.insertSoccerPlayerList(SoccerPlayerList(id = 1, name = "Danh sách mặc định"))
                _state.value = state.value.copy(
                    soccerPlayerLists = listOf(SoccerPlayerList(id = defaultListId.toInt(), name = "Danh sách mặc định")),
                    selectedListId = defaultListId.toInt()
                )
                preferences.saveSelectedListId(defaultListId.toInt())
            } else {
                val savedListId = state.value.selectedListId
                val finalId = if (lists.any { it.id == savedListId }) savedListId else lists.first().id
                
                _state.value = state.value.copy(
                    soccerPlayerLists = lists,
                    selectedListId = finalId
                )
                if (finalId != savedListId) {
                    preferences.saveSelectedListId(finalId)
                }
            }
            getSoccerPlayers(state.value.selectedListId, state.value.soccerPlayerOrder)
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: SoccerPlayerManagerEvent) {
        when(event) {
            is SoccerPlayerManagerEvent.RestoreSoccerPlayer -> {
                viewModelScope.launch {
                    soccerUseCases.insertSoccerPlayer(deletedSoccerPlayer ?: return@launch)
                    deletedSoccerPlayer = null
                }
            }
            is SoccerPlayerManagerEvent.Order -> {
                if (state.value.soccerPlayerOrder::class == event.soccerPlayerOrder::class &&
                    state.value.soccerPlayerOrder.orderType == event.soccerPlayerOrder.orderType
                ) {
                    return
                }
                preferences.saveOrder(event.soccerPlayerOrder)
                getSoccerPlayers(state.value.selectedListId, event.soccerPlayerOrder)
            }
            is SoccerPlayerManagerEvent.DeleteSoccerPlayer -> {
                viewModelScope.launch {
                    soccerUseCases.deleteSoccerPlayer(event.soccerPlayer)
                    deletedSoccerPlayer = event.soccerPlayer
                }
            }
            is SoccerPlayerManagerEvent.ToggleOrderSection -> {
                _state.value.setOrderSectionVisible(!state.value.isOrderSectionVisible.targetState)
            }
            is SoccerPlayerManagerEvent.AddSoccerPlayer -> {
                viewModelScope.launch {
                    soccerUseCases.insertSoccerPlayer(event.soccerPlayer.copy(listId = state.value.selectedListId))
                }
            }
            is SoccerPlayerManagerEvent.EditSoccerPlayer -> {
                viewModelScope.launch {
                    soccerUseCases.updateSoccerPlayer(event.soccerPlayer)
                }
            }
            is SoccerPlayerManagerEvent.OpenAddPlayerDialog -> {
                _selectingPlayer.value = null
                _showDialogAddOrEdit.value = true
            }
            is SoccerPlayerManagerEvent.OpenEditPlayerDialog -> {
                _selectingPlayer.value = event.soccerPlayer
                _showDialogAddOrEdit.value = true
            }
            is SoccerPlayerManagerEvent.OpenConfirmDialog -> {
                title = event.title
                content = event.description
                function = event.function
                _showDialogConfirm.value = true
            }
            is SoccerPlayerManagerEvent.CloseDialogAddOrEdit -> {
                _showDialogAddOrEdit.value = false
            }
            is SoccerPlayerManagerEvent.CloseDialogConfirm -> {
                _showDialogConfirm.value = false
            }
            is SoccerPlayerManagerEvent.SelectList -> {
                preferences.saveSelectedListId(event.listId)
                _state.value = state.value.copy(selectedListId = event.listId)
                getSoccerPlayers(event.listId, state.value.soccerPlayerOrder)
            }
            is SoccerPlayerManagerEvent.AddSoccerPlayerList -> {
                viewModelScope.launch {
                    val id = soccerUseCases.insertSoccerPlayerList(SoccerPlayerList(name = event.name))
                    onEvent(SoccerPlayerManagerEvent.SelectList(id.toInt()))
                }
            }
            is SoccerPlayerManagerEvent.UpdateSoccerPlayerList -> {
                viewModelScope.launch {
                    soccerUseCases.insertSoccerPlayerList(event.soccerPlayerList)
                }
            }
            is SoccerPlayerManagerEvent.DeleteSoccerPlayerList -> {
                viewModelScope.launch {
                    soccerUseCases.deleteSoccerPlayerList(event.soccerPlayerList)
                    if (state.value.selectedListId == event.soccerPlayerList.id) {
                        // Switch to another list if the current one is deleted
                        val remainingLists = state.value.soccerPlayerLists.filter { it.id != event.soccerPlayerList.id }
                        if (remainingLists.isNotEmpty()) {
                            onEvent(SoccerPlayerManagerEvent.SelectList(remainingLists.first().id))
                        }
                    }
                }
            }
        }
    }

    private fun getSoccerPlayers(listId: Int, soccerPlayerOrder: SoccerPlayerOrder) {
        getSoccerPlayersJob?.cancel()
        getSoccerPlayersJob = soccerUseCases.getSoccerPlayer(listId, soccerPlayerOrder).onEach { value ->
            _state.value = state.value.copy(
                soccerPlayers = value,
                soccerPlayerOrder = soccerPlayerOrder
            )
        }.launchIn(viewModelScope)
    }
}
