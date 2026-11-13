package com.glyphtablet.decoder.data.repository

import com.glyphtablet.decoder.data.local.ArchivePreferences
import com.glyphtablet.decoder.data.sample.SampleData
import com.glyphtablet.decoder.domain.model.ArchiveSnapshot
import com.glyphtablet.decoder.domain.repository.ArchiveRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class ArchiveRepositoryImpl(private val preferences: ArchivePreferences) : ArchiveRepository {
    override val tablets = SampleData.tablets
    private val mutex = Mutex()
    private var loaded = false
    private val _snapshot = MutableStateFlow(ArchiveSnapshot())
    override val snapshot = _snapshot.asStateFlow()

    override suspend fun load(): ArchiveSnapshot = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (!loaded) { _snapshot.value = preferences.read(); loaded = true }
            _snapshot.value
        }
    }

    override suspend fun update(transform: (ArchiveSnapshot) -> ArchiveSnapshot): ArchiveSnapshot = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (!loaded) { _snapshot.value = preferences.read(); loaded = true }
            val next = transform(_snapshot.value)
            preferences.write(next)
            _snapshot.value = next
            next
        }
    }

    override suspend fun reset(): ArchiveSnapshot = withContext(Dispatchers.IO) {
        mutex.withLock {
            preferences.clear()
            loaded = true
            ArchiveSnapshot().also { _snapshot.value = it }
        }
    }
}
