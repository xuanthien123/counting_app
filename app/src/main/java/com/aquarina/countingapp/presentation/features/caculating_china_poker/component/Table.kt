package com.aquarina.countingapp.presentation.features.caculating_china_poker.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aquarina.countingapp.presentation.components.formatToReadable
import com.aquarina.countingapp.presentation.features.caculating_china_poker.PersonsViewModel

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    color: Color = Color.Transparent
) {
    Box(
        modifier = Modifier
            .border(.5.dp, Color.Gray)
            .padding(top = 8.dp, bottom = 8.dp)
            .weight(weight)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
        )

    }
}

@Composable
fun TableScreen(viewModel: PersonsViewModel = hiltViewModel()) {
    val numberOfStage = viewModel.numberOfStage.value
    val state = viewModel.state.value
    val column1Weight = .23f // 23%
    val columnId = .08f
    Box(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            Modifier
                .border(1.dp, Color.Gray)
        ) {
            Row(Modifier.background(Color.LightGray)) {
                TableCell(text = "", weight = columnId, color = Color.LightGray)
                for (number in 0..3) {
                    if (state.persons.size < number + 1) {
                        TableCell(text = "?", weight = column1Weight, color = Color.LightGray)
                    } else {
                        TableCell(
                            text = state.persons[number].name ?: "--",
                            weight = column1Weight,
                            color = Color.LightGray
                        )
                    }
                }
//                TableCell(text = "Column 1", weight = column1Weight)
//                TableCell(text = "Column 2", weight = column1Weight)
//                TableCell(text = "Column 3", weight = column1Weight)
//                TableCell(text = "Column 4", weight = column1Weight)
            }
            LazyColumn(
                Modifier
                    .fillMaxSize()
            ) {
                // Here are all the lines of your table.
                items(numberOfStage) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(
                                    // hiệu ứng gợn sóng
                                    bounded = true,          // true = ripple theo shape, false = ripple tròn
                                )
                            ) {
                                viewModel.showEditStage(stage = it)
                            }
                    ) {
                        TableCell(text = "${it + 1}", weight = columnId)
                        for (number in 0..3) {
                            if (state.persons.size < number + 1) {
                                TableCell(text = "-", weight = column1Weight)
                            } else {
                                if (state.persons[number].stages.size < it + 1) {
                                    TableCell(
                                        text = "-",
                                        weight = column1Weight
                                    )
                                } else {
                                    TableCell(
                                        text = state.persons[number].stages[it].formatToReadable(),
                                        weight = column1Weight
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Box(
                        Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                            .border(.5.dp, Color.Gray)
                            .background(Color.LightGray)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(
                                    // hiệu ứng gợn sóng
                                    bounded = true,          // true = ripple theo shape, false = ripple tròn
                                ),
                            ) {
                                viewModel.addNewStage()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Thêm màn chơi mới")
                    }
                }
            }
        }
        Row(
            Modifier
                .shadow(8.dp, clip = true)
                .background(Color.LightGray)
                .border(1.dp, Color.Gray)
        ) {
            TableCell(text = "", weight = columnId, color = Color.LightGray)
            for (number in 0..3) {
                if (state.persons.size < number + 1) {
                    TableCell(text = "?", weight = column1Weight, color = Color.LightGray)
                } else {
                    TableCell(
                        text = state.persons[number].name ?: "--",
                        weight = column1Weight,
                        color = Color.LightGray
                    )
                }
            }
//                TableCell(text = "Column 1", weight = column1Weight)
//                TableCell(text = "Column 2", weight = column1Weight)
//                TableCell(text = "Column 3", weight = column1Weight)
//                TableCell(text = "Column 4", weight = column1Weight)
        }
    }
}