package br.com.b256.domain.usecases

import br.com.b256.domain.entities.enums.Theme
import br.com.b256.domain.interfaces.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso responsável por definir e persistir a preferência de tema visual do aplicativo.
 *
 * Esta classe encapsula a lógica de negócio para atualizar a seleção de tema do usuário,
 * garantindo que a configuração escolhida seja armazenada corretamente via repositório.
 */
class SetThemeUseCase
@Inject
constructor(
    private val repository: SettingsRepository,
) {
    /**
     * Executa o caso de uso para definir o tema do aplicativo.
     *
     * @param value O novo tema a ser definido.
     */
    suspend operator fun invoke(value: Theme) = repository.setTheme(value = value)
}
