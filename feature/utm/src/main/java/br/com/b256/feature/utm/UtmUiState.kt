package br.com.b256.feature.utm

import android.location.Location
import br.com.b256.core.model.GnssInfo
import br.com.b256.core.model.Orientation
import br.com.b256.core.ui.base.UiState

/**
 * Representa o estado da interface do usuário (UI) para a funcionalidade de UTM (Universal Transverse Mercator).
 *
 */
internal data class UtmUiState(
    val gnssInfo: GnssInfo? = null,
    val orientation: Orientation? = null
)
