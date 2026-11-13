package com.glyphtablet.decoder.domain.game

import com.glyphtablet.decoder.domain.model.*

object SessionReducer {
    fun assign(tablet: Tablet, session: BoardSession, candidate: Char, hint: Boolean = false): BoardSession {
        val selected = session.selected ?: CipherRules.next(tablet, session) ?: return session
        if (session.phase != BoardPhase.Active || candidate in session.assignments.values || candidate in session.rejected[selected].orEmpty()) return session
        if (hint && session.hintsRemaining == 0) return session
        if (candidate != CipherRules.letter(selected, tablet.chapter)) {
            return session.copy(rejected = session.rejected + (selected to (session.rejected[selected].orEmpty() + candidate)), phase = BoardPhase.Incorrect)
        }
        val assigned = session.copy(assignments = session.assignments + (selected to candidate), hintsRemaining = session.hintsRemaining - if (hint) 1 else 0, phase = BoardPhase.Revealing)
        return assigned.copy(selected = CipherRules.next(tablet, assigned))
    }

    fun store(snapshot: ArchiveSnapshot, session: BoardSession, tablet: Tablet): ArchiveSnapshot {
        val chapterPairs = snapshot.learned[tablet.chapter].orEmpty().toMutableMap()
        session.assignments.forEach { (glyph, letter) ->
            if (letter == CipherRules.letter(glyph, tablet.chapter)) chapterPairs.putIfAbsent(glyph, LearnedPair(letter, tablet.id))
        }
        val solved = session.phase == BoardPhase.Solved && CipherRules.isSolved(tablet, session)
        val completions = if (solved && session.mode != BoardMode.Practice && tablet.id !in snapshot.completions) {
            snapshot.completions + (tablet.id to Completion(session.elapsedMs.coerceAtLeast(1), 2 - session.hintsRemaining))
        } else snapshot.completions
        return snapshot.copy(
            sessions = snapshot.sessions + (session.boardId to session),
            completions = completions,
            learned = snapshot.learned + (tablet.chapter to chapterPairs.toMap()),
            dailyDates = if (solved && session.mode != BoardMode.Practice && session.dailyDate != null) snapshot.dailyDates + session.dailyDate else snapshot.dailyDates,
            lastActive = if (session.mode == BoardMode.Archive) session.tabletId else snapshot.lastActive
        )
    }
}
