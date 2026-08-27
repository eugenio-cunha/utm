package br.com.b256.domain.usecases

import br.com.b256.domain.entities.enums.Theme
import br.com.b256.domain.interfaces.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Exemplo de referência para casos de uso do `:domain`.
 *
 * Convenção do projeto: um caso de uso é uma classe com um único método público,
 * `operator fun invoke()`, injetada via construtor e usada como `useCase()` a partir de um
 * `ViewModel` (ver [br.com.b256.gnss.MainActivityViewModel]). Um caso de uso não deve conter
 * lógica de UI nem detalhes de infraestrutura — apenas orquestrar repositories do `:domain`.
 */
class GetThemeUseCase
    @Inject
    constructor(
        private val repository: SettingsRepository,
    ) {
        operator fun invoke(): Flow<Theme> = repository.getTheme()
    }
