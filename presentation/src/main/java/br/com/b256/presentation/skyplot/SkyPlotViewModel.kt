package br.com.b256.presentation.skyplot

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * `ViewModel` de referência para uma feature: `@HiltViewModel` + `@Inject constructor`, injetado
 * na tela via `hiltViewModel()` (ver [SkyPlotScreen]). Casos de uso do
 * `:domain` seriam injetados aqui como dependências do construtor, como em
 * [MainActivityViewModel].
 */
@HiltViewModel
class SkyPlotViewModel
    @Inject
    constructor() : ViewModel() {
        fun onClick() {
            print("onClick")
        }
    }
