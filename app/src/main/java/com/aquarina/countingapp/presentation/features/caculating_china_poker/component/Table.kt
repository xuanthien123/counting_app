package com.aquarina.countingapp.presentation.features.caculating_china_poker.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
fun TableScreen(viewModel: PersonsViewModel = hiltViewModel()) {
    val numberOfStage = viewModel.numberOfStage.value
    val state = viewModel.state.value
    val isProcessing = state.isProcessing
    val column1Weight = .23f 
    val columnId = .12f
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when number of stages increases
    LaunchedEffect(numberOfStage) {
        if (numberOfStage > 0) {
            listState.animateScrollToItem(numberOfStage)
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
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                TableCell(text = "#", weight = columnId, isHeader = true)
                state.persons.forEachIndexed { index, person ->
                    TableCell(
                        text = person.name ?: "--",
                        weight = column1Weight,
                        isHeader = true,
                        enabled = !isProcessing,
                        onClick = { viewModel.showEditName(true, index) }
                    )
                }
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            // Body: Match History
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(numberOfStage) { stageIndex ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .then(
                                    if (!isProcessing) {
                                        Modifier.clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = ripple()
                                        ) { viewModel.showEditStage(stage = stageIndex) }
                                    } else Modifier
                                )
                        ) {
                            TableCell(
                                text = "${stageIndex + 1}", 
                                weight = columnId, 
                                isFirstColumn = true,
                                enabled = !isProcessing
                            )
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

                    // "Add Match" Button at the bottom of the list
                    item {
                        Button(
                            onClick = { viewModel.addNewStage() },
                            enabled = !isProcessing,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Thêm ván mới")
                        }
                    }
                }
            }
        }
    }
}
