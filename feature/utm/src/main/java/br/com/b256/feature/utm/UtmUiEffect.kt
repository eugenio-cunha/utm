package br.com.b256.feature.utm

import br.com.b256.core.ui.base.UiEffect

/**
 * Representa os efeitos colaterais da interface do usuário (UI) para a funcionalidade de UTM.
 * Utilizado para disparar eventos únicos que não representam um estado persistente,
 * como a ação de compartilhar um link ou texto gerado.
 */
internal sealed interface UtmUiEffect: UiEffect {
    data class Share(val value: String): UtmUiEffect
}
