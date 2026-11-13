package com.glyphtablet.decoder.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data class Game(val boardId: String) : Screen
    data class GameOver(val boardId: String, val attemptId: String) : Screen
    data class GlyphCodex(val chapter: Int, val fromBoard: Boolean = false) : Screen
    data object DailyTablet : Screen
    data object Stats : Screen
    data object Settings : Screen
}
