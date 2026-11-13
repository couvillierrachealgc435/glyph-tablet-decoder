package com.glyphtablet.decoder.presentation.game

import com.glyphtablet.decoder.domain.model.BoardPhase

data class GlyphCellState(val glyph: Int?, val punctuation: Char? = null, val letter: Char? = null, val occurrences: Int = 0)
data class LetterKeyState(val letter: Char, val enabled: Boolean)
data class GameUiState(
    val boardId: String = "", val tabletId: Int = 1, val chapter: Int = 0,
    val words: List<List<GlyphCellState>> = emptyList(), val keys: List<LetterKeyState> = emptyList(),
    val selected: Int? = null, val hintsRemaining: Int = 2, val elapsedMs: Long = 0,
    val phase: BoardPhase = BoardPhase.Paused, val loading: Boolean = true, val saving: Boolean = false,
    val error: Boolean = false, val message: Int? = null, val solvedSigns: Int = 0, val totalSigns: Int = 0
)

sealed interface GameEvent {
    data class Result(val boardId: String, val attemptId: String) : GameEvent
    data object SuccessSound : GameEvent
}
