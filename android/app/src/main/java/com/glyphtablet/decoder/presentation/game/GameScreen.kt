package com.glyphtablet.decoder.presentation.game

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glyphtablet.decoder.R
import com.glyphtablet.decoder.core.di.ServiceLocator
import com.glyphtablet.decoder.core.theme.*
import com.glyphtablet.decoder.core.ui.*
import com.glyphtablet.decoder.domain.model.BoardPhase

@Composable
fun GameRoute(boardId: String, onResult: (String, String) -> Unit, onBack: () -> Unit, onCodex: (Int) -> Unit, onReplay: (String) -> Unit, onMenu: () -> Unit) {
    val model: GameViewModel = viewModel(factory = ServiceLocator.factory)
    val state by model.uiState.collectAsStateWithLifecycle()
    val owner = LocalLifecycleOwner.current
    val tone = remember { runCatching { ToneGenerator(AudioManager.STREAM_SYSTEM, 30) }.getOrNull() }
    LaunchedEffect(Unit) {
        model.events.collect { event ->
            when (event) {
                is GameEvent.Result -> onResult(event.boardId, event.attemptId)
                GameEvent.SuccessSound -> tone?.startTone(ToneGenerator.TONE_PROP_ACK, 60)
            }
        }
    }
    DisposableEffect(boardId, owner) {
        model.activate(boardId, owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
        val observer = LifecycleEventObserver { _, event ->
            when (event) { Lifecycle.Event.ON_RESUME -> model.resume(); Lifecycle.Event.ON_PAUSE -> model.pause(); else -> Unit }
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer); model.pause(); tone?.release() }
    }
    GameScreen(state, model::select, model::guess, model::hint, model::pause, model::resume, model::retry, onBack, { onCodex(state.chapter) }, { onReplay(boardId) }, onMenu)
}

@Composable
fun GameScreen(state: GameUiState, onSelect: (Int) -> Unit, onLetter: (Char) -> Unit, onHint: () -> Unit, onPause: () -> Unit, onResume: () -> Unit, onRetry: () -> Unit, onBack: () -> Unit, onCodex: () -> Unit, onReplay: () -> Unit, onMenu: () -> Unit) {
    AppBackground(Backdrop.Board) {
        BoxWithConstraints(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
            val compact = maxHeight < 640.dp
            val columnModifier = if (compact) Modifier.fillMaxSize().verticalScroll(rememberScrollState()) else Modifier.fillMaxSize()
            Column(columnModifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth().heightIn(min = 56.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onBack, modifier = Modifier.heightIn(min = 48.dp)) {
                        Image(painterResource(R.drawable.icon_home), null, Modifier.size(20.dp), contentScale = ContentScale.Fit)
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.back))
                    }
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(R.string.tablet_number, state.tabletId), style = MaterialTheme.typography.titleLarge)
                        Text(stringResource(TextResources.chapters[state.chapter]), style = MaterialTheme.typography.labelSmall, color = MutedInk)
                    }
                    Text(timeText(state.elapsedMs), style = MaterialTheme.typography.bodyMedium)
                }
                Column(Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                    Text(if (state.selected == null) stringResource(R.string.choose_sign) else stringResource(R.string.sign_selected, state.selected + 1), style = MaterialTheme.typography.bodyMedium)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        HintStones(state.hintsRemaining)
                        Text(stringResource(R.string.hints_left, state.hintsRemaining), style = MaterialTheme.typography.labelSmall, color = MutedInk)
                    }
                    state.message?.let { Text(stringResource(it), style = MaterialTheme.typography.bodyMedium, color = DeepClay) }
                }
                StatusMessage(state.error, state.loading, onRetry)
                val boardModifier = if (compact) Modifier.fillMaxWidth().height(300.dp) else Modifier.fillMaxWidth().weight(1f)
                GlyphPhraseBoard(state, onSelect, boardModifier)
                when (state.phase) {
                    BoardPhase.Solved -> ClayCard(Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.result_solved), style = MaterialTheme.typography.titleLarge)
                        PrimaryButton(R.string.play_again, onReplay)
                        SecondaryButton(R.string.menu, onMenu, Modifier.fillMaxWidth())
                    }
                    BoardPhase.Paused -> ClayCard(Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.paused_message))
                        PrimaryButton(R.string.resume, onResume)
                    }
                    else -> ClayCard(Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                        val hintState = stringResource(if (state.hintsRemaining == 0) R.string.no_hints else R.string.hints_left, state.hintsRemaining)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SecondaryButton(R.string.hint, onHint, Modifier.weight(1f).semantics { stateDescription = hintState }, enabled = state.hintsRemaining > 0 && state.phase == BoardPhase.Active && !state.saving && !state.error && !state.loading)
                            SecondaryButton(R.string.codex, onCodex, Modifier.weight(1f), enabled = state.phase == BoardPhase.Active && !state.saving)
                            TextButton(onClick = onPause, enabled = state.phase == BoardPhase.Active && !state.saving, modifier = Modifier.heightIn(min = 48.dp)) {
                                Image(painterResource(R.drawable.icon_pause), null, Modifier.size(18.dp), contentScale = ContentScale.Fit)
                                Text(stringResource(R.string.pause), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        LetterKeyboard(state.keys, onLetter)
                    }
                }
            }
        }
    }
}
