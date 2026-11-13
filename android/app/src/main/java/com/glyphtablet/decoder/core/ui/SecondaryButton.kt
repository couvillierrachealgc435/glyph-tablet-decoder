package com.glyphtablet.decoder.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.glyphtablet.decoder.core.theme.*

@Composable
fun SecondaryButton(label: Int, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    OutlinedButton(onClick = onClick, enabled = enabled, modifier = modifier.heightIn(min = 48.dp), shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (enabled) MutedInk else MutedInk.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Parchment.copy(alpha = 0.85f), contentColor = Ink, disabledContainerColor = Sandstone.copy(alpha = 0.4f), disabledContentColor = MutedInk.copy(alpha = 0.65f))
    ) { Text(stringResource(label), style = MaterialTheme.typography.labelLarge) }
}
