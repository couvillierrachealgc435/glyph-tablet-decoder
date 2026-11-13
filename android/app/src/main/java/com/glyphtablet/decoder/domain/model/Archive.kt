package com.glyphtablet.decoder.domain.model

data class Tablet(val id: Int, val proverb: String, val origin: String) {
    val chapter: Int get() = (id - 1) / 20
    val answer: String get() = proverb.uppercase(java.util.Locale.ROOT)
}

enum class BoardMode { Archive, Daily, Practice }
enum class BoardPhase { Active, Revealing, Incorrect, Paused, Solved }

data class BoardSession(
    val boardId: String,
    val tabletId: Int,
    val mode: BoardMode,
    val dailyDate: String? = null,
    val assignments: Map<Int, Char> = emptyMap(),
    val rejected: Map<Int, Set<Char>> = emptyMap(),
    val hintsRemaining: Int = 2,
    val elapsedMs: Long = 0L,
    val selected: Int? = null,
    val phase: BoardPhase = BoardPhase.Active,
    val attemptId: String? = null
)

data class Completion(val elapsedMs: Long, val hintsUsed: Int)
data class LearnedPair(val letter: Char, val tabletId: Int)
data class AppSettings(val largeText: Boolean = false, val soundEnabled: Boolean = true)

data class ArchiveSnapshot(
    val sessions: Map<String, BoardSession> = emptyMap(),
    val completions: Map<Int, Completion> = emptyMap(),
    val learned: Map<Int, Map<Int, LearnedPair>> = emptyMap(),
    val dailyDates: Set<String> = emptySet(),
    val settings: AppSettings = AppSettings(),
    val lastActive: Int? = null,
    val codexChapter: Int = 0,
    val recoveredBoard: Boolean = false
)
