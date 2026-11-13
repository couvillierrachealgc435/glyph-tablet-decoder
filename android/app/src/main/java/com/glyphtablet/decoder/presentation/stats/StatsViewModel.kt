package com.glyphtablet.decoder.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glyphtablet.decoder.domain.repository.ArchiveRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StatsViewModel(private val repository: ArchiveRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState = _uiState.asStateFlow()
    private var observation: Job? = null
    init { refresh() }
    fun refresh() {
        observation?.cancel()
        observation = viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = false) }
            try {
                repository.load()
                repository.snapshot.collect { snapshot -> _uiState.update { it.copy(snapshot = snapshot, averageMs = if (snapshot.completions.isEmpty()) 0 else snapshot.completions.values.sumOf { it.elapsedMs } / snapshot.completions.size, fastestMs = snapshot.completions.values.minOfOrNull { it.elapsedMs } ?: 0, hintsUsed = snapshot.completions.values.sumOf { it.hintsUsed }, loading = false, error = false) } }
            } catch (error: Exception) { if (error is kotlinx.coroutines.CancellationException) throw error; _uiState.update { it.copy(loading = false, error = true) } }
        }
    }
}
