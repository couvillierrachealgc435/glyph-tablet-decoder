package com.glyphtablet.decoder.presentation.menu

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glyphtablet.decoder.R
import com.glyphtablet.decoder.core.di.ServiceLocator
import com.glyphtablet.decoder.core.theme.*
import com.glyphtablet.decoder.core.ui.*

@Composable
fun MenuRoute(onPlay: (Int?) -> Unit, onDaily: () -> Unit, onCodex: (Int) -> Unit, onStats: () -> Unit, onSettings: () -> Unit, preparing: Boolean) {
    val model: MenuViewModel = viewModel(factory = ServiceLocator.factory)
    val state by model.uiState.collectAsStateWithLifecycle()
    MenuScreen(state, onPlay, onDaily, onCodex, onStats, onSettings, model::toggleChapter, model::refresh, preparing)
}

@Composable
fun MenuScreen(state: MenuUiState, onPlay: (Int?) -> Unit, onDaily: () -> Unit, onCodex: (Int) -> Unit, onStats: () -> Unit, onSettings: () -> Unit, onExpand: (Int) -> Unit, onRetry: () -> Unit, preparing: Boolean) {
    val snapshot = state.snapshot
    val next = state.nextTabletId
    AppBackground {
        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth().heightIn(min = 56.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.archive_hall), Modifier.weight(1f), style = MaterialTheme.typography.headlineMedium)
                TextButton(onClick = onSettings, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.settings)) }
            }
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                item {
                    Surface(shape = RoundedCornerShape(22.dp), shadowElevation = 6.dp, border = BorderStroke(1.dp, Sandstone), color = DeepClay) {
                        Column(Modifier.background(Brush.linearGradient(listOf(Terracotta, DeepClay))).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(stringResource(R.string.archive_eyebrow), color = Sandstone, style = MaterialTheme.typography.labelSmall)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(stringResource(R.string.hero_title), Modifier.weight(1f), color = Parchment, style = MaterialTheme.typography.headlineMedium)
                                Image(painterResource(R.drawable.logo), null, Modifier.size(86.dp), contentScale = ContentScale.Fit)
                            }
                            Text(stringResource(R.string.hero_body), color = Parchment, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                item { ArchiveProgress(snapshot.completions.size) }
                item { StatusMessage(state.error, state.loading, onRetry) }
                if (snapshot.recoveredBoard) item { Text(stringResource(R.string.board_recovered), style = MaterialTheme.typography.bodyMedium) }
                item {
                    ClayCard(Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(stringResource(R.string.daily_tablet), style = MaterialTheme.typography.titleLarge)
                                Text(stringResource(R.string.daily_hint), style = MaterialTheme.typography.bodyMedium, color = MutedInk)
                            }
                            TextButton(onClick = onDaily, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.open)) }
                        }
                    }
                }
                item { ArtifactCollection() }
                item { Text(stringResource(R.string.tablet_chapters), style = MaterialTheme.typography.titleLarge) }
                items((0..9).toList(), key = { it }) { chapter ->
                    val expanded = chapter in state.expanded
                    val ids = ((chapter * 20 + 1)..(chapter * 20 + 20)).toList()
                    val solved = ids.count { it in snapshot.completions }
                    val description = stringResource(R.string.chapter_progress, solved)
                    ClayCard(Modifier.fillMaxWidth().animateContentSize().semantics { stateDescription = description }) {
                        TextButton(onClick = { onExpand(chapter) }, modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text(stringResource(R.string.chapter_number, chapter + 1), style = MaterialTheme.typography.labelSmall, color = Terracotta)
                                Text(stringResource(TextResources.chapters[chapter]), style = MaterialTheme.typography.titleLarge, color = Ink)
                            }
                            Text(stringResource(R.string.chapter_progress, solved), style = MaterialTheme.typography.labelSmall, color = MutedInk)
                        }
                        if (expanded) ids.forEach { id ->
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(stringResource(R.string.tablet_number, id), style = MaterialTheme.typography.bodyLarge)
                                    Text(stringResource(if (id in snapshot.completions) R.string.decoded else if ("archive_$id" in snapshot.sessions) R.string.in_progress else R.string.ready), style = MaterialTheme.typography.labelSmall)
                                }
                                TextButton(onClick = { onPlay(id) }, enabled = !preparing && !state.loading, modifier = Modifier.heightIn(min = 48.dp)) {
                                    Text(stringResource(if (id in snapshot.completions) R.string.replay else R.string.open))
                                }
                            }
                        }
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SecondaryButton(R.string.codex, { onCodex(snapshot.codexChapter) }, Modifier.weight(1f))
                        SecondaryButton(R.string.stats, onStats, Modifier.weight(1f))
                    }
                }
            }
            ClayCard(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Text(stringResource(R.string.tablet_number, next), style = MaterialTheme.typography.titleLarge)
                Text(stringResource(when { snapshot.completions.size == 200 -> R.string.archive_complete; snapshot.lastActive != null -> R.string.continue_tablet; else -> R.string.first_tablet }), style = MaterialTheme.typography.bodyMedium, color = MutedInk)
                PrimaryButton(R.string.play, { onPlay(null) }, enabled = !state.loading && !state.error && !preparing)
            }
        }
    }
}
