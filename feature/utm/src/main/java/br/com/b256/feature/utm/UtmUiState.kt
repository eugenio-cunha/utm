package br.com.b256.feature.utm

import android.location.Location
import br.com.b256.core.ui.base.UiState

/**
 * Representa o estado da interface do usuário (UI) para a funcionalidade de UTM (Universal Transverse Mercator).
 *
 * @property location A localização geográfica atual utilizada para o cálculo das coordenadas UTM.
 * @property isLoading Indica se a tela está em processo de carregamento ou processamento de dados.
 */
internal data class UtmUiState(
    val location: Location? = null,
    override val isLoading: Boolean = false,
) : UiState
