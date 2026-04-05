package com.aquarina.countingapp.presentation.features.caculating_china_poker.component

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aquarina.countingapp.presentation.features.caculating_china_poker.PersonsViewModel


@Composable
fun StageDialogWidget(viewModel: PersonsViewModel = hiltViewModel()): Unit {
    val state = viewModel.state.value
    val listWinLoseState = viewModel.listWinLoseState
    val showDialog = viewModel.showDialogEditStage.value
    val persons = state.persons
    val focusManager = LocalFocusManager.current

    // Auto-fill logic
    val emptyIndices = listWinLoseState.value.mapIndexedNotNull { index, s ->
        if (s.isEmpty() || s == "-") index else null
    }
    val canAutoFill = emptyIndices.size == 1
    val autoFillIndex = emptyIndices.firstOrNull()
    val autoFillValue = if (canAutoFill) {
        -listWinLoseState.value.mapIndexed { index, s ->
            if (index == autoFillIndex) 0 else (s.toIntOrNull() ?: 0)
        }.sum()
    } else 0

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!state.isProcessing) {
                    viewModel.showDialogEditStage(false)
                    focusManager.clearFocus()
                }
            },
            title = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Ván đấu ${viewModel.stage + 1}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        enabled = !state.isProcessing,
                        onClick = {
                            viewModel.showConfirmDialog(
                                content = "Bạn có chắc chắn muốn xóa ván này không?",
                                title = "Xóa ván đấu?",
                                function = {
                                    viewModel.deleteStage()
                                    viewModel.showDialogEditStage(false)
                                })
                        },
                        modifier = Modifier.offset(x = 12.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Xóa",
                            tint = if (state.isProcessing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (state.isProcessing) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                    } else {
                        Text(
                            text = "Nhập điểm cho từng người chơi. Tổng điểm phải bằng 0.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    
                    listWinLoseState.value.forEachIndexed { index, value ->
                        if (index < persons.size) {
                            OutlinedTextField(
                                value = value,
                                enabled = !state.isProcessing,
                                onValueChange = { newValue ->
                                    if (newValue.isEmpty() || newValue == "-" || newValue.toIntOrNull() != null) {
                                        listWinLoseState.value =
                                            listWinLoseState.value.toMutableList().apply {
                                                this[index] = newValue
                                            }
                                    }
                                },
                                label = { Text(persons[index].name) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number
                                ),
                                singleLine = true,
                                trailingIcon = {
                                    val num = value.toIntOrNull() ?: 0
                                    if (num != 0) {
                                        Icon(
                                            imageVector = if (num > 0) Icons.Default.AddCircle else Icons.Default.RemoveCircle,
                                            contentDescription = if (num > 0) "Thắng" else "Thua",
                                            tint = if (num > 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    } else if (canAutoFill && index == autoFillIndex && !state.isProcessing) {
                                        IconButton(onClick = {
                                            val newList = listWinLoseState.value.toMutableList()
                                            newList[index] = autoFillValue.toString()
                                            listWinLoseState.value = newList
                                        }) {
                                            Icon(
                                                Icons.Default.AutoAwesome,
                                                contentDescription = "Tự động điền",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                    
                    val currentSum = sumOfMatch(listWinLoseState.value)
                    
                    if (canAutoFill && !state.isProcessing) {
                        TextButton(
                            onClick = {
                                val newList = listWinLoseState.value.toMutableList()
                                newList[autoFillIndex!!] = autoFillValue.toString()
                                listWinLoseState.value = newList
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Điền ${persons[autoFillIndex!!].name}: $autoFillValue",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = if (currentSum == 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tổng điểm: $currentSum",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (currentSum == 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                            )
                            if (currentSum != 0) {
                                Text(
                                    text = " (Phải bằng 0)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = sumOfMatch(listWinLoseState.value) == 0 && !state.isProcessing,
                    onClick = {
                        viewModel.updateStage()
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (state.isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Lưu")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !state.isProcessing,
                    onClick = { viewModel.showDialogEditStage(false) }
                ) {
                    Text("Hủy")
                }
            }
        )
    }
}

fun sumOfMatch(listWinLose: List<String>): Int {
    var sum = 0
    for (value in listWinLose) {
        sum += value.toIntOrNull() ?: 0
    }
    return sum
}
