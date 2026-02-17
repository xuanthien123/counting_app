package com.aquarina.countingapp.presentation.features.caculating_china_poker.component

import android.util.Log
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.aquarina.countingapp.presentation.features.caculating_china_poker.PersonsViewModel

@Composable
fun DialogConfirm(viewModel: PersonsViewModel = hiltViewModel()) {
    val showDialog: Boolean = viewModel.showConfirmDialog.value
    val title = viewModel.title
    val content = viewModel.content
    val function = viewModel.function
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.closeConfirmDialog() },
            title = { Text(text = title) },
            text = {
                Text(text = content)
            },
            confirmButton = {
                Button(onClick = {
                    function.invoke()
                    viewModel.closeConfirmDialog()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.closeConfirmDialog() }) {
                    Text("Hủy")
                }
            }
        )
    }

}