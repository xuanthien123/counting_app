package com.aquarina.countingapp.presentation.features.soccer_player_manager

import androidx.compose.animation.core.MutableTransitionState
import com.aquarina.countingapp.domain.model.SoccerPlayer
import com.aquarina.countingapp.domain.util.OrderType
import com.aquarina.countingapp.domain.util.SoccerPlayerOrder

data class SoccerPlayerManagerState(
    val soccerPlayers: List<SoccerPlayer> = emptyList(),
    val soccerPlayerOrder: SoccerPlayerOrder = SoccerPlayerOrder.Date(OrderType.Descending)
) {
    val isOrderSectionVisible: MutableTransitionState<Boolean> = MutableTransitionState(false)
    fun setOrderSectionVisible(visible: Boolean) {
        isOrderSectionVisible.targetState = visible
    }
}