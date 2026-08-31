package br.com.b256.domain.usecases

import br.com.b256.domain.entities.enums.Datum
import br.com.b256.domain.interfaces.SettingsRepository
import javax.inject.Inject

/**
 * Caso de uso responsável por definir e persistir o datum geodésico usado para exibir
 * latitude/longitude e UTM (ver [Datum]).
 *
 * Esta classe encapsula a lógica de negócio para atualizar a seleção de datum do usuário,
 * garantindo que a configuração escolhida seja armazenada corretamente via repositório.
 */
class SetDatumUseCase
@Inject
constructor(
    private val repository: SettingsRepository,
) {
    /**
     * Executa o caso de uso para definir o datum geodésico do aplicativo.
     *
     * @param value O novo datum a ser definido.
     */
    suspend operator fun invoke(value: Datum) = repository.setDatum(value = value)
}
