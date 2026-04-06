package com.aquarina.countingapp.presentation.features.caculating_china_poker.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aquarina.countingapp.presentation.features.caculating_china_poker.PersonsViewModel
import kotlin.math.abs

@Composable
fun DialogAddMultiStage(viewModel: PersonsViewModel = hiltViewModel()) {
    val showDialog = viewModel.showDialogAddMultiStage.value
    var selectedCount by remember { mutableIntStateOf(5) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showDialogAddMultiStage(false) },
            title = {
                Text(
                    text = "Thêm nhiều ván",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Chọn số lượng ván muốn thêm vào danh sách",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background selection indicator (Square frame)
                        Surface(
                            modifier = Modifier
                                .size(64.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                        ) {}

                        NumberPickerWheel(
                            value = selectedCount,
                            onValueChange = { selectedCount = it },
                            range = 1..50
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addMultiStage(selectedCount)
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(0.45f)
                ) {
                    Text("Thêm $selectedCount ván")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.showDialogAddMultiStage(false) },
                    modifier = Modifier.fillMaxWidth(0.3f)
                ) {
                    Text("Hủy")
                }
            },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false
            ),
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
fun NumberPickerWheel(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier
) {
    val itemWidth = 60.dp
    val itemWidthPx = with(LocalDensity.current) { itemWidth.toPx() }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (value - range.first))
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val haptic = LocalHapticFeedback.current

    var containerWidthPx by remember { mutableFloatStateOf(0f) }

    // Determine center index based on scroll position
    val centerIndex by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex +
                    if (listState.firstVisibleItemScrollOffset > itemWidthPx / 2) 1 else 0
        }
    }

    LaunchedEffect(centerIndex) {
        val newValue = (range.first + centerIndex).coerceIn(range)
        if (newValue != value) {
            onValueChange(newValue)
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .onSizeChanged { containerWidthPx = it.width.toFloat() }
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()
                // Gradient mask for horizontal fading
                drawRect(
                    brush = Brush.horizontalGradient(
                        0f to Color.Transparent,
                        0.15f to Color.Black,
                        0.85f to Color.Black,
                        1f to Color.Transparent
                    ),
                    blendMode = BlendMode.DstIn
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (containerWidthPx > 0) {
            val sidePadding = with(LocalDensity.current) { (containerWidthPx / 2).toDp() - (itemWidth / 2) }

            LazyRow(
                state = listState,
                flingBehavior = flingBehavior,
                contentPadding = PaddingValues(horizontal = sidePadding),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(range.last - range.first + 1) { index ->
                    val number = range.first + index
                    val distance = abs(centerIndex - index)

                    val scale by animateFloatAsState(
                        targetValue = when {
                            distance == 0 -> 1.4f
                            distance == 1 -> 1.0f
                            else -> 0.7f
                        },
                        label = "scale"
                    )
                    val alpha by animateFloatAsState(
                        targetValue = when {
                            distance == 0 -> 1f
                            distance == 1 -> 0.6f
                            else -> 0.3f
                        },
                        label = "alpha"
                    )

                    Box(
                        modifier = Modifier
                            .width(itemWidth)
                            .fillMaxHeight()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = number.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = if (distance == 0) FontWeight.ExtraBold else FontWeight.Bold,
                            color = if (distance == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}