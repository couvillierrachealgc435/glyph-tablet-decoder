package com.glyphtablet.decoder.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glyphtablet.decoder.core.config.GameConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SplashUiState(val transitionConsumed: Boolean = false)

class SplashViewModel : ViewModel() {
    private val _finished = MutableStateFlow(false)
    val finished = _finished.asStateFlow()
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()
    init { viewModelScope.launch { delay(GameConfig.LOADER_DURATION_MS); _finished.value = true } }
    fun consume(): Boolean {
        if (!_finished.value || _uiState.value.transitionConsumed) return false
        _uiState.update { it.copy(transitionConsumed = true) }
        return true
    }
}
