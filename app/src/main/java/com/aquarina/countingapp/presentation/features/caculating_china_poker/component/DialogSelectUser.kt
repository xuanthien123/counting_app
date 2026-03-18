package com.aquarina.countingapp.presentation.features.caculating_china_poker.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aquarina.countingapp.domain.model.UserTag
import com.aquarina.countingapp.presentation.features.caculating_china_poker.PersonsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DialogSelectUser(viewModel: PersonsViewModel = hiltViewModel()) {
    val state = viewModel.state.value
    val showDialog = viewModel.showDialogSelectUser.value
    var newTagName by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showDialogSelectUser(false) },
            confirmButton = {
                Button(onClick = {
                    viewModel.addSelectedTagsAsPersons()
                }) {
                    Text("Thêm vào bàn")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDialogSelectUser(false) }) {
                    Text("Hủy")
                }
            },
            title = { Text("Chọn người chơi") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Input to create new tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = newTagName,
                            onValueChange = { newTagName = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Tên mới...") },
                            singleLine = true
                        )
                        IconButton(onClick = {
                            if (newTagName.isNotBlank()) {
                                viewModel.addUserTag(newTagName)
                                newTagName = ""
                            }
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Thêm tag")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Đã chọn", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val selectedTags = state.userTags.filter { it.id in state.selectedTagIds }
                        selectedTags.forEach { tag ->
                            TagItem(
                                tag = tag,
                                isSelected = true,
                                onToggle = { tag.id?.let { viewModel.toggleTagSelection(it) } },
                                onDelete = { viewModel.deleteUserTag(tag) }
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text("Danh sách tag", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val unselectedTags = state.userTags.filter { it.id !in state.selectedTagIds }
                        unselectedTags.forEach { tag ->
                            TagItem(
                                tag = tag,
                                isSelected = false,
                                onToggle = { tag.id?.let { viewModel.toggleTagSelection(it) } },
                                onDelete = { viewModel.deleteUserTag(tag) }
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun TagItem(
    tag: UserTag,
    isSelected: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tag.name,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.Close,
                contentDescription = "Xóa",
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onDelete() },
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}
