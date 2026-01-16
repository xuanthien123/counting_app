package com.aquarina.countingapp.presentation.features.soccer_player_manager.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.aquarina.countingapp.presentation.features.soccer_player_manager.SoccerPlayerManagerEvent
import com.aquarina.countingapp.presentation.features.soccer_player_manager.SoccerPlayerManagerViewModel

@Composable
fun DialogConfirm(viewModel: SoccerPlayerManagerViewModel = hiltViewModel()) {
    val showDialog: Boolean = viewModel.showDialogConfirm.value
    if (showDialog) {
        val title = viewModel.title
        val content = viewModel.content
        val function = viewModel.function
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(SoccerPlayerManagerEvent.CloseDialogConfirm) },
            title = { Text(text = title) },
            text = {
                Text(text = content)
            },
            confirmButton = {
                Button(onClick = {
                    function.invoke()
                    viewModel.onEvent(SoccerPlayerManagerEvent.CloseDialogConfirm)
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.onEvent(SoccerPlayerManagerEvent.CloseDialogConfirm) }) {
                    Text("Hủy")
                }
            }
        )
    }

}