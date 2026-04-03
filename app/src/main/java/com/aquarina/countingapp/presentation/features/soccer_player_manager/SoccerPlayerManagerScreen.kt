package com.aquarina.countingapp.presentation.features.soccer_player_manager

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aquarina.countingapp.domain.model.SoccerPlayerList
import com.aquarina.countingapp.presentation.features.soccer_player_manager.component.*
import com.aquarina.countingapp.presentation.navigation.Screen


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.SoccerPlayerManagerScreen(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope?,
    viewModel: SoccerPlayerManagerViewModel = hiltViewModel(),
) {
    val state = viewModel.state.value
    var showAddListDialog by remember { mutableStateOf(false) }
    var listToEdit by remember { mutableStateOf<SoccerPlayerList?>(null) }

    Scaffold(
        modifier = if (animatedContentScope != null) Modifier.sharedElement(
            sharedTransitionScope.rememberSharedContentState(key = "screen-${Screen.SoccerPlayerManager.route}"),
            animatedVisibilityScope = animatedContentScope
        ) else Modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quản lý cầu thủ",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                        modifier = if (animatedContentScope != null) Modifier.sharedElement(
                            sharedTransitionScope.rememberSharedContentState(key = "text-${Screen.SoccerPlayerManager.route}"),
                            animatedVisibilityScope = animatedContentScope
                        ) else Modifier
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.onEvent(SoccerPlayerManagerEvent.ToggleOrderSection)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.Sort,
                            contentDescription = "Sort",
                            tint = if (state.isOrderSectionVisible.targetState) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(SoccerPlayerManagerEvent.OpenAddPlayerDialog)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Default.PlaylistAdd,
                    contentDescription = "Thêm mới cầu thủ"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Horizontal List Selector (Modern Chip-based)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.soccerPlayerLists) { list ->
                        val isSelected = list.id == state.selectedListId
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onEvent(SoccerPlayerManagerEvent.SelectList(list.id)) },
                            label = { Text(list.name) },
                            trailingIcon = if (isSelected) {
                                {
                                    IconButton(
                                        onClick = { listToEdit = list },
                                        modifier = Modifier.size(18.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Sửa",
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
                
                VerticalDivider(modifier = Modifier.height(32.dp))
                
                IconButton(
                    onClick = { showAddListDialog = true },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm danh sách",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            AnimatedVisibility(
                visibleState = state.isOrderSectionVisible
            ) {
                FilterPlayer(
                    onOrderChange = {
                        viewModel.onEvent(SoccerPlayerManagerEvent.Order(it))
                    }
                )
            }
            
            TableSoccerPlayer()
        }
    }

    AddOrEditPlayerDialog()
    DialogConfirm()
    AddListDialog(
        showDialog = showAddListDialog,
        onDismiss = { showAddListDialog = false },
        onConfirm = { name ->
            viewModel.onEvent(SoccerPlayerManagerEvent.AddSoccerPlayerList(name))
        }
    )
    EditListDialog(
        showDialog = listToEdit != null,
        soccerPlayerList = listToEdit,
        canDelete = state.soccerPlayerLists.size > 1,
        onDismiss = { listToEdit = null },
        onUpdate = { newName ->
            listToEdit?.let {
                viewModel.onEvent(SoccerPlayerManagerEvent.UpdateSoccerPlayerList(it.copy(name = newName)))
            }
        },
        onDelete = {
            listToEdit?.let { list ->
                viewModel.onEvent(SoccerPlayerManagerEvent.OpenConfirmDialog(
                    title = "Xóa danh sách?",
                    description = "Tất cả cầu thủ trong '${list.name}' sẽ bị xóa vĩnh viễn.",
                    function = {
                        viewModel.onEvent(SoccerPlayerManagerEvent.DeleteSoccerPlayerList(list))
                        listToEdit = null
                    }
                ))
            }
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun SoccerPlayerManagerPreview() {
    SharedTransitionLayout {
        SoccerPlayerManagerScreen(
            navController = rememberNavController(),
            this@SharedTransitionLayout,
            null,
//            viewModel = SoccerPlayerManagerViewModel(soccerUseCases)
        )
    }
}
