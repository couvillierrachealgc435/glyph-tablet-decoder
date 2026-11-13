package com.glyphtablet.decoder.core.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.glyphtablet.decoder.core.theme.*

@Composable
fun StatCard(label: Int, value: String, modifier: Modifier = Modifier) {
    ClayCard(modifier) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = DeepClay)
        Text(stringResource(label), style = MaterialTheme.typography.labelSmall, color = MutedInk)
    }
}
