package com.aquarina.countingapp.presentation.features.menu

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aquarina.countingapp.R
import com.aquarina.countingapp.presentation.features.caculating_china_poker.CalculatingScreen
import com.aquarina.countingapp.presentation.features.menu.component.ClickableBox
import com.aquarina.countingapp.presentation.features.menu.component.ItemBox
import com.aquarina.countingapp.presentation.features.menu.component.SelectableCard
import com.aquarina.countingapp.presentation.features.menu.component.Service
import com.aquarina.countingapp.presentation.navigation.Screen

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.MenuScreen(
    navController: NavController = rememberNavController(),
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope?,
) {
    val items = listOf(
        Service(
            name = "Tính tiền đánh bài",
            description = "Tính tiền theo mức cược",
            image = R.drawable.ic_calculate,
            route = Screen.Calculating.route
        ),
        Service(
            name = "Quản lý cầu thủ",
            description = "Nó là điền tên với giá vô để nhìn cho dễ á",
            image = R.drawable.ic_note,
            route = Screen.SoccerPlayerManager.route
        )
    )
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = Color.White.toArgb()

            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = true
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text("Counting App")}
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items.forEach { item ->
                    ClickableBox(
                        onClick = {
                            navController.navigate(item.route)
                        },
                        modifier = if (animatedContentScope != null) {
                            Modifier
                                .sharedElement(
                                    sharedTransitionScope.rememberSharedContentState(key = "screen-${item.route}"),
                                    animatedVisibilityScope = animatedContentScope
                                )
                                .padding(bottom = 16.dp)
                                .fillMaxWidth()
                                .height(90.dp)
                                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                        } else {
                            Modifier
                                .padding(bottom = 16.dp)
                                .fillMaxWidth()
                                .height(90.dp)
                                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                        },
                        borderRadius = 8.dp
                    ) {
                        ItemBox(
                            image = item.image,
                            title = item.name,
                            description = item.description,
                            textModifier = if (animatedContentScope != null) {
                                Modifier.sharedElement(
                                    sharedTransitionScope.rememberSharedContentState(key = "text-${item.route}"),
                                    animatedVisibilityScope = animatedContentScope
                                )
                            } else {
                                Modifier
                            }
                        )
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun PreviewMenuScreen() {
    SharedTransitionLayout {
        MenuScreen(
            navController = rememberNavController(),
            this@SharedTransitionLayout,
            null
        )
    }
}