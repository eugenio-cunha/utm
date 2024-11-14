package br.com.b256.core.data.repository

import br.com.b256.core.model.Settings
import br.com.b256.core.model.Theme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<Settings>

    suspend fun setSettings(value: Settings)

    fun getTheme(): Flow<Theme>

    suspend fun setTheme(value: Theme)
}
