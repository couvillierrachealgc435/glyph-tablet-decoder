package com.glyphtablet.decoder.domain.game

import com.glyphtablet.decoder.domain.model.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object CipherRules {
    fun glyph(letter: Char, chapter: Int): Int = (5 * (letter - 'A') + 7 * chapter) % 26
    fun letter(glyph: Int, chapter: Int): Char = ('A'..'Z').first { glyph(it, chapter) == glyph }
    fun used(tablet: Tablet): List<Int> = tablet.answer.filter { it in 'A'..'Z' }.map { glyph(it, tablet.chapter) }.distinct()
    fun next(tablet: Tablet, session: BoardSession): Int? = used(tablet).firstOrNull { it !in session.assignments }
    fun isSolved(tablet: Tablet, session: BoardSession): Boolean = used(tablet).all { session.assignments[it] == letter(it, tablet.chapter) }
    fun nextTablet(snapshot: ArchiveSnapshot): Int = (1..200).firstOrNull { it !in snapshot.completions } ?: 1
    fun continueTablet(snapshot: ArchiveSnapshot): Int = snapshot.lastActive?.takeIf { it !in snapshot.completions } ?: nextTablet(snapshot)
}

object DailyTabletSelector {
    fun select(date: LocalDate): Int = Math.floorMod(ChronoUnit.DAYS.between(LocalDate.of(2026, 1, 1), date), 200L).toInt() + 1
}
