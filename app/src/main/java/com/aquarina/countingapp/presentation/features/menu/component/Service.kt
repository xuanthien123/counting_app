package com.aquarina.countingapp.presentation.features.menu.component

data class Service (
    val name: String,
    val description: String,
    val image: Int,
    val route: String = ""
)