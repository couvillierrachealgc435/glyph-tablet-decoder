package com.glyphtablet.decoder.presentation.dialog

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.glyphtablet.decoder.R

@Composable
fun ResetArchiveDialog(resetting: Boolean, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.reset_dialog_title)) },
        text = { Text(stringResource(R.string.reset_dialog_body)) },
        confirmButton = { TextButton(onClick = onConfirm, enabled = !resetting, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.reset)) } },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !resetting, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.cancel)) } })
}
