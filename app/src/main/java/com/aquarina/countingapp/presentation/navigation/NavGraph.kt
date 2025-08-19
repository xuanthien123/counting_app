package com.aquarina.countingapp.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aquarina.countingapp.presentation.features.add_todo.AddTodoScreen
import com.aquarina.countingapp.presentation.features.caculating_china_poker.CalculatingScreen
import com.aquarina.countingapp.presentation.features.list_todo.ListTodoScreen
import com.aquarina.countingapp.presentation.features.menu.MenuScreen

enum class Screen(val route: String) {
    Menu("menu"),
    Calculating("calculating"),
    ListTodo("list_todo"),
    AddTodo("add_todo")
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavGraph(navController: NavHostController) {
    SharedTransitionLayout {
        NavHost(
            navController, Screen.Menu.route,
//            popExitTransition = {
//                scaleOut(
//                    targetScale = 0.8f,
//                    transformOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 0.5f)
//                )
//            },
//            popEnterTransition = {
//                EnterTransition.None
//            }
        ) {
            composable(
                Screen.Menu.route,
//                popExitTransition = {
//                    ExitTransition.KeepUntilTransitionsFinished
//                }
                //            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
                //            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) }
                //            enterTransition = { scaleIn() },
                //            exitTransition = { scaleOut() }
            ) {
                MenuScreen(
                    navController,
                    this@SharedTransitionLayout,
                    this@composable,
                )
            }
            composable(
                Screen.Calculating.route,
                //            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
                //            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
                //            enterTransition = { scaleIn() },
                //            exitTransition = { scaleOut() }
            ) {
                // content
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
        }
    }
}