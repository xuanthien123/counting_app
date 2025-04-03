package com.aquarina.countingapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aquarina.countingapp.presentation.features.add_todo.AddTodoScreen
import com.aquarina.countingapp.presentation.features.caculating_china_poker.CalculatingScreen
import com.aquarina.countingapp.presentation.features.list_todo.ListTodoScreen

enum class Screen(val route: String) {
    Calculating("calculating"),
    ListTodo("list_todo"),
    AddTodo("add_todo")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Calculating.route) {
        composable(Screen.Calculating.route) { CalculatingScreen(navController) }
        composable(Screen.ListTodo.route) { ListTodoScreen(navController) }
        composable(Screen.AddTodo.route) { AddTodoScreen(navController) }
    }
}