package com.glyphtablet.decoder.presentation.gameover

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glyphtablet.decoder.R
import com.glyphtablet.decoder.core.di.ServiceLocator
import com.glyphtablet.decoder.core.theme.*
import com.glyphtablet.decoder.core.ui.*

@Composable
fun GameOverRoute(boardId: String, onAgain: () -> Unit, onNext: () -> Unit, onMenu: () -> Unit) {
    val model: GameOverViewModel = viewModel(factory = ServiceLocator.factory)
    val state by model.uiState.collectAsStateWithLifecycle()
    DisposableEffect(boardId) { model.show(boardId); onDispose { } }
    GameOverScreen(state, onAgain, onNext, onMenu, model::retry)
}

@Composable
fun GameOverScreen(state: GameOverUiState, onAgain: () -> Unit, onNext: () -> Unit, onMenu: () -> Unit, onRetry: () -> Unit) {
    AppBackground(Backdrop.Result) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Spacer(Modifier.height(16.dp))
            Text(stringResource(if (state.solved) R.string.result_eyebrow else R.string.unfinished_eyebrow), style = MaterialTheme.typography.labelSmall, color = Terracotta)
            if (state.solved) Image(painterResource(R.drawable.logo), stringResource(R.string.seal_description), Modifier.size(96.dp), contentScale = ContentScale.Fit)
            else ClayCard { GlyphMark(7, 80.dp, Terracotta) }
            Text(stringResource(if (state.solved) R.string.result_solved else R.string.result_unfinished),
                Modifier.semantics { heading(); liveRegion = LiveRegionMode.Polite }, style = MaterialTheme.typography.displaySmall, textAlign = TextAlign.Center)
            StatusMessage(state.error, state.loading, onRetry)
            if (state.missing) Text(stringResource(R.string.result_missing), textAlign = TextAlign.Center)
            else if (!state.loading) {
                ClayCard(Modifier.fillMaxWidth()) {
                    if (state.solved) {
                        SelectionContainer {
                            Text(stringResource(TextResources.proverbs[state.tabletId - 1]), style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif, fontSize = MaterialTheme.typography.bodyLarge.fontSize * 1.5f, lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.42f))
                        }
                        HorizontalDivider(color = Sandstone)
                        Text(stringResource(TextResources.originTitle(state.origin)), style = MaterialTheme.typography.titleLarge)
                        Text(stringResource(TextResources.originNote(state.origin)), style = MaterialTheme.typography.bodyLarge, color = MutedInk)
                    } else {
                        Text(stringResource(R.string.unfinished_body), style = MaterialTheme.typography.bodyLarge)
                        Text(stringResource(R.string.board_progress, state.decodedSigns, state.totalSigns), color = Terracotta, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                if (state.solved && state.count > 0 && state.time > 0) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(R.string.time_label, timeText(state.time), Modifier.weight(1f))
                        StatCard(R.string.archive_label, stringResource(R.string.archive_fraction, state.count), Modifier.weight(1f))
                    }
                }
                if (state.solved) {
                    Text(if (state.hintsUsed == 0) stringResource(R.string.no_hints_used) else stringResource(R.string.hint_usage, state.hintsUsed), style = MaterialTheme.typography.bodyMedium, color = MutedInk)
                    Text(stringResource(when { state.practice -> R.string.practice_note; state.daily -> R.string.daily_recorded; state.nextAvailable -> R.string.next_waiting; else -> R.string.archive_complete }), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
                }
                if (state.solved && state.nextAvailable && !state.practice) {
                    PrimaryButton(R.string.next_tablet, onNext)
                    SecondaryButton(R.string.play_again, onAgain, Modifier.fillMaxWidth())
                } else PrimaryButton(R.string.play_again, onAgain)
            }
            SecondaryButton(R.string.menu, onMenu, Modifier.fillMaxWidth())
        }
    }
}
