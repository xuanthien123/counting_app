package com.aquarina.countingapp.presentation.features.caculating_china_poker

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.stylusHoverIcon
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
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
    val betLevel = viewModel.betLevel.value
    val focusManager = LocalFocusManager.current
    val isProcessing = state.isProcessing
    
    Scaffold(
        modifier = Modifier.sharedElement(
            sharedTransitionScope.rememberSharedContentState(key = "screen-${Screen.Calculating.route}"),
            animatedVisibilityScope = animatedContentScope
        ),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Tính Tiền Đánh Bài",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Mức cược: ${betLevel.formatToReadable()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                actions = {
                    IconButton(
                        enabled = !isProcessing,
                        onClick = { viewModel.showDialogBoxBetLevel(true) }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Sửa cược")
                    }
                    IconButton(
                        enabled = !isProcessing,
                        onClick = { viewModel.showDialogSelectUser(true) }
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Thêm người")
                    }
                    IconButton(
                        enabled = !isProcessing,
                        onClick = {
                        viewModel.showConfirmDialog(
                            content = "Bạn có chắc chắn muốn làm mới toàn bộ lịch sử chơi?",
                            title = "Đặt lại",
                            function = { viewModel.refreshData() })
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Làm mới")
                    }
                    IconButton(
                        enabled = !isProcessing,
                        onClick = {
                        viewModel.showConfirmDialog(
                            content = "Bạn có chắc chắn muốn xóa toàn bộ người chơi?",
                            title = "Xóa tất cả",
                            function = { viewModel.deleteAllPerson() })
                    }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Xóa hết", tint = if (isProcessing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.changes.any { it.pressed }) {
                                focusManager.clearFocus()
                            }
                        }
                    }
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                
                // Achievement & Result Summary
                if (state.persons.isNotEmpty()) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            userScrollEnabled = false
                        ) {
                            items(state.persons) { person ->
                                val score = person.total * betLevel
                                val scoreColor = if (score >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                                
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = viewModel.getAchievement(person.total),
                                            fontSize = 20.sp,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Column {
                                            Text(
                                                text = person.name ?: "Player",
                                                style = MaterialTheme.typography.labelMedium,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = score.formatToReadable(),
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = scoreColor.copy(alpha = if (isProcessing) 0.38f else 1f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Sound Button in the very middle of the player list

                            Surface(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(55.dp)
                                    .background(
                                        if (isProcessing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.background, 
                                        shape = CircleShape
                                    )
                                    .padding(6.dp)
                                    .then(
                                        if (!isProcessing) {
                                            Modifier.clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = ripple(radius = 23.dp, bounded = false)
                                            ) { viewModel.playAllPlayersInfo() }
                                        } else Modifier
                                    ),
                                shape = CircleShape,
                                color = if (isProcessing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer,
                                tonalElevation = 4.dp,
                                shadowElevation = 4.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Đọc thông tin",
                                        tint = if (isProcessing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                    }


                }

                // Table Screen (Includes "Add Match" at bottom and auto-scroll)
                Box(modifier = Modifier.weight(1f)) {
                    TableScreen()
                }

                DialogWidget()
                DialogWidgetBetLevel()
                StageDialogWidget()
                DialogConfirm()
                DialogSelectUser()
            }
        }
    }
}
