package br.com.b256.feature.utm

import android.location.Location

sealed interface UtmUiState {
    data object Loading : UtmUiState

    data class Success(
        val location: Location,
    ) : UtmUiState

    data object Empty : UtmUiState
}
