package com.glyphtablet.decoder.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glyphtablet.decoder.core.navigation.Screen
import com.glyphtablet.decoder.domain.game.*
import com.glyphtablet.decoder.domain.model.*
import com.glyphtablet.decoder.domain.repository.ArchiveRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

data class AppUiState(val largeText: Boolean = false, val preparing: Boolean = false, val error: Boolean = false)

class AppViewModel(private val repository: ArchiveRepository) : ViewModel() {
    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen = _screen.asStateFlow()
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState = _uiState.asStateFlow()
    private var backStack: List<Screen> = emptyList()
    private val terminalAttempts = mutableSetOf<String>()
    private var retryAction: (() -> Unit)? = null

    init {
        viewModelScope.launch {
            repository.snapshot.collect { snapshot -> _uiState.update { it.copy(largeText = snapshot.settings.largeText) } }
        }
    }

    fun finishSplash() { if (_screen.value == Screen.Splash) _screen.value = Screen.Menu }

    fun navigate(next: Screen) {
        if (_screen.value == next) return
        if (_screen.value != Screen.Splash) backStack = backStack + _screen.value
        _screen.value = next
    }

    fun back(): Boolean {
        if (_screen.value == Screen.Menu || _screen.value == Screen.Splash || backStack.isEmpty()) return false
        _screen.value = backStack.last()
        backStack = backStack.dropLast(1)
        return true
    }

    fun menu() { backStack = emptyList(); _screen.value = Screen.Menu; _uiState.update { it.copy(error = false) } }

    fun showResult(boardId: String, attemptId: String) {
        if (_screen.value == Screen.Game(boardId) && terminalAttempts.add(attemptId)) navigate(Screen.GameOver(boardId, attemptId))
    }

    fun play(tabletId: Int? = null, daily: Boolean = false, replayBoardId: String? = null, next: Boolean = false) {
        if (_uiState.value.preparing) return
        retryAction = { play(tabletId, daily, replayBoardId, next) }
        _uiState.update { it.copy(preparing = true, error = false) }
        viewModelScope.launch {
            try {
                val snapshot = repository.load()
                val previous = replayBoardId?.let { snapshot.sessions[it] }
                val date = if (daily) LocalDate.now().toString() else null
                val id = previous?.tabletId ?: tabletId ?: if (daily) DailyTabletSelector.select(LocalDate.parse(date)) else if (next) CipherRules.nextTablet(snapshot) else CipherRules.continueTablet(snapshot)
                val practice = (previous?.phase == BoardPhase.Solved) || (daily && date in snapshot.dailyDates) || (!daily && id in snapshot.completions)
                val key = when {
                    previous != null && previous.phase != BoardPhase.Solved -> previous.boardId
                    practice -> "practice_${id}_${UUID.randomUUID()}"
                    daily && id in snapshot.completions -> "daily_${date}_$id"
                    else -> "archive_$id"
                }
                val existing = snapshot.sessions[key]
                val session = existing?.copy(dailyDate = date ?: existing.dailyDate) ?: BoardSession(
                    boardId = key, tabletId = id,
                    mode = when { practice -> BoardMode.Practice; daily && id in snapshot.completions -> BoardMode.Daily; else -> BoardMode.Archive },
                    dailyDate = date, selected = CipherRules.used(repository.tablets[id - 1]).firstOrNull()
                )
                repository.update { SessionReducer.store(it, session, repository.tablets[id - 1]) }
                if (replayBoardId != null || next) {
                    while (backStack.lastOrNull() is Screen.Game || backStack.lastOrNull() is Screen.GameOver) backStack = backStack.dropLast(1)
                    _screen.value = Screen.Game(key)
                } else navigate(Screen.Game(key))
            } catch (error: Exception) { if (error is kotlinx.coroutines.CancellationException) throw error; _uiState.update { it.copy(error = true) } }
            _uiState.update { it.copy(preparing = false) }
        }
    }

    fun retry() { retryAction?.invoke() }
}
