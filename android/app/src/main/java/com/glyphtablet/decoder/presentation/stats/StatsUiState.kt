package com.glyphtablet.decoder.presentation.stats

import com.glyphtablet.decoder.domain.model.ArchiveSnapshot

data class StatsUiState(val snapshot: ArchiveSnapshot = ArchiveSnapshot(), val loading: Boolean = true, val error: Boolean = false, val averageMs: Long = 0, val fastestMs: Long = 0, val hintsUsed: Int = 0)
