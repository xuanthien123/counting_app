package com.aquarina.countingapp.presentation.features.caculating_china_poker.component

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aquarina.countingapp.presentation.features.caculating_china_poker.PersonsViewModel

@Composable
fun DialogWidget(viewModel: PersonsViewModel = hiltViewModel()): Unit {
    var name: String by remember { mutableStateOf("") }
    val showDialog = viewModel.showDialog.value
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showDialogBox(false) },
            title = { Text(text = "Nhập Tên") },
            text = {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    shape = RoundedCornerShape(12.dp), // 👈 Bo góc nè
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    label = { Text("Tên người chơi") }
                )
            },
            confirmButton = {
                Button(
                    enabled = name.isNotEmpty(),
                    onClick = {
                        viewModel.showDialogBox(false)
                        viewModel.addPerson(name)
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