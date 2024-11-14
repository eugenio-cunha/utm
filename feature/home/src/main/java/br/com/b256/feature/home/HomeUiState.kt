package br.com.b256.feature.home

import br.com.b256.core.model.Settings

sealed interface HomeUiState {
    /**
     * Carregando novo estado.
     */
    data object Loading : HomeUiState

    /**
     * O novo estado não pôde ser carregado.
     */
    data object LoadFailed : HomeUiState

    /**
     * Novo estado carregado.
     */
    data class Success(val settings: Settings) : HomeUiState {
        val biometrics: Boolean get() = settings.biometrics
    }
}
