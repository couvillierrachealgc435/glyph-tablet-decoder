package com.glyphtablet.decoder.core.di

import android.content.Context
import com.glyphtablet.decoder.data.local.ArchivePreferences
import com.glyphtablet.decoder.data.repository.ArchiveRepositoryImpl
import com.glyphtablet.decoder.domain.repository.ArchiveRepository

object ServiceLocator {
    private var archive: ArchiveRepository? = null
    fun initialize(context: Context) {
        if (archive == null) archive = ArchiveRepositoryImpl(ArchivePreferences(context.applicationContext))
    }
    val factory: AppViewModelFactory get() = AppViewModelFactory(requireNotNull(archive))
}
