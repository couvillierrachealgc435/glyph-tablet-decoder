package com.glyphtablet.decoder.presentation.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.glyphtablet.decoder.core.theme.*

@Composable
fun HintStones(remaining: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        repeat(2) { index ->
            val color by animateColorAsState(if (index < remaining) Terracotta else Sandstone.copy(alpha = 0.2f), label = "hint stone")
            Surface(Modifier.size(16.dp, 12.dp), shape = RoundedCornerShape(6.dp), color = color, border = BorderStroke(1.dp, Terracotta.copy(alpha = 0.6f))) { }
        }
    }
}
