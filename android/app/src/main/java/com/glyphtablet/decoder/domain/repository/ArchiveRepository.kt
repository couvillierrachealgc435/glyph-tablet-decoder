package com.glyphtablet.decoder.domain.repository

import com.glyphtablet.decoder.domain.model.ArchiveSnapshot
import com.glyphtablet.decoder.domain.model.Tablet
import kotlinx.coroutines.flow.StateFlow

interface ArchiveRepository {
    val tablets: List<Tablet>
    val snapshot: StateFlow<ArchiveSnapshot>
    suspend fun load(): ArchiveSnapshot
    suspend fun update(transform: (ArchiveSnapshot) -> ArchiveSnapshot): ArchiveSnapshot
    suspend fun reset(): ArchiveSnapshot
}
