package com.glyphtablet.decoder.presentation.codex

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glyphtablet.decoder.domain.model.ArchiveSnapshot
import com.glyphtablet.decoder.domain.repository.ArchiveRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GlyphCodexUiState(val snapshot: ArchiveSnapshot = ArchiveSnapshot(), val chapter: Int = 0, val loading: Boolean = true, val error: Boolean = false)

class GlyphCodexViewModel(private val repository: ArchiveRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(GlyphCodexUiState())
    val uiState = _uiState.asStateFlow()
    private var job: Job? = null
    fun show(chapter: Int) {
        job?.cancel()
        job = viewModelScope.launch {
            _uiState.update { it.copy(chapter = chapter, loading = true, error = false) }
            try {
                repository.load()
                repository.snapshot.collect { value -> _uiState.update { it.copy(snapshot = value, loading = false) } }
            } catch (error: Exception) { if (error is kotlinx.coroutines.CancellationException) throw error; _uiState.update { it.copy(loading = false, error = true) } }
        }
    }
    fun selectChapter(chapter: Int) {
        _uiState.update { it.copy(chapter = chapter, error = false) }
        viewModelScope.launch {
            try { repository.update { it.copy(codexChapter = chapter) } }
            catch (error: Exception) { if (error is kotlinx.coroutines.CancellationException) throw error; _uiState.update { it.copy(error = true) } }
        }
    }
    fun retry() { selectChapter(_uiState.value.chapter); show(_uiState.value.chapter) }
}
