package br.com.b256.domain.usecases

import br.com.b256.domain.entities.enums.Datum
import br.com.b256.domain.interfaces.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso responsável por recuperar o datum geodésico atual, usado para exibir
 * latitude/longitude e UTM (ver [Datum]).
 *
 * Este caso de uso interage com o [SettingsRepository] para fornecer um fluxo ([Flow])
 * reativo do enum [Datum], permitindo que a interface do usuário observe e reaja a mudanças
 * de datum em tempo real.
 *
 * @property repository O repositório utilizado para acessar as configurações do usuário.
 */
class GetDatumUseCase
@Inject
constructor(
    private val repository: SettingsRepository,
) {
    /**
     * Executa o caso de uso para recuperar a preferência de datum atual.
     *
     * @return Um [Flow] que emite o [Datum] selecionado e suas atualizações futuras.
     */
    operator fun invoke(): Flow<Datum> = repository.getDatum()
}
