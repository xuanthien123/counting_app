package com.aquarina.countingapp.presentation.features.caculating_china_poker.component

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.aquarina.countingapp.R
import com.aquarina.countingapp.presentation.components.formatToReadable
import com.aquarina.countingapp.presentation.features.caculating_china_poker.PersonsViewModel
import kotlinx.coroutines.delay

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    isHeader: Boolean = false,
    isFirstColumn: Boolean = false,
    isNegative: Boolean = false,
    isPositive: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val backgroundColor = when {
        isHeader -> MaterialTheme.colorScheme.primaryContainer
        isFirstColumn -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else -> Color.Transparent
    }
    
    val textColor = when {
        isHeader -> MaterialTheme.colorScheme.onPrimaryContainer
        isNegative -> Color(0xFFC62828)
        isPositive -> Color(0xFF2E7D32)
        else -> MaterialTheme.colorScheme.onSurface
    }.run { if (enabled) this else this.copy(alpha = 0.38f) }

    Box(
        modifier = Modifier
            .weight(weight)
            .background(backgroundColor)
            .height(IntrinsicSize.Min)
            .then(
                if (onClick != null && enabled) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(vertical = 10.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHeader || isFirstColumn) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun SelectionCheckbox(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    isHeader: Boolean = false
) {
    val checkedColor = if (isHeader) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
    val uncheckedColor = if (isHeader) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline
    val checkmarkColor = if (isHeader) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(if (checked) checkedColor else Color.Transparent)
            .border(1.5.dp, if (checked) checkedColor else uncheckedColor, CircleShape)
            .then(if (onCheckedChange != null) Modifier.clickable { onCheckedChange(!checked) } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = checkmarkColor
            )
        }
    }
}

@Composable
private fun SelectionTriStateCheckbox(
    state: ToggleableState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val checkedColor = MaterialTheme.colorScheme.onPrimaryContainer
    val uncheckedColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
    val markColor = MaterialTheme.colorScheme.primaryContainer

    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(if (state != ToggleableState.Off) checkedColor else Color.Transparent)
            .border(1.5.dp, if (state != ToggleableState.Off) checkedColor else uncheckedColor, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            ToggleableState.On -> Icon(Icons.Default.Check, null, Modifier.size(12.dp), markColor)
            ToggleableState.Indeterminate -> Box(Modifier.size(8.dp, 1.5.dp).background(markColor))
            ToggleableState.Off -> {}
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TableScreen(viewModel: PersonsViewModel = hiltViewModel()) {
    val numberOfStage = viewModel.numberOfStage.value
    val state = viewModel.state.value
    val isProcessing = state.isProcessing
    val column1Weight = .23f 
    val columnId = .12f
    val listState = rememberLazyListState()

    var previousStageCount by remember { mutableIntStateOf(numberOfStage) }
    var isInitialized by remember { mutableStateOf(false) }

    // Use a secondary state to control how many items are "visible" to the UI initially
    var visibleItemCount by remember { mutableIntStateOf(0) }

    // Reset and progressively load items when the stage list changes significantly
    LaunchedEffect(state.stageIds.size) {
        if (state.stageIds.size > visibleItemCount) {
            // If new items were added, we progressively show them
            while (visibleItemCount < state.stageIds.size) {
                visibleItemCount++
                delay(10) // Fast delay for "line to line" rendering effect
            }
        } else {
            // If items were deleted or it's a refresh, just sync
            visibleItemCount = state.stageIds.size
        }
    }

    // Auto-scroll to bottom only when number of stages increases AND it's not the initial load
    LaunchedEffect(numberOfStage) {
        if (isInitialized && numberOfStage > previousStageCount && !state.isSelectionMode) {
            listState.animateScrollToItem(numberOfStage)
        }
        previousStageCount = numberOfStage
        if (numberOfStage > 0) {
            isInitialized = true
        }
    }

    if (state.persons.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.blue_archive_alisu)
                        .decoderFactory(GifDecoder.Factory())
                        .build(),
                    modifier = Modifier.size(180.dp).clip(RoundedCornerShape(16.dp)),
                    contentDescription = null
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Vui lòng thêm người chơi để bắt đầu",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    } else {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
        ) {
            // Header: Player Names
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Header "#" Column / Select All Checkbox
                Box(
                    modifier = Modifier
                        .weight(columnId)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable(enabled = state.isSelectionMode || numberOfStage > 0) {
                            if (state.isSelectionMode) {
                                viewModel.toggleSelectAll()
                            }
                        }
                        .padding(vertical = 10.dp, horizontal = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = state.isSelectionMode,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(220, delayMillis = 90)) togetherWith
                                    fadeOut(animationSpec = tween(90))).using(SizeTransform(clip = false))
                        },
                        label = "header_id_checkbox_transition",
                        modifier = Modifier.graphicsLayer(clip = false),
                        contentAlignment = Alignment.Center
                    ) { isSelectionMode ->
                        if (isSelectionMode) {
                            val selectAllState = remember(state.selectedStages.size, numberOfStage) {
                                when {
                                    state.selectedStages.isEmpty() -> ToggleableState.Off
                                    state.selectedStages.size == numberOfStage -> ToggleableState.On
                                    else -> ToggleableState.Indeterminate
                                }
                            }
                            SelectionTriStateCheckbox(
                                state = selectAllState,
                                onClick = { viewModel.toggleSelectAll() }
                            )
                        } else {
                            Text(
                                text = "#",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                state.persons.forEachIndexed { index, person ->
                    TableCell(
                        text = person.name,
                        weight = column1Weight,
                        isHeader = true,
                        enabled = !isProcessing,
                        onClick = if (state.isSelectionMode) null else { { viewModel.showEditName(true, index) } }
                    )
                }
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            // Body: Match History
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Only show items up to visibleItemCount to achieve line-to-line rendering
                    val visibleStages = state.stageIds.take(visibleItemCount)

                    itemsIndexed(
                        items = visibleStages,
                        key = { _, id -> id }
                    ) { stageIndex, _ ->
                        val isSelected = state.selectedStages.contains(stageIndex)
                        
                        // Row Background Color Animation
                        val rowBackground by androidx.compose.animation.animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else Color.Transparent,
                            animationSpec = tween(300),
                            label = "row_bg"
                        )

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .graphicsLayer(clip = false)
                                .animateItem(
                                    fadeInSpec = tween(durationMillis = 300),
                                    fadeOutSpec = tween(durationMillis = 300),
                                    placementSpec = tween(durationMillis = 300)
                                )
                                .background(rowBackground)
                                .combinedClickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(),
                                    enabled = !isProcessing,
                                    onClick = {
                                        if (state.isSelectionMode) {
                                            viewModel.toggleStageSelection(stageIndex)
                                        } else {
                                            viewModel.showEditStage(stage = stageIndex)
                                        }
                                    },
                                    onLongClick = {
                                        if (!state.isSelectionMode) {
                                            viewModel.enterSelectionMode(stageIndex)
                                        }
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // ID Column / Checkbox
                            Box(
                                modifier = Modifier
                                    .weight(columnId)
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedContent(
                                    targetState = state.isSelectionMode,
                                    transitionSpec = {
                                        (fadeIn(animationSpec = tween(220, delayMillis = 90)) togetherWith
                                                fadeOut(animationSpec = tween(90))).using(SizeTransform(clip = false))
                                    },
                                    label = "id_checkbox_transition",
                                    modifier = Modifier.graphicsLayer(clip = false),
                                    contentAlignment = Alignment.Center
                                ) { isSelectionMode ->
                                    if (isSelectionMode) {
                                        SelectionCheckbox(
                                            checked = isSelected
                                        )
                                    } else {
                                        Text(
                                            text = "${stageIndex + 1}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }

                            state.persons.forEach { person ->
                                val score = if (person.stages.size > stageIndex) person.stages[stageIndex] else 0
                                TableCell(
                                    text = score.formatToReadable(),
                                    weight = column1Weight,
                                    isNegative = score < 0,
                                    isPositive = score > 0,
                                    enabled = !isProcessing
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
                    }
                }
            }

            // Bottom Buttons (Sticky at the bottom)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
            AnimatedContent(
                targetState = state.isSelectionMode,
                transitionSpec = {
                    (slideInVertically { it } + fadeIn()).togetherWith(slideOutVertically { it } + fadeOut())
                },
                label = "bottom_buttons_animation"
            ) { isSelection ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isSelection) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.showConfirmDialog(
                                        title = "Xóa ván bài",
                                        content = "Bạn có chắc muốn xóa ${state.selectedStages.size} ván đã chọn?",
                                        function = { viewModel.deleteMultipleStages(state.selectedStages.toList()) }
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Xóa (${state.selectedStages.size})", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.clearSelection() },
                                modifier = Modifier.weight(0.5f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Hủy")
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Undo/Redo Split Button
                            Row(
                                modifier = Modifier
                                    .width(96.dp)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = ripple(),
                                            enabled = state.canUndo && !isProcessing) { viewModel.undo() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Undo,
                                        contentDescription = "Hoàn tác",
                                        tint = if (state.canUndo && !isProcessing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                VerticalDivider(
                                    modifier = Modifier.height(20.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = ripple(),
                                            enabled = state.canRedo && !isProcessing) { viewModel.redo() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Redo,
                                        contentDescription = "Tiếp tục",
                                        tint = if (state.canRedo && !isProcessing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Button(
                                onClick = { viewModel.addNewStage() },
                                enabled = !isProcessing,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.filledTonalButtonColors(),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(vertical = 12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Ván mới", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }

                            Button(
                                onClick = { viewModel.showDialogAddMultiStage(true) },
                                enabled = !isProcessing,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.filledTonalButtonColors(),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(vertical = 12.dp)
                            ) {
                                Icon(Icons.Default.LibraryAdd, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Nhiều ván", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
    DialogAddMultiStage(viewModel = viewModel)
}
