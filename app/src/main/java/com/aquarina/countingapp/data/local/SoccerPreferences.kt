package com.aquarina.countingapp.data.local

import android.content.Context
import android.content.SharedPreferences
import com.aquarina.countingapp.domain.util.OrderType
import com.aquarina.countingapp.domain.util.SoccerPlayerOrder

class SoccerPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("soccer_prefs", Context.MODE_PRIVATE)

    fun saveSelectedListId(listId: Int) {
        sharedPreferences.edit().putInt(KEY_SELECTED_LIST_ID, listId).apply()
    }

    fun getSelectedListId(): Int {
        return sharedPreferences.getInt(KEY_SELECTED_LIST_ID, 1)
    }

    fun saveOrder(order: SoccerPlayerOrder) {
        val orderField = when (order) {
            is SoccerPlayerOrder.Price -> FIELD_PRICE
            is SoccerPlayerOrder.NamePlayer -> FIELD_NAME
            is SoccerPlayerOrder.Date -> FIELD_DATE
        }
        val orderType = if (order.orderType is OrderType.Ascending) TYPE_ASC else TYPE_DESC
        
        sharedPreferences.edit()
            .putString(KEY_ORDER_FIELD, orderField)
            .putString(KEY_ORDER_TYPE, orderType)
            .apply()
    }

    fun getOrder(): SoccerPlayerOrder {
        val field = sharedPreferences.getString(KEY_ORDER_FIELD, FIELD_DATE)
        val type = sharedPreferences.getString(KEY_ORDER_TYPE, TYPE_DESC)
        
        val orderType = if (type == TYPE_ASC) OrderType.Ascending else OrderType.Descending
        
        return when (field) {
            FIELD_PRICE -> SoccerPlayerOrder.Price(orderType)
            FIELD_NAME -> SoccerPlayerOrder.NamePlayer(orderType)
            else -> SoccerPlayerOrder.Date(orderType)
        }
    }

    companion object {
        private const val KEY_SELECTED_LIST_ID = "selected_list_id"
        private const val KEY_ORDER_FIELD = "order_field"
        private const val KEY_ORDER_TYPE = "order_type"

        private const val FIELD_PRICE = "price"
        private const val FIELD_NAME = "name"
        private const val FIELD_DATE = "date"

        private const val TYPE_ASC = "asc"
        private const val TYPE_DESC = "desc"
    }
}
