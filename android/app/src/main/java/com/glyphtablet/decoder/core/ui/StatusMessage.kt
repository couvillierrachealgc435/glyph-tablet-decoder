package com.glyphtablet.decoder.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import com.glyphtablet.decoder.R
import com.glyphtablet.decoder.core.theme.DeepClay

@Composable
fun StatusMessage(error: Boolean, loading: Boolean, onRetry: () -> Unit) {
    if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
    if (error) ClayCard(Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite }) {
        Text(stringResource(R.string.storage_error), color = DeepClay)
        SecondaryButton(R.string.retry, onRetry)
    }
}
