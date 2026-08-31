package br.com.b256.data.repositories

import br.com.b256.data.datastore.PreferencesDataSource
import br.com.b256.domain.entities.enums.Datum
import br.com.b256.domain.entities.enums.Theme
import br.com.b256.domain.interfaces.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementação de referência de [SettingsRepository]: delega para a fonte de dados concreta
 * ([PreferencesDataSource], baseada em DataStore) sem expor esse detalhe ao `:domain`.
 *
 * `internal`, pois só é acessada via a interface [SettingsRepository], amarrada em
 * `data/di/RepositoryModule.kt`. Um novo repository deve seguir o mesmo padrão: implementação
 * `internal` em `data/repositories`, contrato público em `domain/interfaces`, ligação via `@Binds`.
 */
internal class SettingsRepositoryImpl
    @Inject
    constructor(
        private val dataStore: PreferencesDataSource,
    ) : SettingsRepository {
        override fun getTheme(): Flow<Theme> = dataStore.getTheme()

        override suspend fun setTheme(value: Theme) {
            dataStore.setTheme(theme = value)
        }

        override fun getDatum(): Flow<Datum> = dataStore.getDatum()

        override suspend fun setDatum(value: Datum) {
            dataStore.setDatum(datum = value)
        }
    }
