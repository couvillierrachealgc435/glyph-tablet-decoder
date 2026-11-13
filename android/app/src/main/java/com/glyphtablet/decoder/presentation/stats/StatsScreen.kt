package com.glyphtablet.decoder.presentation.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glyphtablet.decoder.R
import com.glyphtablet.decoder.core.di.ServiceLocator
import com.glyphtablet.decoder.core.theme.*
import com.glyphtablet.decoder.core.ui.*

@Composable
fun StatsRoute(onBack: () -> Unit, onPlay: () -> Unit, onMenu: () -> Unit) {
    val model: StatsViewModel = viewModel(factory = ServiceLocator.factory)
    val state by model.uiState.collectAsStateWithLifecycle()
    StatsScreen(state, onBack, onPlay, onMenu, model::refresh)
}

@Composable
fun StatsScreen(state: StatsUiState, onBack: () -> Unit, onPlay: () -> Unit, onMenu: () -> Unit, onRetry: () -> Unit) {
    val records = state.snapshot.completions
    AppBackground {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            ScreenHeader(R.string.stats_title, onBack)
            Text(stringResource(R.string.stats_body), style = MaterialTheme.typography.bodyLarge, color = MutedInk)
            StatusMessage(state.error, state.loading, onRetry)
            ArchiveProgress(records.size)
            if (records.isEmpty() && !state.loading) ClayCard(Modifier.fillMaxWidth()) {
                GlyphMark(1, 56.dp, Terracotta)
                Text(stringResource(R.string.stats_empty), style = MaterialTheme.typography.titleLarge)
                PrimaryButton(R.string.play, onPlay, enabled = !state.error)
            }
            if (records.isNotEmpty()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(R.string.solved_label, stringResource(R.string.count_format, records.size), Modifier.weight(1f))
                    StatCard(R.string.average_label, timeText(state.averageMs), Modifier.weight(1f))
                }
                val fastest = state.fastestMs
                if (state.snapshot.dailyDates.isNotEmpty()) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(R.string.fastest_label, timeText(fastest), Modifier.weight(1f))
                    StatCard(R.string.daily_label, stringResource(R.string.count_format, state.snapshot.dailyDates.size), Modifier.weight(1f))
                }
                Text(stringResource(R.string.total_hints, state.hintsUsed), style = MaterialTheme.typography.bodyMedium)
            }
            (0..9).forEach { chapter ->
                val count = records.keys.count { (it - 1) / 20 == chapter }
                ClayCard(Modifier.fillMaxWidth()) {
                    Text(stringResource(TextResources.chapters[chapter]), style = MaterialTheme.typography.titleLarge)
                    Text(stringResource(R.string.chapter_progress, count), style = MaterialTheme.typography.bodyMedium)
                    LinearProgressIndicator(progress = { count / 20f }, modifier = Modifier.fillMaxWidth(), color = ActionBlue, trackColor = Sandstone)
                }
            }
            SecondaryButton(R.string.menu, onMenu, Modifier.fillMaxWidth())
        }
    }
}
