package com.aquarina.countingapp.presentation.features.soccer_player_manager

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquarina.countingapp.domain.model.SoccerPlayer
import com.aquarina.countingapp.domain.usecase.person_usecase.PersonUseCases
import com.aquarina.countingapp.domain.usecase.soccer_usecase.SoccerUseCases
import com.aquarina.countingapp.domain.util.OrderType
import com.aquarina.countingapp.domain.util.SoccerPlayerOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoccerPlayerManagerViewModel @Inject constructor(
    private val soccerUseCases: SoccerUseCases
) : ViewModel() {
    private val _state = mutableStateOf(SoccerPlayerManagerState())
    val state: State<SoccerPlayerManagerState> = _state
    private var deletedSoccerPlayer: SoccerPlayer? = null
    private var getSoccerPlayersJob: Job? = null

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
        getSoccerPlayers(soccerPlayerOrder = SoccerPlayerOrder.Price(orderType = OrderType.Descending))
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
                getSoccerPlayers(event.soccerPlayerOrder)
            }
            is SoccerPlayerManagerEvent.DeleteSoccerPlayer -> {
                viewModelScope.launch {
                    soccerUseCases.deleteSoccerPlayer(event.soccerPlayer)
                    deletedSoccerPlayer = event.soccerPlayer
                }
            }
            is SoccerPlayerManagerEvent.ToggleOrderSection -> {
//                _state.value = state.value.copy(
//                    isOrderSectionVisible = !state.value.isOrderSectionVisible
//                )
                _state.value.setOrderSectionVisible(!state.value.isOrderSectionVisible.targetState)
            }
            is SoccerPlayerManagerEvent.AddSoccerPlayer -> {
                viewModelScope.launch {
                    soccerUseCases.insertSoccerPlayer(event.soccerPlayer)
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
        }
    }

    fun getSoccerPlayers(soccerPlayerOrder: SoccerPlayerOrder) {
        getSoccerPlayersJob?.cancel()
        getSoccerPlayersJob = soccerUseCases.getSoccerPlayer(soccerPlayerOrder).onEach { value ->
            _state.value = state.value.copy(
                soccerPlayers = value,
                soccerPlayerOrder = soccerPlayerOrder
            )
        }.launchIn(viewModelScope)
    }
}
