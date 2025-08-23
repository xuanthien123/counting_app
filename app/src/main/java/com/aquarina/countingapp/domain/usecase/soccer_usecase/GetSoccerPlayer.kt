package com.aquarina.countingapp.domain.usecase.soccer_usecase

import com.aquarina.countingapp.domain.model.SoccerPlayer
import com.aquarina.countingapp.domain.repository.SoccerRepository
import com.aquarina.countingapp.domain.util.OrderType
import com.aquarina.countingapp.domain.util.SoccerPlayerOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSoccerPlayer(
    private val repository: SoccerRepository
) {
    operator fun invoke(soccerPlayerOrder: SoccerPlayerOrder = SoccerPlayerOrder.Price(OrderType.Descending)): Flow<List<SoccerPlayer>> {
        return repository.getSoccerers().map { value ->
            when(soccerPlayerOrder.orderType) {
                is OrderType.Ascending -> {
                    when(soccerPlayerOrder) {
                        is SoccerPlayerOrder.Price -> value.sortedBy { it.price }
                        is SoccerPlayerOrder.NamePlayer -> value.sortedBy { it.name }
                        is SoccerPlayerOrder.Date -> value.sortedBy { it.timestamp }
                    }
                }
                is OrderType.Descending -> {
                    when(soccerPlayerOrder) {
                        is SoccerPlayerOrder.Price -> value.sortedByDescending { it.price }
                        is SoccerPlayerOrder.NamePlayer -> value.sortedByDescending { it.name }
                        is SoccerPlayerOrder.Date -> value.sortedByDescending { it.timestamp }
                    }
                }
            }
         }
    }
}