package com.glyphtablet.decoder.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun appTypography(large: Boolean): Typography {
    val factor = if (large) 1.15f else 1f
    return Typography(
        displaySmall = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 34.sp, lineHeight = 40.sp),
        headlineMedium = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp),
        titleLarge = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
        bodyLarge = TextStyle(fontSize = (16 * factor).sp, lineHeight = (24 * factor).sp),
        bodyMedium = TextStyle(fontSize = (14 * factor).sp, lineHeight = (20 * factor).sp),
        labelLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = (16 * factor).sp, lineHeight = (20 * factor).sp, letterSpacing = 0.7.sp),
        labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = (12 * factor).sp, lineHeight = (16 * factor).sp, letterSpacing = 0.4.sp)
    )
}
