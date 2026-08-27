package br.com.b256.domain.interfaces

import br.com.b256.domain.entities.enums.Theme
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repository do `:domain`, exemplo de referência para novos repositories.
 *
 * O `:domain` só declara a interface; a implementação concreta fica em `:data`
 * ([br.com.b256.data.repositories.SettingsRepositoryImpl]) e é exposta via Hilt `@Binds`
 * (`data/di/RepositoryModule.kt`). Isso mantém o `:domain` sem depender de nenhum detalhe de
 * infraestrutura (DataStore, Room, Retrofit, ...).
 */
interface SettingsRepository {
    fun getTheme(): Flow<Theme>

    suspend fun setTheme(value: Theme)
}
