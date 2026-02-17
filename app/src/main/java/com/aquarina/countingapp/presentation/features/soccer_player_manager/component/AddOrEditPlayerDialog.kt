package com.aquarina.countingapp.presentation.features.soccer_player_manager.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aquarina.countingapp.domain.model.SoccerPlayer
import com.aquarina.countingapp.presentation.features.soccer_player_manager.SoccerPlayerManagerEvent
import com.aquarina.countingapp.presentation.features.soccer_player_manager.SoccerPlayerManagerViewModel

@Composable
fun AddOrEditPlayerDialog(viewModel: SoccerPlayerManagerViewModel = hiltViewModel()) {
    val showDialog = viewModel.showDialogAddOrEdit.value
    val player = viewModel.selectingPlayer.value
    val focusManager = LocalFocusManager.current

    if (showDialog) {
        var name: String by remember { mutableStateOf(player?.name ?: "") }
        var price: String by remember { mutableStateOf(player?.price?.toString() ?: "") }
        var note: String by remember { mutableStateOf(player?.note ?: "") }
        AlertDialog(
            onDismissRequest = {
                viewModel.onEvent(SoccerPlayerManagerEvent.CloseDialogAddOrEdit)
                focusManager.clearFocus()

            },
            title = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = if(player != null) "Thông tin" else "Thêm cầu thủ")
                    if (player != null) {
                        Box(
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(
                                    bounded = true
                                ),
                            ) {
                                viewModel.onEvent(
                                    SoccerPlayerManagerEvent.OpenConfirmDialog(
                                        title = "Xóa cầu thủ?",
                                        description = "Bạn có chắc chắn muốn xóa cầu thủ này không",
                                        function = {
                                            viewModel.onEvent(
                                                SoccerPlayerManagerEvent.DeleteSoccerPlayer(player)
                                            )
                                            viewModel.onEvent(SoccerPlayerManagerEvent.CloseDialogAddOrEdit)
                                        }
                                    ))

                            },
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Xóa",
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        "Tên cầu thủ",
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                    TextField(
                        value = name,
                        onValueChange = { newName ->
                            name = newName
                        },
                        placeholder = {Text("Nhập tên cầu thủ", color = Color.Gray)},
                        shape = RoundedCornerShape(12.dp), // 👈 Bo góc nè
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedLabelColor = Color.Gray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Sentences
                        )
                    )
                    Text(
                        "Giá",
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                    TextField(
                        value = price,
                        onValueChange = { newPrice ->
                            price = newPrice
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        placeholder = {Text("Nhập giá", color = Color.Gray)},
                        shape = RoundedCornerShape(12.dp), // 👈 Bo góc nè
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedLabelColor = Color.Gray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    )
                    Text(
                        "Ghi chú",
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                    TextField(
                        value = note,
                        onValueChange = { newNote ->
                            note = newNote
                        },

                        placeholder = {Text("Ghi chú", color = Color.Gray)},
                        shape = RoundedCornerShape(12.dp), // 👈 Bo góc nè
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedLabelColor = Color.Gray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Sentences
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = name.isNotEmpty() && price.isNotEmpty(),
                    onClick = {
                        if (player != null) {
                            viewModel.onEvent(SoccerPlayerManagerEvent.EditSoccerPlayer(
                                player.copy(name = name, price = price.toInt(), note = note)
                            ))
                        } else {
                            viewModel.onEvent(SoccerPlayerManagerEvent.AddSoccerPlayer(
                                SoccerPlayer(
                                    name = name,
                                    price = price.toInt(),
                                    note = note
                                )
                            ))

                        }
                        viewModel.onEvent(SoccerPlayerManagerEvent.CloseDialogAddOrEdit)
//                    viewModel.changeBetLevel(name)
//                    name = ""
                    }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = {
                    viewModel.onEvent(SoccerPlayerManagerEvent.CloseDialogAddOrEdit)
                    focusManager.clearFocus()
                }) {
                    Text("Hủy")
                }
            }
        )
    }
}