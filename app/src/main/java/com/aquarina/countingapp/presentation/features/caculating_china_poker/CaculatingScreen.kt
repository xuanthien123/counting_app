package com.aquarina.countingapp.presentation.features.caculating_china_poker

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aquarina.countingapp.presentation.components.formatToReadable
import com.aquarina.countingapp.presentation.features.caculating_china_poker.component.*
import com.aquarina.countingapp.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CalculatingScreen(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    viewModel: PersonsViewModel = hiltViewModel(),
) {
    val state = viewModel.state.value
    val showDialog = viewModel.showDialog.value
    val betLevel = viewModel.betLevel.value
    val focusManager = LocalFocusManager.current
    Scaffold(
        modifier = Modifier.sharedElement(
            sharedTransitionScope.rememberSharedContentState(key = "screen-${Screen.Calculating.route}"),
            animatedVisibilityScope = animatedContentScope
        ),
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Tính tiền đánh bài",
                    style = TextStyle(fontWeight = FontWeight.W500, fontSize = 18.sp),
                    modifier = Modifier.sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = "text-${Screen.Calculating.route}"),
                        animatedVisibilityScope = animatedContentScope
                    )
                )
            }, actions = {
                IconButton(onClick = {
                    viewModel.showConfirmDialog(
                        content = "Bạn có chắc chắn muốn làm mới toàn bộ lịch sử chơi (sẽ mất số tiền hiện tại)",
                        title = "Đặt lại",
                        function = { viewModel.refreshData() })
                }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Làm mới")
                }
            })
        }) { padding ->
        Box(
            modifier = Modifier
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
                    Button(
//                        enabled = state.persons.size < 4,
                        enabled = true,
                        onClick = {
//                        viewModel.addPerson()
                            viewModel.showDialogBox(!showDialog)
                        }) {
//                        Text(text = "Thêm người chơi")
                        Icon(Icons.Default.Add, contentDescription = "Thêm")
                    }
                }
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    columns = GridCells.Fixed(2)
                ) {
                    items(state.persons) { value ->
                        Text(
                            "${value.name}${
                                viewModel.getAchievement(
                                    value.total
                                )
                            }: ${((value.total * betLevel)).formatToReadable()}",
                            fontSize = TextUnit(value = 16f, type = TextUnitType.Sp)
                        )
                    }
                }
                if (state.persons.isNotEmpty()) {
                    Button(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                            .align(Alignment.End), onClick = {
                            viewModel.showConfirmDialog(
                                content = "Bạn có chắc chắn muốn xóa toàn bộ người chơi",
                                title = "Xóa toàn bộ người chơi",
                                function = { viewModel.deleteAllPerson() })

                        }) {
//                        Text(text = "Xóa toàn bộ người chơi")
                        Icon(Icons.Default.Delete, contentDescription = "Xóa")
                    }
                }
                // Đảm bảo TableScreen có nội dung để hiển thị
                TableScreen()
                DialogWidget()
                DialogWidgetBetLevel()
                StageDialogWidget()
                DialogConfirm()
            }
        }
    }
}