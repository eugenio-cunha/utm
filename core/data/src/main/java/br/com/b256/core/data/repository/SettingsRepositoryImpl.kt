package br.com.b256.core.data.repository

import br.com.b256.core.datastore.Preference
import br.com.b256.core.model.Settings
import br.com.b256.core.model.Theme
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val preference: Preference,
) : SettingsRepository {
    override fun getSettings(): Flow<Settings> {
        return preference.getSettings()
    }

    override suspend fun setSettings(value: Settings) {
        preference.setSettings(value = value)
    }

    override fun getTheme(): Flow<Theme> {
        return preference.getTheme()
    }

    override suspend fun setTheme(value: Theme) {
        preference.setTheme(value = value)
    }
}
