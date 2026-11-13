package com.glyphtablet.decoder.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.glyphtablet.decoder.R

@Composable
fun ScreenHeader(title: Int, onBack: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        TextButton(onClick = onBack, modifier = Modifier.heightIn(min = 48.dp).align(Alignment.Start)) { Text(stringResource(R.string.back)) }
        Text(stringResource(title), style = MaterialTheme.typography.headlineMedium)
    }
}
