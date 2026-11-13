package com.glyphtablet.decoder.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.glyphtablet.decoder.R
import com.glyphtablet.decoder.core.theme.*

enum class Backdrop { Splash, Archive, Board, Result }

@Composable
fun AppBackground(style: Backdrop = Backdrop.Archive, content: @Composable BoxScope.() -> Unit) {
    val colors = when (style) {
        Backdrop.Splash -> listOf(DeepClay.copy(alpha = 0.94f), Terracotta.copy(alpha = 0.91f), Ink.copy(alpha = 0.97f))
        Backdrop.Archive -> listOf(Parchment.copy(alpha = 0.94f), Parchment.copy(alpha = 0.87f), Sandstone.copy(alpha = 0.92f))
        Backdrop.Board -> listOf(Sandstone.copy(alpha = 0.88f), Parchment.copy(alpha = 0.94f))
        Backdrop.Result -> listOf(Parchment.copy(alpha = 0.96f), Sandstone.copy(alpha = 0.87f))
    }
    Box(Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.bg_game), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(colors)))
        Box(Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = Alignment.TopCenter) {
            Box(Modifier.fillMaxHeight().widthIn(max = 600.dp).fillMaxWidth(), content = content)
        }
    }
}
