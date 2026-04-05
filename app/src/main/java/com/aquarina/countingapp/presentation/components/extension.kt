package com.aquarina.countingapp.presentation.components

import java.text.DecimalFormat

// Extension function cho Int
fun Int.formatToReadable(): String {
    val decimalFormat = DecimalFormat("#,###")
    return decimalFormat.format(this)
}

// Extension function cho Double (nếu cần cho số thập phân)
fun Double.formatToReadable(): String {
    val decimalFormat = DecimalFormat("#,###.##")
    return decimalFormat.format(this)
}