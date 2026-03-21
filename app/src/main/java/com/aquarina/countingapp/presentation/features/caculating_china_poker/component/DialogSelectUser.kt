package com.aquarina.countingapp.presentation.features.caculating_china_poker.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Indication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
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
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Chọn người chơi")
                    if (state.selectedTagIds.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearTagSelection() }) {
                            Icon(Icons.Default.Deselect, contentDescription = "Bỏ chọn tất cả", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            },
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
                            shape = RoundedCornerShape(12.dp), // 👈 Bo góc nè
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent,
                                unfocusedLabelColor = Color.Gray
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words
                            ),
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
                                onDelete = {
                                    viewModel.showConfirmDialog(
                                        title = "Xóa tag",
                                        content = "Bạn có chắc chắn muốn xóa tag '${tag.name}' không?",
                                        function = { viewModel.deleteUserTag(tag) }
                                    )
                                }
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
                                onDelete = {
                                    viewModel.showConfirmDialog(
                                        title = "Xóa tag",
                                        content = "Bạn có chắc chắn muốn xóa tag '${tag.name}' không?",
                                        function = { viewModel.deleteUserTag(tag) }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TagItem(
    tag: UserTag,
    isSelected: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.combinedClickable(
            onClick = { onToggle() },
            onLongClick = { onDelete() },
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tag.name,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
