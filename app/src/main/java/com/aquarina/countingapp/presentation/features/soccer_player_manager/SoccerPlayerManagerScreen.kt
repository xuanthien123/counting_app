package com.aquarina.countingapp.presentation.features.soccer_player_manager

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aquarina.countingapp.presentation.features.soccer_player_manager.component.AddOrEditPlayerDialog
import com.aquarina.countingapp.presentation.features.soccer_player_manager.component.DialogConfirm
import com.aquarina.countingapp.presentation.features.soccer_player_manager.component.FilterPlayer
import com.aquarina.countingapp.presentation.features.soccer_player_manager.component.TableSoccerPlayer
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
                        style = TextStyle(fontWeight = FontWeight.W500, fontSize = 18.sp),
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
                        Icon(Icons.AutoMirrored.Default.Sort, contentDescription = "Sort")
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(SoccerPlayerManagerEvent.OpenAddPlayerDialog)
            }) {
                Icon(
                    Icons.AutoMirrored.Default.PlaylistAdd,
                    contentDescription = "Thêm mới cầu thủ"
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column {
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
    }
    AddOrEditPlayerDialog()
    DialogConfirm()
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