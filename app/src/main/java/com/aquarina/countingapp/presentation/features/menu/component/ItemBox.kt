package com.aquarina.countingapp.presentation.features.menu.component

import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ItemBox(
    textModifier: Modifier = Modifier,
    image: Int,
    title: String,
    description: String
) {
    Box(
        modifier = Modifier.padding(8.dp)
    ) {
        Row {
            Image(
                painter = painterResource(id = image),
                contentDescription = title,
                modifier = Modifier.size(70.dp)

            )
            Spacer(modifier = Modifier.size(16.dp))
            Column {
                Text(text = title, style = TextStyle(fontWeight = FontWeight.W500, fontSize = 18.sp), modifier = textModifier)
                Text(text = description, style = TextStyle(fontSize = 14.sp, color = Color.DarkGray))

            }
        }
    }
}