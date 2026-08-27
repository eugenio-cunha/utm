package br.com.b256.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * `ViewModel` de referência para uma feature: `@HiltViewModel` + `@Inject constructor`, injetado
 * na tela via `hiltViewModel()` (ver [br.com.b256.presentation.home.HomeScreen]). Casos de uso do
 * `:domain` seriam injetados aqui como dependências do construtor, como em
 * [br.com.b256.gnss.MainActivityViewModel].
 */
@HiltViewModel
class HomeViewModel
    @Inject
    constructor() : ViewModel() {
        fun onClick() {
            print("onClick")
        }
    }
