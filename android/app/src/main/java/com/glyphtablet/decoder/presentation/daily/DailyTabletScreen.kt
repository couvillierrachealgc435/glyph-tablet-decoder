package com.glyphtablet.decoder.presentation.daily

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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun DailyTabletRoute(onBack: () -> Unit, onPlay: () -> Unit, onMenu: () -> Unit, preparing: Boolean) {
    val model: DailyTabletViewModel = viewModel(factory = ServiceLocator.factory)
    val state by model.uiState.collectAsStateWithLifecycle()
    val owner = LocalLifecycleOwner.current
    DisposableEffect(owner) {
        model.refresh()
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_RESUME) model.refresh() }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    DailyTabletScreen(state, onBack, onPlay, onMenu, model::refresh, preparing)
}

@Composable
fun DailyTabletScreen(state: DailyTabletUiState, onBack: () -> Unit, onPlay: () -> Unit, onMenu: () -> Unit, onRetry: () -> Unit, preparing: Boolean) {
    val id = state.tabletId
    val completed = state.date in state.snapshot.dailyDates
    val status = stringResource(if (completed) R.string.decoded_today else R.string.ready_today)
    AppBackground {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            ScreenHeader(R.string.daily_tablet, onBack)
            Text(stringResource(R.string.daily_eyebrow), style = MaterialTheme.typography.labelSmall, color = Terracotta)
            Text(stringResource(R.string.daily_date, LocalDate.parse(state.date).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))), style = MaterialTheme.typography.bodyLarge)
            StatusMessage(state.error, state.loading, onRetry)
            ClayCard(Modifier.fillMaxWidth().semantics { stateDescription = status }) {
                Image(painterResource(R.drawable.logo), null, Modifier.size(144.dp).align(Alignment.CenterHorizontally), contentScale = ContentScale.Fit)
                Text(stringResource(R.string.tablet_number, id), style = MaterialTheme.typography.headlineMedium)
                Text(stringResource(TextResources.chapters[(id - 1) / 20]), style = MaterialTheme.typography.titleLarge)
                Text(status, style = MaterialTheme.typography.bodyLarge, color = Terracotta)
                Text(stringResource(R.string.daily_hint), style = MaterialTheme.typography.bodyLarge)
            }
            Text(stringResource(R.string.daily_explainer), style = MaterialTheme.typography.bodyMedium, color = MutedInk)
            PrimaryButton(if (completed) R.string.play_again else R.string.play, onPlay, enabled = !state.loading && !state.error && !preparing)
            SecondaryButton(R.string.menu, onMenu, Modifier.fillMaxWidth())
            Text(stringResource(R.string.daily_count, state.snapshot.dailyDates.size), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
