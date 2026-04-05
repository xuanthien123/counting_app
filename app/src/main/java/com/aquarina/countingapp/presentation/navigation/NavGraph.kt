package com.aquarina.countingapp.presentation.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aquarina.countingapp.presentation.features.add_todo.AddTodoScreen
import com.aquarina.countingapp.presentation.features.caculating_china_poker.CalculatingScreen
import com.aquarina.countingapp.presentation.features.list_todo.ListTodoScreen
import com.aquarina.countingapp.presentation.features.menu.MenuScreen
import com.aquarina.countingapp.presentation.features.soccer_player_manager.SoccerPlayerManagerScreen

enum class Screen(val route: String) {
    Menu("menu"),
    Calculating("calculating"),
    ListTodo("list_todo"),
    AddTodo("add_todo"),
    SoccerPlayerManager("soccer_player_manager"),
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavGraph(navController: NavHostController) {
    SharedTransitionLayout {
        NavHost(
            navController, Screen.Menu.route,
        ) {
            composable(
                Screen.Menu.route,
            ) {
                MenuScreen(
                    navController,
                    this@SharedTransitionLayout,
                    this@composable,
                )
            }
            composable(
                Screen.Calculating.route,
            ) {
                CalculatingScreen(
                    navController,
                    this@SharedTransitionLayout,
                    this@composable,
                )
            }
            composable(Screen.ListTodo.route) {
                ListTodoScreen(
                    navController,
                    this@SharedTransitionLayout,
                    this@composable,
                )
            }
            composable(Screen.AddTodo.route) { AddTodoScreen(navController) }
            composable(Screen.SoccerPlayerManager.route) {
                SoccerPlayerManagerScreen(
                    navController,
                    this@SharedTransitionLayout,
                    this@composable,
                )
            }
        }
    }
}
