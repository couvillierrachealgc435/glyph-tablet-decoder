package com.glyphtablet.decoder.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glyphtablet.decoder.domain.repository.ArchiveRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MenuViewModel(private val repository: ArchiveRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState = _uiState.asStateFlow()
    private var observation: Job? = null
    init { refresh() }
    fun refresh() {
        observation?.cancel()
        observation = viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = false) }
            try {
                repository.load()
                repository.snapshot.collect { snapshot -> _uiState.update { it.copy(snapshot = snapshot, nextTabletId = com.glyphtablet.decoder.domain.game.CipherRules.continueTablet(snapshot), loading = false, error = false) } }
            } catch (error: Exception) { if (error is kotlinx.coroutines.CancellationException) throw error; _uiState.update { it.copy(loading = false, error = true) } }
        }
    }

    fun toggleChapter(chapter: Int) {
        _uiState.update { it.copy(expanded = if (chapter in it.expanded) it.expanded - chapter else it.expanded + chapter) }
    }
}
