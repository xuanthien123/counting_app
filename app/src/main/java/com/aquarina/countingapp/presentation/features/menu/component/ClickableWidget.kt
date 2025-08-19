package com.aquarina.countingapp.presentation.features.menu.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ClickableBox(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderRadius: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(borderRadius)) // bo góc
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple( // hiệu ứng gợn sóng
                    bounded = true,          // true = ripple theo shape, false = ripple tròn
//                    color = Color.Gray       // màu ripple
                ),
                onClick = onClick
            ),
        content = content
    )
}
