package com.aquarina.countingapp.presentation.features.list_todo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aquarina.countingapp.presentation.navigation.Screen

@Composable
fun ListTodoScreen(navController: NavController) {
    val todoList = remember { mutableStateListOf("Buy groceries", "Read a book") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddTodo.route) }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(todoList) { todo ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Text(todo, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}