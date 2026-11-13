package com.glyphtablet.decoder.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val WarmEarthy = lightColorScheme(
    primary = ActionBlue, onPrimary = Parchment, secondary = Terracotta, onSecondary = Parchment,
    background = Parchment, onBackground = Ink, surface = Parchment, onSurface = Ink,
    surfaceVariant = Sandstone, onSurfaceVariant = MutedInk, outline = MutedInk,
    error = DeepClay, onError = Parchment
)

@Composable
fun AppTheme(large: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = WarmEarthy, typography = appTypography(large), content = content)
}
