package com.glyphtablet.decoder.presentation.codex

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glyphtablet.decoder.R
import com.glyphtablet.decoder.core.di.ServiceLocator
import com.glyphtablet.decoder.core.theme.*
import com.glyphtablet.decoder.core.ui.*

@Composable
fun GlyphCodexRoute(chapter: Int, fromBoard: Boolean, onBack: () -> Unit, onSource: (Int) -> Unit) {
    val model: GlyphCodexViewModel = viewModel(factory = ServiceLocator.factory)
    val state by model.uiState.collectAsStateWithLifecycle()
    DisposableEffect(chapter) { model.show(chapter); onDispose { } }
    GlyphCodexScreen(state, fromBoard, onBack, onSource, model::selectChapter, model::retry)
}

@Composable
fun GlyphCodexScreen(state: GlyphCodexUiState, fromBoard: Boolean, onBack: () -> Unit, onSource: (Int) -> Unit, onChapter: (Int) -> Unit, onRetry: () -> Unit) {
    val pairs = state.snapshot.learned[state.chapter].orEmpty()
    val signs = (0..25).sortedWith(compareBy<Int> { if (it in pairs) 0 else 1 }.thenBy { pairs[it]?.letter?.code ?: it })
    AppBackground {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ScreenHeader(R.string.codex_title, onBack)
            Text(stringResource(R.string.codex_explainer), style = MaterialTheme.typography.bodyMedium, color = MutedInk)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (0..9).forEach { chapter ->
                    val selected = chapter == state.chapter
                    val description = stringResource(if (selected) R.string.selected else R.string.not_selected)
                    FilterChip(selected = selected, onClick = { onChapter(chapter) }, label = { Text(stringResource(TextResources.chapters[chapter])) },
                        modifier = Modifier.heightIn(min = 48.dp).semantics { stateDescription = description })
                }
            }
            StatusMessage(state.error, state.loading, onRetry)
            Text(stringResource(R.string.learned_count, pairs.size), style = MaterialTheme.typography.bodyMedium)
            if (pairs.isEmpty()) Text(stringResource(R.string.codex_empty), style = MaterialTheme.typography.bodyMedium, color = Terracotta)
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(signs.chunked(2)) { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { glyph ->
                            val pair = pairs[glyph]
                            ClayCard(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    GlyphMark(glyph, 36.dp)
                                    Text(if (pair == null) stringResource(R.string.sign_label, glyph + 1) else stringResource(TextResources.letters[pair.letter - 'A']), Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                                }
                                Text(if (pair == null) stringResource(R.string.not_learned) else stringResource(R.string.learned_source, pair.tabletId), style = MaterialTheme.typography.bodyMedium, color = MutedInk)
                                if (pair != null) TextButton(onClick = { onSource(pair.tabletId) }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.open_source)) }
                            }
                        }
                    }
                }
            }
            if (fromBoard) SecondaryButton(R.string.open_tablet, onBack, Modifier.fillMaxWidth())
        }
    }
}
