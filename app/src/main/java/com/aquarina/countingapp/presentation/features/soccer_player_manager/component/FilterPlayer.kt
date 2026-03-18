package com.aquarina.countingapp.presentation.features.soccer_player_manager.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aquarina.countingapp.domain.util.OrderType
import com.aquarina.countingapp.domain.util.SoccerPlayerOrder
import com.aquarina.countingapp.presentation.features.soccer_player_manager.SoccerPlayerManagerEvent
import com.aquarina.countingapp.presentation.features.soccer_player_manager.SoccerPlayerManagerViewModel

@Composable
fun FilterPlayer(
    modifier: Modifier = Modifier,
    onOrderChange: (SoccerPlayerOrder) -> Unit,
    viewModel: SoccerPlayerManagerViewModel = hiltViewModel()
) {
    val order = viewModel.state.value.soccerPlayerOrder
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            DefaultRadioButton(
                text = "Giá",
                selected = order is SoccerPlayerOrder.Price,
                onSelect = {
                    onOrderChange(SoccerPlayerOrder.Price(order.orderType))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = "Tên",
                selected = order is SoccerPlayerOrder.NamePlayer,
                onSelect = {
                    onOrderChange(SoccerPlayerOrder.NamePlayer(order.orderType))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = "Ngày tạo",
                selected = order is SoccerPlayerOrder.Date,
                onSelect = {
                    onOrderChange(SoccerPlayerOrder.Date(order.orderType))
                }
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            DefaultRadioButton(
                text = "Từ cao đến thấp",
                selected = order.orderType is OrderType.Descending,
                onSelect = {
                    onOrderChange(order.copy(orderType = OrderType.Descending))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = "Từ thấp đến cao",
                selected = order.orderType is OrderType.Ascending,
                onSelect = {
                    onOrderChange(order.copy(orderType = OrderType.Ascending))
                }
            )
        }
    }
}