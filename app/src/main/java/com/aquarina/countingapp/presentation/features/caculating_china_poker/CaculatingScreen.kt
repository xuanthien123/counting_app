package com.aquarina.countingapp.presentation.features.caculating_china_poker

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
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
    val showCurrency = viewModel.showCurrency.value
    val title = viewModel.title.value
    val titleIconName = viewModel.titleIcon.value
    val focusManager = LocalFocusManager.current
    val isProcessing = state.isProcessing
    
    val infiniteTransition = rememberInfiniteTransition(label = "processing")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val titleIcon = remember(titleIconName) {
        getIconByName(titleIconName)
    }

    var showSavedGames by remember { mutableStateOf(false) }

    val currentGameName = remember(state.savedGames, state.selectedGameId) {
        state.savedGames.find { it.id == state.selectedGameId }?.name
    }

    Scaffold(
        modifier = Modifier.sharedElement(
            sharedTransitionScope.rememberSharedContentState(key = "screen-${Screen.Calculating.route}"),
            animatedVisibilityScope = animatedContentScope
        ),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        enabled = !isProcessing,
                        onClick = { showSavedGames = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if (isProcessing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                        )                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            imageVector = titleIcon,
//                            contentDescription = null,
//                            modifier = Modifier.size(24.dp),
//                            tint = MaterialTheme.colorScheme.primary
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = currentGameName ?: title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (showCurrency) {
                                Text(
                                    text = "Mức cược: ${betLevel.formatToReadable()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        enabled = !isProcessing,
                        onClick = { viewModel.showDialogSettings(true) }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Cài đặt")
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
                                val score = if (showCurrency) person.total * betLevel else person.total
                                val scoreColor = if (score >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                                val isHighlighted = person.id == state.highlightedPersonId
                                
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isHighlighted) {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    },
                                    border = if (isHighlighted) {
                                        androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                    } else null,
                                    tonalElevation = if (isHighlighted) 4.dp else 0.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .padding(end = 8.dp)
                                                .size(32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AnimatedContent(
                                                targetState = person.total,
                                                transitionSpec = {
                                                    if (targetState > initialState) {
                                                        // Tăng điểm: Trượt LÊN
                                                        (slideInVertically { height -> -height } + fadeIn() togetherWith
                                                                slideOutVertically { height -> height } + fadeOut())
                                                            .using(SizeTransform(clip = false))
                                                    } else if (targetState < initialState) {
                                                        // Giảm điểm: Trượt XUỐNG
                                                        (slideInVertically { height -> height } + fadeIn() togetherWith
                                                                slideOutVertically { height -> -height } + fadeOut())
                                                            .using(SizeTransform(clip = false))
                                                    } else {
                                                        fadeIn(animationSpec = tween(0)) togetherWith fadeOut(animationSpec = tween(0))
                                                    }
                                                }, label = "achievement_anim"
                                            ) { targetTotal ->
                                                Text(
                                                    text = viewModel.getAchievement(targetTotal),
                                                    fontSize = 20.sp
                                                )
                                            }
                                        }
                                        Column {
                                            Text(
                                                text = person.name ?: "Player",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = if (isHighlighted) FontWeight.SemiBold else FontWeight.Medium,
                                                maxLines = 1,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            AnimatedContent(
                                                targetState = score,
                                                transitionSpec = {
                                                    if (targetState > initialState) {
                                                        // Tăng điểm: Trượt LÊN
                                                        (slideInVertically { height -> -height } + fadeIn() togetherWith
                                                                slideOutVertically { height -> height } + fadeOut())
                                                            .using(SizeTransform(clip = false))
                                                    } else if (targetState < initialState) {
                                                        // Giảm điểm: Trượt XUỐNG
                                                        (slideInVertically { height -> height } + fadeIn() togetherWith
                                                                slideOutVertically { height -> -height } + fadeOut())
                                                            .using(SizeTransform(clip = false))
                                                    } else {
                                                        fadeIn(animationSpec = tween(0)) togetherWith fadeOut(animationSpec = tween(0))
                                                    }
                                                },
                                                label = "score_anim"
                                            ) { targetScore ->
                                                Text(
                                                    text = if (showCurrency) "₫ ${targetScore.formatToReadable()}" else "${targetScore.formatToReadable()} điểm",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isHighlighted) {
                                                        scoreColor
                                                    } else {
                                                        scoreColor.copy(alpha = if (isProcessing) 0.38f else 1f)
                                                    }
                                                )
                                            }
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
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(radius = 23.dp, bounded = false)
                                    ) {
                                        if (isProcessing) {
                                            viewModel.stopPlaying()
                                        } else {
                                            viewModel.playAllPlayersInfo()
                                        }
                                    },
                                shape = CircleShape,
                                border = if (isProcessing) {
                                    androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                } else null,
                                color = if (isProcessing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer,
                                tonalElevation = 4.dp,
                                shadowElevation = 4.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        if (isProcessing) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = if (isProcessing) "Dừng phát" else "Đọc thông tin",
                                        tint = if (isProcessing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .then(if (isProcessing) Modifier.rotate(rotation) else Modifier)
                                    )
                                }
                            }
                    }
                }

                // Table Screen (Includes "Add Match" at bottom and auto-scroll)
                Box(modifier = Modifier.weight(1f)) {
                    TableScreen()
                    
                    // Overlay for the table area only when processing
                    if (isProcessing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            awaitPointerEvent()
                                        }
                                    }
                                }
                        )
                    }
                }

                DialogWidget()
                DialogWidgetBetLevel()
                StageDialogWidget()
                DialogConfirm()
                DialogSelectUser()
                
                if (viewModel.showDialogSettings.value) {
                    DialogSettings(
                        viewModel = viewModel,
                        onDismiss = { viewModel.showDialogSettings(false) }
                    )
                }

                if (showSavedGames) {
                    SavedGamesDialog(
                        onDismiss = { showSavedGames = false },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

fun getIconByName(name: String): ImageVector {
    return when (name) {
        "Calculate" -> Icons.Default.Calculate
        "SportsSoccer" -> Icons.Default.SportsSoccer
        "Casino" -> Icons.Default.Casino
        "Games" -> Icons.Default.Games
        "EmojiEvents" -> Icons.Default.EmojiEvents
        "Groups" -> Icons.Default.Groups
        "Payments" -> Icons.Default.Payments
        "ShoppingBag" -> Icons.Default.ShoppingBag
        "SportsEsports" -> Icons.Default.SportsEsports
        "HistoryEdu" -> Icons.Default.HistoryEdu
        "LocalActivity" -> Icons.Default.LocalActivity
        "MonetizationOn" -> Icons.Default.MonetizationOn
        "Star" -> Icons.Default.Star
        "Favorite" -> Icons.Default.Favorite
        else -> Icons.Default.Calculate
    }
}

val availableIcons = listOf(
    "Calculate", "SportsSoccer", "Casino", "Games", "EmojiEvents", "Groups", "Payments", "ShoppingBag", "SportsEsports", "HistoryEdu", "LocalActivity", "MonetizationOn", "Star", "Favorite"
)
