package com.aquarina.countingapp.presentation.features.caculating_china_poker

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aquarina.countingapp.presentation.components.formatToReadable
import com.aquarina.countingapp.presentation.features.caculating_china_poker.component.DialogWidget
import com.aquarina.countingapp.presentation.features.caculating_china_poker.component.DialogWidgetBetLevel
import com.aquarina.countingapp.presentation.features.caculating_china_poker.component.StageDialogWidget
import com.aquarina.countingapp.presentation.features.caculating_china_poker.component.TableScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatingScreen(navController: NavController, viewModel: PersonsViewModel = hiltViewModel()) {
    val state = viewModel.state.value
    val showDialog = viewModel.showDialog.value
    val betLevel = viewModel.betLevel.value
    val focusManager = LocalFocusManager.current
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Calculating") }, actions = {
            IconButton(onClick = { viewModel.refreshData() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Làm mới")
            }
        })
    }) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        // Chỉ clear focus nếu không đụng vào input nào
                        if (event.changes.any { it.pressed }) {
                            focusManager.clearFocus()
                        }
                    }
                }
            }) {
            Column {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(), // Đảm bảo Row chiếm hết chiều rộng
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Mức cược: ${betLevel.formatToReadable()}")
                        IconButton(onClick = {
                            viewModel.showDialogBoxBetLevel(!showDialog)
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa")
                        }
                    }
                    Button(onClick = {
//                        viewModel.addPerson()
                        viewModel.showDialogBox(!showDialog)
                    }) {
                        Text(text = "Thêm người chơi")
                    }
                }
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    columns = GridCells.Fixed(2)
                ) {
                    items(state.persons) { value -> Text("${value.name}: ${((value.total * betLevel)).formatToReadable()}") }
                }
                if (state.persons.isNotEmpty()) {
                    Button(modifier = Modifier.padding(start = 16.dp, top = 16.dp), onClick = {
                        viewModel.deleteAllPerson()
                    }) {
                        Text(text = "Xóa toàn bộ người chơi")
                    }
                }
                // Đảm bảo TableScreen có nội dung để hiển thị
                TableScreen()
                DialogWidget()
                DialogWidgetBetLevel()
                StageDialogWidget()
            }
        }
    }
}