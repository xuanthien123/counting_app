package com.aquarina.countingapp.presentation.features.caculating_china_poker

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aquarina.countingapp.domain.model.GameSaved
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedGamesDialog(
    onDismiss: () -> Unit,
    viewModel: PersonsViewModel
) {
    val state = viewModel.state.value
    var editingGame by remember { mutableStateOf<GameSaved?>(null) }
    val listState = rememberLazyListState()
    var hasInitialScrolled by rememberSaveable { mutableStateOf(false) }
    val density = LocalDensity.current

    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedGames by remember { mutableStateOf(setOf<GameSaved>()) }

    // Smoothly scroll to the selected game when the dialog is first shown
    LaunchedEffect(state.savedGames, state.selectedGameId) {
        if (state.savedGames.isNotEmpty() && state.selectedGameId != null && !hasInitialScrolled) {
            val index = state.savedGames.indexOfFirst { it.id == state.selectedGameId }
            if (index != -1) {
                hasInitialScrolled = true
                // Delay to allow the list to be populated and layout to occur
                delay(300)
                
                val layoutInfo = listState.layoutInfo
                val isVisible = layoutInfo.visibleItemsInfo.any { it.index == index }
                
                if (!isVisible) {
                    val visibleItems = layoutInfo.visibleItemsInfo
                    if (visibleItems.isNotEmpty()) {
                        val firstVisible = visibleItems.first()
                        val averageSize = visibleItems.map { it.size }.average()
                        val spacingPx = with(density) { 12.dp.toPx() }

                        // Approximate distance to the target item from current viewport start
                        val distanceToItem = (index - firstVisible.index) * (averageSize + spacingPx) - firstVisible.offset
                        
                        // Scroll slowly using animateScrollBy with a tween spec
                        listState.animateScrollBy(
                            value = (distanceToItem - 180).toFloat(),
                            animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing)
                        )
                        // Final snap to ensure exact positioning
                        listState.scrollToItem(index, -180)
                    } else {
                        // Fallback to standard animation if layout info is not available
                        listState.animateScrollToItem(index, -180)
                    }
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                if (isSelectionMode) "Đã chọn ${selectedGames.size}" else "Lịch sử trận đấu",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        navigationIcon = {
                            if (isSelectionMode) {
                                IconButton(onClick = {
                                    isSelectionMode = false
                                    selectedGames = emptySet()
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Hủy")
                                }
                            } else {
                                IconButton(onClick = onDismiss) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Quay lại",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        actions = {
                            if (isSelectionMode) {
                                val selectableGames = state.savedGames.filter { it.id != state.selectedGameId }
                                val allSelected = selectedGames.size == selectableGames.size && selectableGames.isNotEmpty()

                                IconButton(onClick = {
                                    selectedGames = if (allSelected) emptySet() else selectableGames.toSet()
                                }) {
                                    Icon(
                                        imageVector = if (allSelected) Icons.Default.LibraryAddCheck else Icons.Default.LibraryAdd,
                                        contentDescription = "Chọn tất cả",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        if (selectedGames.isNotEmpty()) {
                                            viewModel.showConfirmDialog(
                                                title = "Xóa các trận đã chọn",
                                                content = "Bạn có chắc muốn xóa ${selectedGames.size} trận đấu này? Toàn bộ dữ liệu bên trong sẽ bị mất.",
                                                function = {
                                                    viewModel.onEvent(PersonEvent.DeleteMultipleGameSaved(selectedGames.toList()))
                                                    isSelectionMode = false
                                                    selectedGames = emptySet()
                                                }
                                            )
                                        }
                                    },
                                    enabled = selectedGames.isNotEmpty()
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Xóa",
                                        tint = if (selectedGames.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                                    )
                                }
                            } else {
                                IconButton(onClick = { isSelectionMode = true }) {
                                    Icon(
                                        Icons.Default.DeleteSweep,
                                        contentDescription = "Chọn để xóa",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                        )
                    )
                },
                floatingActionButton = {
                    if (!isSelectionMode) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                viewModel.onEvent(PersonEvent.CreateGameSaved(null))
                                onDismiss()
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(16.dp),
                            elevation = FloatingActionButtonDefaults.elevation(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Tạo trận mới")
                        }
                    }
                }
            ) { padding ->
                if (state.savedGames.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding), contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Chưa có trận đấu nào",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.savedGames,
                            key = { it.id ?: it.createdAt }
                        ) { game ->
                            val isSelected = game.id == state.selectedGameId
                            val isChecked = selectedGames.contains(game)
                            val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(game.createdAt))

                            GameItem(
                                game = game,
                                isSelected = isSelected,
                                isSelectionMode = isSelectionMode,
                                isChecked = isChecked,
                                date = date,
                                onSelect = {
                                    if (isSelectionMode) {
                                        if (game.id != state.selectedGameId) {
                                            selectedGames = if (isChecked) selectedGames - game else selectedGames + game
                                        }
                                    } else {
                                        viewModel.onEvent(PersonEvent.SelectGame(game.id!!))
                                        onDismiss()
                                    }
                                },
                                onEdit = { editingGame = game }
                            )
                        }
                    }
                }
            }
        }

        if (editingGame != null) {
            GameNameDialog(
                title = "Sửa tên trận đấu",
                initialName = editingGame?.name ?: "",
                onDismiss = { editingGame = null },
                onConfirm = { name ->
                    editingGame?.let {
                        viewModel.onEvent(PersonEvent.UpdateGameSaved(it.copy(name = name)))
                    }
                    editingGame = null
                }
            )
        }
    }
}

@Composable
fun GameItem(
    game: GameSaved,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    isChecked: Boolean,
    date: String,
    onSelect: () -> Unit,
    onEdit: () -> Unit
) {
    val defaultName = remember(game.createdAt) {
        SimpleDateFormat("'Tạo lúc' HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date(game.createdAt))
    }

    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                ) { onSelect() },
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.3f
            ),
            border = if (isSelected) BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            ) else null,
            tonalElevation = if (isSelected) 4.dp else 0.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.1f
                            ),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.PlayArrow else Icons.Default.Casino,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = game.name ?: defaultName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    if (game.name != null) {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isSelectionMode) {
                    SelectionIndicator(
                        checked = isChecked,
                        enabled = !isSelected
                    )
                } else {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Sửa",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionIndicator(
    checked: Boolean,
    enabled: Boolean
) {
    val transition = updateTransition(targetState = checked, label = "selectionTransition")

    val color by transition.animateColor(label = "indicatorColor") { isChecked ->
        if (!enabled) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        else if (isChecked) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
    }

    val scale by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            } else {
                tween(200)
            }
        },
        label = "indicatorScale"
    ) { isChecked ->
        if (isChecked) 1f else 0.9f
    }

    Box(
        modifier = Modifier
            .size(48.dp) // Larger touch target
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (checked && enabled) color else Color.Transparent)
                .border(2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = checked,
                enter = scaleIn(animationSpec = spring(Spring.DampingRatioMediumBouncy)) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun GameNameDialog(
    title: String,
    initialName: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String?) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên trận đấu") },
                    placeholder = { Text("Ví dụ: Trận Tết 2024") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Bỏ trống để sử dụng thời gian hiện tại làm tên mặc định.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name.takeIf { it.isNotBlank() }) },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
