package com.glyphtablet.decoder.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.glyphtablet.decoder.core.theme.*

@Composable
fun PrimaryButton(label: Int, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f, label = "press")
    val shape = RoundedCornerShape(18.dp)
    Button(
        onClick = onClick, enabled = enabled, interactionSource = interaction, shape = shape,
        modifier = modifier.fillMaxWidth().heightIn(min = 60.dp).scale(scale).shadow(if (enabled) 4.dp else 0.dp, shape)
            .clip(shape).background(Brush.horizontalGradient(if (enabled) listOf(ActionBlue, DeepBlue) else listOf(Sandstone, Sandstone))),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Parchment, disabledContainerColor = Color.Transparent, disabledContentColor = MutedInk),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) { Text(stringResource(label), style = MaterialTheme.typography.labelLarge) }
}
