package br.com.b256.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import br.com.b256.domain.entities.enums.Datum
import br.com.b256.domain.entities.enums.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.time.Instant

/**
 * Fonte de dados de referência sobre Jetpack DataStore (Preferences). Consumida por
 * [SettingsRepositoryImpl]; uma nova preferência simples (flags,
 * strings, etc.) deveria ganhar uma chave em [PreferencesKeys] e os respectivos getter/setter
 * aqui, em vez de acessar o [DataStore] diretamente de fora deste arquivo.
 */
internal class PreferencesDataSource
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        suspend fun setTheme(theme: Theme) {
            dataStore.edit { preferences ->
                preferences[THEME] = theme.value
            }
        }

        fun getTheme(): Flow<Theme> {
            return dataStore.data.map { preferences ->
                if (preferences[THEME].isNullOrBlank()) {
                    Theme.FOLLOW_SYSTEM
                } else {
                    Theme.from(preferences[THEME]!!)
                }
            }
        }

        suspend fun setDatum(datum: Datum) {
            dataStore.edit { preferences ->
                preferences[DATUM] = datum.value
            }
        }

        fun getDatum(): Flow<Datum> {
            return dataStore.data.map { preferences ->
                if (preferences[DATUM].isNullOrBlank()) {
                    Datum.WGS84
                } else {
                    Datum.from(preferences[DATUM]!!)
                }
            }
        }

        companion object PreferencesKeys {
            val THEME = stringPreferencesKey("theme")
            val DATUM = stringPreferencesKey("datum")
        }
    }
