package com.glyphtablet.decoder.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
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
import com.glyphtablet.decoder.presentation.dialog.ResetArchiveDialog

@Composable
fun SettingsRoute(onBack: () -> Unit, onMenu: () -> Unit, onReset: () -> Unit) {
    val model: SettingsViewModel = viewModel(factory = ServiceLocator.factory)
    val state by model.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { model.events.collect { onReset() } }
    SettingsScreen(state, model::setLarge, model::setSound, model::showReset, model::dismissReset, model::reset, model::retry, onBack, onMenu)
}

@Composable
fun SettingsScreen(state: SettingsUiState, onLarge: (Boolean) -> Unit, onSound: (Boolean) -> Unit, onShowReset: () -> Unit, onDismissReset: () -> Unit, onReset: () -> Unit, onRetry: () -> Unit, onBack: () -> Unit, onMenu: () -> Unit) {
    AppBackground {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            ScreenHeader(R.string.settings, onBack)
            StatusMessage(state.error, state.loading || state.saving || state.resetting, onRetry)
            ClayCard(Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.reading_title), style = MaterialTheme.typography.titleLarge)
                Text(stringResource(R.string.reading_body), style = MaterialTheme.typography.bodyMedium, color = MutedInk)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(false, true).forEach { large ->
                        val description = stringResource(if (state.settings.largeText == large) R.string.selected else R.string.not_selected)
                        SecondaryButton(if (large) R.string.large else R.string.standard, { onLarge(large) }, Modifier.weight(1f).semantics { stateDescription = description }, enabled = !state.saving && !state.loading)
                    }
                }
                Text(stringResource(R.string.preview_label), style = MaterialTheme.typography.labelSmall, color = Terracotta)
                Text(stringResource(R.string.proverb_001), style = MaterialTheme.typography.bodyLarge.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Serif))
            }
            val sound = stringResource(if (state.settings.soundEnabled) R.string.sound_on else R.string.sound_off)
            ClayCard(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().heightIn(min = 64.dp).semantics { stateDescription = sound }
                    .toggleable(value = state.settings.soundEnabled, enabled = !state.saving && !state.loading, role = Role.Switch, onValueChange = onSound), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(R.string.sound_title), style = MaterialTheme.typography.titleLarge)
                        Text(stringResource(R.string.sound_body), style = MaterialTheme.typography.bodyMedium, color = MutedInk)
                    }
                    Switch(checked = state.settings.soundEnabled, onCheckedChange = null)
                }
            }
            ClayCard(Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.reset_title), style = MaterialTheme.typography.titleLarge, color = DeepClay)
                Text(stringResource(R.string.reset_body), style = MaterialTheme.typography.bodyMedium)
                SecondaryButton(R.string.reset, onShowReset, enabled = !state.resetting && !state.saving)
            }
            Text(stringResource(R.string.local_only), style = MaterialTheme.typography.bodyMedium, color = MutedInk)
            SecondaryButton(R.string.menu, onMenu, Modifier.fillMaxWidth())
        }
        if (state.resetDialog) ResetArchiveDialog(state.resetting, onReset, onDismissReset)
    }
}
