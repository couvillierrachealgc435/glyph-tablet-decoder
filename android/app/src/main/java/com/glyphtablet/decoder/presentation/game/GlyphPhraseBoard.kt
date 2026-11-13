package com.glyphtablet.decoder.presentation.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.glyphtablet.decoder.core.ui.ClayCard
import com.glyphtablet.decoder.domain.model.BoardPhase

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GlyphPhraseBoard(state: GameUiState, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    ClayCard(modifier) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val columns = ((maxWidth.value + 4f) / 52f).toInt().coerceAtLeast(1)
            var selectedSeen = false
            FlowRow(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                state.words.forEach { word ->
                    word.chunked(columns).forEach { segment ->
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            segment.forEach { cell ->
                                val first = cell.glyph != null && cell.glyph == state.selected && !selectedSeen
                                if (first) selectedSeen = true
                                GlyphCell(cell, state.selected == cell.glyph, state.phase == BoardPhase.Active && !state.saving && !state.error, first, onSelect)
                            }
                        }
                    }
                }
            }
        }
    }
}
