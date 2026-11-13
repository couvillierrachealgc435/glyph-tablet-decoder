package com.glyphtablet.decoder.presentation.game

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glyphtablet.decoder.R
import com.glyphtablet.decoder.domain.game.*
import com.glyphtablet.decoder.domain.model.*
import com.glyphtablet.decoder.domain.repository.ArchiveRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.UUID

class GameViewModel(private val repository: ArchiveRepository, private val now: () -> Long = { SystemClock.elapsedRealtime() }) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState = _uiState.asStateFlow()
    private val _events = Channel<GameEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    private var session: BoardSession? = null
    private val retainedSessions = mutableMapOf<String, BoardSession>()
    private val failedBoards = mutableSetOf<String>()
    private val terminalBoards = mutableSetOf<String>()
    private var ticker: Job? = null
    private var transaction: Job? = null
    private var activation: Job? = null
    private var visible = false
    private var lastTick = 0L
    private var pendingTerminal = false

    fun activate(boardId: String, resumed: Boolean = true) {
        visible = resumed
        val retainedError = boardId in failedBoards
        if (session?.boardId != boardId) {
            stopTicker()
            session?.let { retainedSessions[it.boardId] = it }
            transaction?.cancel()
            session = null
            pendingTerminal = boardId in terminalBoards
            _uiState.update { GameUiState(boardId = boardId) }
        }
        activation?.cancel()
        activation = viewModelScope.launch {
            _uiState.update { it.copy(boardId = boardId, loading = true, error = retainedError) }
            try {
                val snapshot = repository.load()
                val loaded = if (session?.boardId == boardId) session else retainedSessions[boardId] ?: snapshot.sessions[boardId]
                requireNotNull(loaded)
                val tablet = repository.tablets[loaded.tabletId - 1]
                val recoverTransaction = transaction?.isActive != true && !pendingTerminal && loaded.phase != BoardPhase.Solved
                val recoveredSolved = recoverTransaction && CipherRules.isSolved(tablet, loaded)
                val recoveredIncorrect = recoverTransaction && loaded.phase == BoardPhase.Incorrect
                session = loaded.copy(
                    selected = loaded.selected?.takeIf { it !in loaded.assignments } ?: CipherRules.next(tablet, loaded),
                    phase = when {
                        recoveredSolved -> BoardPhase.Solved
                        recoveredIncorrect || pendingTerminal -> loaded.phase
                        recoverTransaction -> BoardPhase.Active
                        loaded.phase == BoardPhase.Paused -> BoardPhase.Active
                        else -> loaded.phase
                    }
                )
                if ((recoveredSolved || recoveredIncorrect) && !retainedError) {
                    session = session?.copy(phase = if (recoveredSolved) BoardPhase.Solved else BoardPhase.Paused, attemptId = UUID.randomUUID().toString())
                    pendingTerminal = true
                    terminalBoards.add(boardId)
                }
                if (pendingTerminal && !retainedError && !persist()) return@launch
                publish(message = if (loaded.phase == BoardPhase.Paused && !pendingTerminal) null else _uiState.value.message, error = retainedError)
                if (visible && !retainedError) { if (pendingTerminal) emitResult() else resume() }
            } catch (error: Exception) { if (error is kotlinx.coroutines.CancellationException) throw error; _uiState.update { it.copy(loading = false, error = true) } }
        }
    }

    private fun publish(message: Int? = _uiState.value.message, error: Boolean = _uiState.value.error, saving: Boolean = false) {
        val board = session ?: return
        retainedSessions[board.boardId] = board
        if (error) failedBoards.add(board.boardId) else failedBoards.remove(board.boardId)
        val tablet = repository.tablets[board.tabletId - 1]
        val used = CipherRules.used(tablet)
        val counts = tablet.answer.filter { it in 'A'..'Z' }.groupingBy { CipherRules.glyph(it, tablet.chapter) }.eachCount()
        val words = tablet.answer.split(' ').map { word -> word.map { char ->
            if (char in 'A'..'Z') {
                val glyph = CipherRules.glyph(char, tablet.chapter)
                GlyphCellState(glyph, letter = board.assignments[glyph], occurrences = counts[glyph] ?: 0)
            } else GlyphCellState(null, punctuation = char)
        } }
        val active = board.phase == BoardPhase.Active && !saving && !error
        _uiState.update { it.copy(boardId = board.boardId, tabletId = board.tabletId, chapter = tablet.chapter,
            words = words, selected = board.selected, hintsRemaining = board.hintsRemaining, elapsedMs = board.elapsedMs,
            phase = board.phase, loading = false, saving = saving, error = error, message = message,
            solvedSigns = board.assignments.size, totalSigns = used.size,
            keys = ('A'..'Z').map { letter -> LetterKeyState(letter, active && board.selected != null && letter !in board.assignments.values && letter !in board.rejected[board.selected].orEmpty()) }) }
    }

    fun select(glyph: Int) {
        val board = session ?: return
        if (board.phase != BoardPhase.Active || _uiState.value.saving || _uiState.value.error || glyph in board.assignments) return
        session = board.copy(selected = glyph)
        publish(message = null)
    }

    fun guess(letter: Char) { assign(letter, false) }
    fun hint() {
        val board = session ?: return
        val tablet = repository.tablets[board.tabletId - 1]
        val glyph = board.selected ?: CipherRules.next(tablet, board) ?: return
        assign(CipherRules.letter(glyph, tablet.chapter), true)
    }

    private fun assign(letter: Char, hint: Boolean) {
        if (_uiState.value.loading || _uiState.value.error || _uiState.value.saving || transaction?.isActive == true) return
        if (session == null) return
        checkpointTime()
        val board = session ?: return
        val tablet = repository.tablets[board.tabletId - 1]
        val changed = SessionReducer.assign(tablet, board, letter, hint)
        if (changed == board) return
        session = changed
        val incorrect = changed.phase == BoardPhase.Incorrect
        publish(if (incorrect) R.string.incorrect_message else R.string.correct_message, saving = true)
        transaction = viewModelScope.launch {
            val saved = persist()
            if (!saved) return@launch
            if (visible && !incorrect && repository.snapshot.value.settings.soundEnabled) _events.send(GameEvent.SuccessSound)
            delay(if (incorrect) 220L else 180L)
            val current = session ?: return@launch
            val solved = CipherRules.isSolved(tablet, current)
            if (incorrect || solved) {
                stopTicker()
                session = (session ?: current).copy(phase = if (solved) BoardPhase.Solved else BoardPhase.Paused, attemptId = UUID.randomUUID().toString())
                pendingTerminal = true
                terminalBoards.add(current.boardId)
                if (persist()) emitResult()
            } else {
                session = current.copy(phase = if (visible) BoardPhase.Active else BoardPhase.Paused)
                publish()
                persist()
            }
        }
    }

    private suspend fun emitResult() {
        val board = session ?: return
        val attempt = board.attemptId ?: return
        if (visible && pendingTerminal) {
            pendingTerminal = false
            terminalBoards.remove(board.boardId)
            _events.send(GameEvent.Result(board.boardId, attempt))
        }
    }

    private suspend fun persist(): Boolean {
        val board = session ?: return false
        return try {
            repository.update { SessionReducer.store(it, board, repository.tablets[board.tabletId - 1]) }
            failedBoards.remove(board.boardId)
            if (session?.boardId == board.boardId) publish(error = false)
            true
        } catch (error: Exception) {
            if (error is kotlinx.coroutines.CancellationException) throw error
            failedBoards.add(board.boardId)
            if (session?.boardId == board.boardId) { stopTicker(); publish(error = true) }
            false
        }
    }

    fun retry() {
        if (session == null) { activate(_uiState.value.boardId); return }
        if (transaction?.isActive == true) return
        transaction = viewModelScope.launch {
            if (persist()) {
                if (pendingTerminal) emitResult()
                else {
                    val board = session ?: return@launch
                    val tablet = repository.tablets[board.tabletId - 1]
                    if (board.phase == BoardPhase.Incorrect || CipherRules.isSolved(tablet, board)) {
                        session = board.copy(phase = if (CipherRules.isSolved(tablet, board)) BoardPhase.Solved else BoardPhase.Paused, attemptId = UUID.randomUUID().toString())
                        pendingTerminal = true
                        terminalBoards.add(board.boardId)
                        if (persist()) emitResult()
                    } else { session = board.copy(phase = if (visible) BoardPhase.Active else BoardPhase.Paused); publish(); if (visible) resume() }
                }
            }
        }
    }

    fun resume() {
        visible = true
        val board = session ?: return
        if (_uiState.value.error) return
        if (pendingTerminal) { viewModelScope.launch { emitResult() }; return }
        if (board.phase == BoardPhase.Solved || ticker?.isActive == true) return
        if (board.phase == BoardPhase.Paused) session = board.copy(phase = BoardPhase.Active)
        lastTick = now()
        publish()
        ticker = viewModelScope.launch {
            var ticks = 0
            while (isActive) {
                delay(1000L)
                checkpointTime()
                publish()
                if (++ticks % 5 == 0 && transaction?.isActive != true) persist()
            }
        }
    }

    private fun checkpointTime() {
        if (ticker?.isActive == true) {
            val currentTime = now()
            session = session?.let { it.copy(elapsedMs = it.elapsedMs + (currentTime - lastTick).coerceAtLeast(0)) }
            lastTick = currentTime
        }
    }

    private fun stopTicker() { checkpointTime(); ticker?.cancel(); ticker = null }

    fun pause() {
        visible = false
        stopTicker()
        session = session?.let { if (it.phase == BoardPhase.Active) it.copy(phase = BoardPhase.Paused) else it }
        publish()
        viewModelScope.launch { persist() }
    }

    fun clear() {
        ticker?.cancel()
        transaction?.cancel()
        activation?.cancel()
        session = null
        pendingTerminal = false
        retainedSessions.clear()
        failedBoards.clear()
        terminalBoards.clear()
        while (_events.tryReceive().isSuccess) Unit
        _uiState.update { GameUiState() }
    }
}
