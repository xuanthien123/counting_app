package com.aquarina.countingapp.presentation.features.soccer_player_manager.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aquarina.countingapp.domain.model.SoccerPlayer
import com.aquarina.countingapp.presentation.components.formatToReadable
import com.aquarina.countingapp.presentation.features.soccer_player_manager.SoccerPlayerManagerEvent
import com.aquarina.countingapp.presentation.features.soccer_player_manager.SoccerPlayerManagerViewModel

@Composable
fun RowScope.TableCell(
    weight: Float,
    color: Color = Color.Transparent,
    alignment: Alignment = Alignment.Center,
    padding: PaddingValues = PaddingValues(top = 8.dp, bottom = 8.dp),
    text: @Composable () -> Unit
) {
    Box(
        Modifier
            .background(color)
            .weight(weight)
            .fillMaxHeight(),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .padding(padding),
        ) {
            text()
        }

    }
}

@Composable
fun TableSoccerPlayer(viewModel: SoccerPlayerManagerViewModel = hiltViewModel()) {
    val state = viewModel.state.value
    val listPlayer = state.soccerPlayers
    val numberColumnWeight = .15f
    val nameColumnWeight = .40f
    val priceColumnWeight = .15f
    val noteColumnWeight = .3f
    Box(
        Modifier
            .fillMaxWidth()
    ) {
        Column {
            Row(
                Modifier
                    .background(Color.LightGray)
                    .height(IntrinsicSize.Min)
            ) {
                TableCell(
                    text = { Text("STT", style = TextStyle(fontWeight = FontWeight.Bold)) },
                    weight = numberColumnWeight,
                    color = Color.White,
                    alignment = Alignment.CenterStart,
                    padding = PaddingValues(start = 16.dp, top = 8.dp, bottom = 8.dp)
                )
                TableCell(
                    text = { Text("Tên", style = TextStyle(fontWeight = FontWeight.Bold)) },
                    weight = nameColumnWeight,
                    color = Color.White,
                    alignment = Alignment.CenterStart
                )
                TableCell(
                    text = {
                        Text(
                            "Giá tiền",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    },
                    weight = priceColumnWeight,
                    color = Color.White,
                )
                TableCell(
                    text = { Text("Ghi chú", style = TextStyle(fontWeight = FontWeight.Bold)) },
                    weight = noteColumnWeight,
                    color = Color.White,
                    padding = PaddingValues(end = 16.dp, top = 8.dp, bottom = 8.dp)
                )
            }
            LazyColumn(
                Modifier
                    .fillMaxSize()
            ) {
                // Here are all the lines of your table.
                items(listPlayer.size) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(
                                    // hiệu ứng gợn sóng
                                    bounded = true,          // true = ripple theo shape, false = ripple tròn
                                )
                            ) {
                                viewModel.onEvent(
                                    SoccerPlayerManagerEvent.OpenEditPlayerDialog(
                                        listPlayer[it]
                                    )
                                )
                            }
                    ) {
                        val soccerPlayer = listPlayer[it]
                        TableCell(
                            text = { Text("${it + 1}") },
                            weight = numberColumnWeight,
                            alignment = Alignment.CenterStart,
                            padding = PaddingValues(start = 16.dp, top = 8.dp, bottom = 8.dp),
                            color = getColorFromPrice(soccerPlayer.price).copy(alpha = .2f)
                        )
                        TableCell(
                            text = { Text(soccerPlayer.name) },
                            weight = nameColumnWeight,
                            alignment = Alignment.CenterStart,
                            color = getColorFromPrice(soccerPlayer.price).copy(alpha = .2f)
                        )
                        TableCell(
                            text = { Text("${soccerPlayer.price.toString()}tr") },
                            weight = priceColumnWeight,
                            color = getColorFromPrice(soccerPlayer.price).copy(alpha = .2f)
                        )
                        TableCell(
                            text = { Text(soccerPlayer.note) },
                            weight = noteColumnWeight,
                            padding = PaddingValues(end = 16.dp, top = 8.dp, bottom = 8.dp),
                            color = getColorFromPrice(soccerPlayer.price).copy(alpha = .2f)
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(50.dp)) }
            }
        }
    }
}

fun getColorFromPrice(price: Int): Color {
    return when (price) {
        in 0..49 -> Color.LightGray
        in 50..99 -> Color.Green
        in 100..149 -> Color.Blue
        in 150..199 -> Color(0xFF9C27B0)
        else -> Color.Yellow
    }
}