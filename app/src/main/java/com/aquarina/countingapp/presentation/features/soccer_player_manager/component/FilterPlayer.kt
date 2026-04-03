package com.aquarina.countingapp.presentation.features.soccer_player_manager.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aquarina.countingapp.domain.util.OrderType
import com.aquarina.countingapp.domain.util.SoccerPlayerOrder
import com.aquarina.countingapp.presentation.features.soccer_player_manager.SoccerPlayerManagerViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterPlayer(
    modifier: Modifier = Modifier,
    onOrderChange: (SoccerPlayerOrder) -> Unit,
    viewModel: SoccerPlayerManagerViewModel = hiltViewModel()
) {
    val order = viewModel.state.value.soccerPlayerOrder
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Sắp xếp theo",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = order is SoccerPlayerOrder.Price,
                    onClick = { onOrderChange(SoccerPlayerOrder.Price(order.orderType)) },
                    label = { Text("Giá") }
                )
                FilterChip(
                    selected = order is SoccerPlayerOrder.NamePlayer,
                    onClick = { onOrderChange(SoccerPlayerOrder.NamePlayer(order.orderType)) },
                    label = { Text("Tên") }
                )
                FilterChip(
                    selected = order is SoccerPlayerOrder.Date,
                    onClick = { onOrderChange(SoccerPlayerOrder.Date(order.orderType)) },
                    label = { Text("Ngày tạo") }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InputChip(
                    selected = order.orderType is OrderType.Descending,
                    onClick = { onOrderChange(order.copy(orderType = OrderType.Descending)) },
                    label = { Text("Giảm dần") },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                )
                InputChip(
                    selected = order.orderType is OrderType.Ascending,
                    onClick = { onOrderChange(order.copy(orderType = OrderType.Ascending)) },
                    label = { Text("Tăng dần") },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                )
            }
        }
    }
}
