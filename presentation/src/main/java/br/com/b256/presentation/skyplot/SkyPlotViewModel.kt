package br.com.b256.presentation.skyplot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.b256.domain.usecases.ObserveGnssStatusUseCase
import br.com.b256.domain.usecases.ObserveLocationUseCase
import br.com.b256.domain.usecases.ObserveOrientationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * `ViewModel` de referência para uma feature: `@HiltViewModel` + `@Inject constructor`, injetado
 * na tela via `hiltViewModel()` (ver [SkyPlotScreen]). Casos de uso do
 * `:domain` seriam injetados aqui como dependências do construtor, como em
 * [MainActivityViewModel].
 */
@HiltViewModel
class SkyPlotViewModel @Inject constructor(
    private val observeLocationUseCase: ObserveLocationUseCase,
    private val observeGnssStatusUseCase: ObserveGnssStatusUseCase,
    private val observeOrientationUseCase: ObserveOrientationUseCase,
) : ViewModel() {
    // Ações que podem ser disparadas para atualizar as informações de localização.
    private val refreshSignal = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit)
    }

    // Estado da localização atualizada.
    val locationState = refreshSignal
        .flatMapLatest { observeLocationUseCase() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    // Estado do status do sinal Gnss.
    @OptIn(ExperimentalCoroutinesApi::class)
    val gnssStatus = refreshSignal
        .flatMapLatest { observeGnssStatusUseCase() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    // Estado da orientação do dispositivo.
    @OptIn(ExperimentalCoroutinesApi::class)
    val orientation = refreshSignal
        .flatMapLatest { observeOrientationUseCase() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    /**
     * Atualiza as informações de localização.
     * */
    fun refresh() {
        viewModelScope.launch {
            refreshSignal.emit(Unit)
        }
    }

    fun onClick() {
        print("onClick")
    }
}
