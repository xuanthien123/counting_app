package com.aquarina.countingapp.presentation.features.soccer_player_manager.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
        var name by remember { mutableStateOf(player?.name ?: "") }
        var price by remember { mutableStateOf(player?.price?.toString() ?: "") }
        var note by remember { mutableStateOf(player?.note ?: "") }

        Dialog(onDismissRequest = {
            viewModel.onEvent(SoccerPlayerManagerEvent.CloseDialogAddOrEdit)
            focusManager.clearFocus()
        }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SportsSoccer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (player != null) "Sửa cầu thủ" else "Thêm cầu thủ",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        if (player != null) {
                            IconButton(
                                onClick = {
                                    viewModel.onEvent(
                                        SoccerPlayerManagerEvent.OpenConfirmDialog(
                                            title = "Xóa cầu thủ?",
                                            description = "Bạn có chắc chắn muốn xóa cầu thủ '${player.name}' không?",
                                            function = {
                                                viewModel.onEvent(SoccerPlayerManagerEvent.DeleteSoccerPlayer(player))
                                                viewModel.onEvent(SoccerPlayerManagerEvent.CloseDialogAddOrEdit)
                                            }
                                        )
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Xóa",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Tên cầu thủ") },
                        placeholder = { Text("Nhập tên...") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.Words
                        )
                    )

                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Giá (triệu)") },
                        placeholder = { Text("Ví dụ: 150") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Ghi chú") },
                        placeholder = { Text("Nhập ghi chú hoặc vị trí...") },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.Note, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.Sentences
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                viewModel.onEvent(SoccerPlayerManagerEvent.CloseDialogAddOrEdit)
                                focusManager.clearFocus()
                            }
                        ) {
                            Text("Hủy")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            enabled = name.isNotBlank() && price.isNotBlank(),
                            onClick = {
                                val priceInt = price.toIntOrNull() ?: 0
                                if (player != null) {
                                    viewModel.onEvent(SoccerPlayerManagerEvent.EditSoccerPlayer(
                                        player.copy(name = name, price = priceInt, note = note)
                                    ))
                                } else {
                                    viewModel.onEvent(SoccerPlayerManagerEvent.AddSoccerPlayer(
                                        SoccerPlayer(name = name, price = priceInt, note = note)
                                    ))
                                }
                                viewModel.onEvent(SoccerPlayerManagerEvent.CloseDialogAddOrEdit)
                                focusManager.clearFocus()
                            },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text(if (player != null) "Cập nhật" else "Thêm ngay")
                        }
                    }
                }
            }
        }
    }
}
