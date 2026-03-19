package com.aquarina.countingapp.presentation.features.caculating_china_poker.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aquarina.countingapp.presentation.features.caculating_china_poker.PersonsViewModel

@Composable
fun DialogWidgetBetLevel(viewModel: PersonsViewModel = hiltViewModel()) {
    var betInput by remember { mutableStateOf("") }
    val showDialog = viewModel.showDialogBetLevel.value
    val currentBet = viewModel.betLevel.value

    LaunchedEffect(showDialog) {
        if (showDialog) {
            betInput = currentBet.toString()
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showDialogBoxBetLevel(false) },
            icon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { 
                Text(
                    text = "Mức cược",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = {
                Column {
                    Text(
                        text = "Nhập giá trị tiền cho mỗi đơn vị điểm.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = betInput,
                        onValueChange = { if (it.all { char -> char.isDigit() }) betInput = it },
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("Mức cược") },
                        placeholder = { Text("Ví dụ: 1000") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        prefix = { Text("₫ ", fontWeight = FontWeight.Bold) }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.changeBetLevel(betInput)
                        viewModel.showDialogBoxBetLevel(false)
                    },
                    shape = RoundedCornerShape(8.dp),
                    enabled = betInput.isNotEmpty()
                ) {
                    Text("Cập nhật")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDialogBoxBetLevel(false) }) {
                    Text("Hủy")
                }
            }
        )
    }
}