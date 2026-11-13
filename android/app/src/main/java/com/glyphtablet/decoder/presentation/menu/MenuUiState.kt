package com.glyphtablet.decoder.presentation.menu

import com.glyphtablet.decoder.domain.model.ArchiveSnapshot

data class MenuUiState(val snapshot: ArchiveSnapshot = ArchiveSnapshot(), val loading: Boolean = true, val error: Boolean = false, val expanded: Set<Int> = emptySet(), val nextTabletId: Int = 1)
