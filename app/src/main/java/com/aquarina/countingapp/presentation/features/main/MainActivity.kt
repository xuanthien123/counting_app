package com.aquarina.countingapp.presentation.features.main

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.aquarina.countingapp.presentation.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO // ép light
        )
        super.onCreate(savedInstanceState)
        setContent {
            TodoApp()
        }
    }
}

@Composable
fun TodoApp() {
    val navController = rememberNavController()
    NavGraph(navController)
}