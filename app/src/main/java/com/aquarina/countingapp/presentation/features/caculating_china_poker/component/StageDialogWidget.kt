package com.aquarina.countingapp.presentation.features.caculating_china_poker.component

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aquarina.countingapp.presentation.components.formatToReadable
import com.aquarina.countingapp.presentation.features.caculating_china_poker.PersonsViewModel


@Composable
fun StageDialogWidget(viewModel: PersonsViewModel = hiltViewModel()): Unit {
    val listWinLoseState = viewModel.listWinLoseState
    val showDialog = viewModel.showDialogEditStage.value
    val persons = viewModel.state.value.persons
    val focusManager = LocalFocusManager.current

    Log.d("NameInput", "stage: ${listWinLoseState.value}")
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                viewModel.showDialogEditStage(false)
                focusManager.clearFocus()
            },
            title = { Text(text = "Ván ${viewModel.stage + 1}") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    listWinLoseState.value.forEachIndexed { index, value ->
                        TextField(
                            value = value,
                            onValueChange = { newName ->
                                // Cập nhật giá trị tên người dùng vào danh sách
                                listWinLoseState.value =
                                    listWinLoseState.value.toMutableList().apply {
                                        this[index] = newName
                                    }
                            },
                            label = { Text(persons[index].name) },
                            shape = RoundedCornerShape(12.dp), // 👈 Bo góc nè
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent,
                                unfocusedLabelColor = Color.Gray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            )
                        )

                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = sumOfMatch(listWinLoseState.value) == 0,
                    onClick = {
                        viewModel.updateStage()
                        viewModel.showDialogEditStage(false)
//                    viewModel.changeBetLevel(name)
//                    name = ""
                    }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.showDialogEditStage(false) }) {
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