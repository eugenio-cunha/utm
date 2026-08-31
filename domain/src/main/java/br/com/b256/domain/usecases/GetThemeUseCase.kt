package br.com.b256.domain.usecases

import br.com.b256.domain.entities.enums.Theme
import br.com.b256.domain.interfaces.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/**
 * Caso de uso responsável por recuperar o tema atual do aplicativo.
 *
 * Este caso de uso interage com o [SettingsRepository] para fornecer um fluxo ([Flow])
 * reativo do enum [Theme], permitindo que a interface do usuário observe e reaja a
 * mudanças de tema em tempo real.
 *
 * @property repository O repositório utilizado para acessar as configurações do usuário.
 */
class GetThemeUseCase
@Inject
constructor(
    private val repository: SettingsRepository,
) {
    /**
     * Executa o caso de uso para recuperar a preferência de tema atual.
     *
     * @return Um [Flow] que emite o [Theme] selecionado e suas atualizações futuras.
     */
    operator fun invoke(): Flow<Theme> = repository.getTheme()
}
