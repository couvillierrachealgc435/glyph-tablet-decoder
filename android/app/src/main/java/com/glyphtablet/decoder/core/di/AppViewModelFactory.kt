package com.glyphtablet.decoder.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.glyphtablet.decoder.domain.repository.ArchiveRepository
import com.glyphtablet.decoder.presentation.app.AppViewModel
import com.glyphtablet.decoder.presentation.splash.SplashViewModel
import com.glyphtablet.decoder.presentation.menu.MenuViewModel
import com.glyphtablet.decoder.presentation.game.GameViewModel
import com.glyphtablet.decoder.presentation.gameover.GameOverViewModel
import com.glyphtablet.decoder.presentation.codex.GlyphCodexViewModel
import com.glyphtablet.decoder.presentation.daily.DailyTabletViewModel
import com.glyphtablet.decoder.presentation.stats.StatsViewModel
import com.glyphtablet.decoder.presentation.settings.SettingsViewModel

class AppViewModelFactory(private val repository: ArchiveRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        AppViewModel::class.java -> AppViewModel(repository)
        SplashViewModel::class.java -> SplashViewModel()
        MenuViewModel::class.java -> MenuViewModel(repository)
        GameViewModel::class.java -> GameViewModel(repository)
        GameOverViewModel::class.java -> GameOverViewModel(repository)
        GlyphCodexViewModel::class.java -> GlyphCodexViewModel(repository)
        DailyTabletViewModel::class.java -> DailyTabletViewModel(repository)
        StatsViewModel::class.java -> StatsViewModel(repository)
        SettingsViewModel::class.java -> SettingsViewModel(repository)
        else -> throw IllegalArgumentException(modelClass.name)
    } as T
}
