package br.com.b256.feature.utm

import br.com.b256.core.ui.base.UiIntent

/**
 * Interface selada que representa as intenções de usuário (actions) para a funcionalidade de UTM.
 *
 * Segue o padrão MVI (Model-View-Intent), encapsulando todas as interações possíveis
 * que podem ser disparadas da interface do usuário para o ViewModel.
 *
 * @property StartService Solicita o início do serviço de monitoramento.
 * @property StopService Solicita a interrupção do serviço de monitoramento.
 * @property Cleared Indica que os dados ou o estado foram limpos.
 * @property Share Solicita o compartilhamento de um valor específico.
 */
internal sealed interface UtmUiIntent: UiIntent {
    object StartService : UtmUiIntent

    object StopService : UtmUiIntent

    object Cleared : UtmUiIntent

    data class Share(val value: String): UtmUiIntent
}
