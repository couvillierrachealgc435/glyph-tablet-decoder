package com.glyphtablet.decoder.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glyphtablet.decoder.domain.model.AppSettings
import com.glyphtablet.decoder.domain.repository.ArchiveRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(val settings: AppSettings = AppSettings(), val loading: Boolean = true, val saving: Boolean = false, val error: Boolean = false, val resetDialog: Boolean = false, val resetting: Boolean = false)

class SettingsViewModel(private val repository: ArchiveRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()
    private val _events = Channel<Unit>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    init { load() }
    fun load() {
        viewModelScope.launch {
            try { val value = repository.load(); _uiState.update { it.copy(settings = value.settings, loading = false, error = false) } }
            catch (error: Exception) { if (error is kotlinx.coroutines.CancellationException) throw error; _uiState.update { it.copy(loading = false, error = true) } }
        }
    }
    fun setLarge(large: Boolean) { save(_uiState.value.settings.copy(largeText = large)) }
    fun setSound(enabled: Boolean) { save(_uiState.value.settings.copy(soundEnabled = enabled)) }
    private fun save(settings: AppSettings) {
        if (_uiState.value.saving || _uiState.value.resetting) return
        _uiState.update { it.copy(settings = settings, saving = true, error = false) }
        viewModelScope.launch {
            try { repository.update { it.copy(settings = settings) }; _uiState.update { it.copy(saving = false) } }
            catch (error: Exception) { if (error is kotlinx.coroutines.CancellationException) throw error; _uiState.update { it.copy(saving = false, error = true) } }
        }
    }
    fun retry() { save(_uiState.value.settings) }
    fun showReset() { _uiState.update { it.copy(resetDialog = true) } }
    fun dismissReset() { if (!_uiState.value.resetting) _uiState.update { it.copy(resetDialog = false) } }
    fun reset() {
        if (_uiState.value.resetting) return
        _uiState.update { it.copy(resetting = true, error = false) }
        viewModelScope.launch {
            try {
                repository.reset()
                _uiState.update { SettingsUiState(loading = false) }
                _events.send(Unit)
            } catch (error: Exception) { if (error is kotlinx.coroutines.CancellationException) throw error; _uiState.update { it.copy(resetting = false, error = true, resetDialog = false) } }
        }
    }
}
