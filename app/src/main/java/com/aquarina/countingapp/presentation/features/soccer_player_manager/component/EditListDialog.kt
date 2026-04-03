package com.aquarina.countingapp.presentation.features.soccer_player_manager.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aquarina.countingapp.domain.model.SoccerPlayerList

@Composable
fun EditListDialog(
    showDialog: Boolean,
    soccerPlayerList: SoccerPlayerList?,
    canDelete: Boolean,
    onDismiss: () -> Unit,
    onUpdate: (String) -> Unit,
    onDelete: () -> Unit
) {
    if (showDialog && soccerPlayerList != null) {
        var listName by remember(soccerPlayerList) { mutableStateOf(soccerPlayerList.name) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Sửa danh sách")
                    if (canDelete) {
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Xóa danh sách",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = listName,
                        onValueChange = { listName = it },
                        label = { Text("Tên danh sách") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (canDelete) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Lưu ý: Xóa danh sách sẽ xóa tất cả cầu thủ trong đó.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Không thể xóa danh sách cuối cùng.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (listName.isNotBlank()) {
                            onUpdate(listName)
                            onDismiss()
                        }
                    }
                ) {
                    Text("Cập nhật")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Hủy")
                }
            }
        )
    }
}
