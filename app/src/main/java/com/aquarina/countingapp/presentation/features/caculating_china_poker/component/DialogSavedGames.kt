package com.aquarina.countingapp.presentation.features.caculating_china_poker.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aquarina.countingapp.domain.model.GameSaved
import com.aquarina.countingapp.presentation.features.caculating_china_poker.PersonEvent
import com.aquarina.countingapp.presentation.features.caculating_china_poker.PersonsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DialogSavedGames(
    viewModel: PersonsViewModel,
    onDismiss: () -> Unit
) {
    val state = viewModel.state.value
    var showAddDialog by remember { mutableStateOf(false) }
    var editingGame by remember { mutableStateOf<GameSaved?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Danh sách trận đấu") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tạo trận mới")
                }

                LazyColumn {
                    items(state.savedGames) { game ->
                        val isSelected = game.id == state.selectedGameId
                        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(game.createdAt))
                        
                        ListItem(
                            headlineContent = { 
                                Text(
                                    text = game.name ?: date,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            supportingContent = { if (game.name != null) Text(date) },
                            trailingContent = {
                                Row {
                                    IconButton(onClick = { editingGame = game }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Sửa tên")
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel.showConfirmDialog(
                                                title = "Xóa trận đấu",
                                                content = "Bạn có chắc muốn xóa trận đấu này?",
                                                function = { viewModel.onEvent(PersonEvent.DeleteGameSaved(game)) }
                                            )
                                        },
                                        enabled = !isSelected
                                    ) {
                                        Icon(
                                            Icons.Default.Delete, 
                                            contentDescription = "Xóa",
                                            tint = if (isSelected) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.clickable { 
                                viewModel.onEvent(PersonEvent.SelectGame(game.id!!))
                                onDismiss()
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Đóng") }
        }
    )

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tạo trận mới") },
            text = {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Tên trận đấu (Không bắt buộc)") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(PersonEvent.CreateGameSaved(name.takeIf { it.isNotBlank() }))
                    showAddDialog = false
                    onDismiss()
                }) { Text("Tạo") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Hủy") }
            }
        )
    }

    if (editingGame != null) {
        var name by remember { mutableStateOf(editingGame?.name ?: "") }
        AlertDialog(
            onDismissRequest = { editingGame = null },
            title = { Text("Sửa tên trận đấu") },
            text = {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Tên trận đấu") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    editingGame?.let { 
                        viewModel.onEvent(PersonEvent.UpdateGameSaved(it.copy(name = name.takeIf { it.isNotBlank() })))
                    }
                    editingGame = null
                }) { Text("Lưu") }
            },
            dismissButton = {
                TextButton(onClick = { editingGame = null }) { Text("Hủy") }
            }
        )
    }
}
