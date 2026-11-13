package com.glyphtablet.decoder.presentation.app

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glyphtablet.decoder.core.di.ServiceLocator
import com.glyphtablet.decoder.core.navigation.Screen
import com.glyphtablet.decoder.core.theme.AppTheme
import com.glyphtablet.decoder.core.ui.StatusMessage
import com.glyphtablet.decoder.presentation.splash.SplashRoute
import com.glyphtablet.decoder.presentation.menu.MenuRoute
import com.glyphtablet.decoder.presentation.game.GameRoute
import com.glyphtablet.decoder.presentation.game.GameViewModel
import com.glyphtablet.decoder.presentation.gameover.GameOverRoute
import com.glyphtablet.decoder.presentation.codex.GlyphCodexRoute
import com.glyphtablet.decoder.presentation.daily.DailyTabletRoute
import com.glyphtablet.decoder.presentation.stats.StatsRoute
import com.glyphtablet.decoder.presentation.settings.SettingsRoute

@Composable
fun AppRoot() {
    val model: AppViewModel = viewModel(factory = ServiceLocator.factory)
    val gameModel: GameViewModel = viewModel(factory = ServiceLocator.factory)
    val screen by model.screen.collectAsStateWithLifecycle()
    val state by model.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as? Activity
    SideEffect {
        activity?.let {
            val controller = WindowCompat.getInsetsController(it.window, it.window.decorView)
            controller.isAppearanceLightStatusBars = screen != Screen.Splash
            controller.isAppearanceLightNavigationBars = screen != Screen.Splash
        }
    }
    BackHandler { if (!model.back()) activity?.finish() }
    AppTheme(large = state.largeText) {
        Box(Modifier.fillMaxSize()) {
            AnimatedContent(targetState = screen, transitionSpec = { fadeIn(tween(240)) togetherWith fadeOut(tween(240)) }, label = "archive navigation") { current ->
                when (current) {
                    Screen.Splash -> SplashRoute(model::finishSplash)
                    Screen.Menu -> MenuRoute({ model.play(tabletId = it) }, { model.navigate(Screen.DailyTablet) }, { model.navigate(Screen.GlyphCodex(it)) }, { model.navigate(Screen.Stats) }, { model.navigate(Screen.Settings) }, state.preparing)
                    is Screen.Game -> GameRoute(current.boardId, model::showResult, { if (!model.back()) activity?.finish() }, { model.navigate(Screen.GlyphCodex(it, true)) }, { model.play(replayBoardId = it) }, model::menu)
                    is Screen.GameOver -> GameOverRoute(current.boardId, { model.play(replayBoardId = current.boardId) }, { model.play(next = true) }, model::menu)
                    is Screen.GlyphCodex -> GlyphCodexRoute(current.chapter, current.fromBoard, { model.back() }, { model.play(tabletId = it) })
                    Screen.DailyTablet -> DailyTabletRoute({ model.back() }, { model.play(daily = true) }, model::menu, state.preparing)
                    Screen.Stats -> StatsRoute({ model.back() }, { model.play() }, model::menu)
                    Screen.Settings -> SettingsRoute({ model.back() }, model::menu, { gameModel.clear(); model.menu() })
                }
            }
            if (state.preparing) LinearProgressIndicator(Modifier.align(Alignment.TopCenter).safeDrawingPadding().fillMaxWidth())
            if (state.error) Column(Modifier.align(Alignment.TopCenter).safeDrawingPadding().padding(16.dp)) {
                StatusMessage(true, false, model::retry)
            }
        }
    }
}
