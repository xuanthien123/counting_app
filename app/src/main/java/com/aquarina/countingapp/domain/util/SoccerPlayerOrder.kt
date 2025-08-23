package com.aquarina.countingapp.domain.util

sealed class SoccerPlayerOrder(val orderType: OrderType) {
    class NamePlayer(orderType: OrderType) : SoccerPlayerOrder(orderType)
    class Price(orderType: OrderType) : SoccerPlayerOrder(orderType)
    class Date(orderType: OrderType) : SoccerPlayerOrder(orderType)
    fun copy(orderType: OrderType): SoccerPlayerOrder {
        return when(this) {
            is NamePlayer -> NamePlayer(orderType)
            is Price -> Price(orderType)
            is Date -> Date(orderType)
        }
    }

}