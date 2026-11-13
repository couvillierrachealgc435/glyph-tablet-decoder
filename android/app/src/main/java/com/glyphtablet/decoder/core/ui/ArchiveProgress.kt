package com.glyphtablet.decoder.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.glyphtablet.decoder.R
import com.glyphtablet.decoder.core.theme.*

@Composable
fun ArchiveProgress(count: Int) {
    val progress by animateFloatAsState(count / 200f, label = "archive")
    val description = stringResource(R.string.archive_progress, count)
    Column(Modifier.semantics { stateDescription = description; liveRegion = LiveRegionMode.Polite }, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(description, style = MaterialTheme.typography.bodyMedium)
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(6.dp), color = ActionBlue, trackColor = Sandstone)
    }
}
