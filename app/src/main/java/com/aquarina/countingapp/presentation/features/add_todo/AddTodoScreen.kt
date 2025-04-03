package com.aquarina.countingapp.presentation.features.add_todo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AddTodoScreen(navController: NavController) {
//    var title by remember { mutableStateOf("") }
//    var description by remember { mutableStateOf("") }

    Scaffold(
//        topBar = {
//            TopAppBar(title = { Text("Add To-Do") })
//        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
//            OutlinedTextField(
//                value = title,
//                onValueChange = { title = it },
//                label = { Text("Title") },
//                modifier = Modifier.fillMaxWidth()
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            OutlinedTextField(
//                value = description,
//                onValueChange = { description = it },
//                label = { Text("Description") },
//                modifier = Modifier.fillMaxWidth()
//            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                Text("Add To-Do")
            }
        }
    }
}