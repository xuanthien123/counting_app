package com.aquarina.countingapp.presentation.features.caculating_china_poker.component

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aquarina.countingapp.presentation.features.caculating_china_poker.PersonsViewModel

@Composable
fun DialogWidgetBetLevel(viewModel: PersonsViewModel = hiltViewModel()) : Unit {
    var name: String by remember { mutableStateOf("") }
    name = viewModel.betLevel.value.toString()
    val showDialog = viewModel.showDialogBetLevel.value
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showDialogBoxBetLevel(false) },
            title = { Text(text = "Nhập mức cược") },
            text = {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    shape = RoundedCornerShape(12.dp), // 👈 Bo góc nè
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    label = { Text("Mức cược") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.showDialogBoxBetLevel(false)
                    viewModel.changeBetLevel(name)
                    name = ""
                    Log.d("NameInput", "Tên đã nhập: $name")
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.showDialogBoxBetLevel(false) }) {
                    Text("Hủy")
                }
            }
        )
    }
}