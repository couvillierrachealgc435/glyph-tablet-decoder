package com.glyphtablet.decoder.presentation.daily

import com.glyphtablet.decoder.domain.model.ArchiveSnapshot

data class DailyTabletUiState(val snapshot: ArchiveSnapshot = ArchiveSnapshot(), val loading: Boolean = true, val error: Boolean = false, val date: String = java.time.LocalDate.now().toString(), val tabletId: Int = 1)
