package com.aquarina.countingapp.presentation.features.caculating_china_poker.component

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aquarina.countingapp.presentation.features.caculating_china_poker.PersonsViewModel

@Composable
fun DialogWidget(viewModel: PersonsViewModel = hiltViewModel()): Unit {
    var name: String by viewModel.initName
    val showDialog = viewModel.showDialog.value

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showDialogBox(false) },
            title = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = if (viewModel.editingId.value != null) "Sửa tên" else "Nhập Tên")
                    if (viewModel.editingId.value != null) {
                        val user = viewModel.state.value.persons[viewModel.editingId.value!!]
                        if (user.total == 0) {
                        Box(
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(
                                    bounded = true
                                ),
                            ) {
                                viewModel.showConfirmDialog(
                                    content = "Bạn có chắc chắn muốn xóa người chơi này không?(Khi xóa người chơi sẽ làm mới lịch sử đánh bài và gộp lại thành 1 ván)",
                                    title = "Xóa người chơi?",
                                    function = {
                                        viewModel.deleteStage()
                                        viewModel.showDialogEditStage(false)
                                    })

                            },
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Xóa",
                                modifier = Modifier.padding(4.dp)
                            )
                        }}
                    }
                }},
            text = {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    shape = RoundedCornerShape(12.dp), // 👈 Bo góc nè
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    label = { Text("Tên người chơi") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Sentences
                    )
                )
            },
            confirmButton = {
                Button(
                    enabled = name.isNotEmpty(),
                    onClick = {
                        viewModel.showDialogBox(false)
                        if (viewModel.editingId.value != null) {
                            viewModel.editPerson(name)
                        } else {
                            viewModel.addPerson(name)
                        }
                        name = ""
                        Log.d("NameInput", "Tên đã nhập: $name")
                    }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.showDialogBox(false) }) {
                    Text("Hủy")
                }
            }
        )
    }
}