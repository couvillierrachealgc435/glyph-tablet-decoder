package com.glyphtablet.decoder.presentation.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.glyphtablet.decoder.R
import com.glyphtablet.decoder.core.theme.*
import com.glyphtablet.decoder.core.ui.TextResources

@Composable
fun LetterKeyboard(keys: List<LetterKeyState>, onLetter: (Char) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val columns = if (maxWidth >= 308.dp) 6 else 5
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            keys.chunked(columns).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    row.forEach { key ->
                        val letter = stringResource(TextResources.letters[key.letter - 'A'])
                        val description = stringResource(if (key.enabled) R.string.available_key else R.string.disabled_key, letter)
                        Button(onClick = { onLetter(key.letter) }, enabled = key.enabled,
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp).semantics { stateDescription = description },
                            shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(0.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Parchment, contentColor = Ink, disabledContainerColor = Sandstone.copy(alpha = 0.35f), disabledContentColor = MutedInk.copy(alpha = 0.6f))) {
                            Text(letter, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}
