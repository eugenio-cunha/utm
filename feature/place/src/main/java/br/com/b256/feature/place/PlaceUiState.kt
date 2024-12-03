package br.com.b256.feature.place

import br.com.b256.core.model.Place

sealed interface PlaceUiState {
    data object Loading : PlaceUiState

    data class Success(
        val place: List<Place>
    ): PlaceUiState

    data object Empty: PlaceUiState
}
