package com.glyphtablet.decoder.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glyphtablet.decoder.domain.game.CipherRules
import com.glyphtablet.decoder.domain.model.*
import com.glyphtablet.decoder.domain.repository.ArchiveRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GameOverUiState(
    val boardId: String = "", val tabletId: Int = 1, val origin: String = "T", val solved: Boolean = false,
    val practice: Boolean = false, val daily: Boolean = false, val time: Long = 0L, val hintsUsed: Int = 0,
    val count: Int = 0, val decodedSigns: Int = 0, val totalSigns: Int = 0,
    val nextAvailable: Boolean = false, val loading: Boolean = true, val error: Boolean = false, val missing: Boolean = false
)

class GameOverViewModel(private val repository: ArchiveRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(GameOverUiState())
    val uiState = _uiState.asStateFlow()
    fun show(boardId: String) {
        _uiState.update { it.copy(boardId = boardId, loading = true, error = false) }
        viewModelScope.launch {
            try {
                val snapshot = repository.load()
                val board = snapshot.sessions[boardId]
                if (board == null) { _uiState.update { it.copy(loading = false, missing = true) }; return@launch }
                val tablet = repository.tablets[board.tabletId - 1]
                _uiState.update { it.copy(tabletId = board.tabletId, origin = tablet.origin, solved = board.phase == BoardPhase.Solved,
                    practice = board.mode == BoardMode.Practice, daily = board.dailyDate != null,
                    time = board.elapsedMs, hintsUsed = 2 - board.hintsRemaining, count = snapshot.completions.size,
                    decodedSigns = board.assignments.size, totalSigns = CipherRules.used(tablet).size,
                    nextAvailable = snapshot.completions.size < 200 && board.mode == BoardMode.Archive && board.dailyDate == null,
                    loading = false, error = false, missing = false) }
            } catch (error: Exception) { if (error is kotlinx.coroutines.CancellationException) throw error; _uiState.update { it.copy(loading = false, error = true) } }
        }
    }
    fun retry() { show(_uiState.value.boardId) }
}
