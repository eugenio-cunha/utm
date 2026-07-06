package br.com.b256.feature.utm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.core.b256.domain.ObserveGnssStatusUseCase
import br.com.core.b256.domain.ObserveLocationUseCase
import br.com.core.b256.domain.ObserveOrientationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para a funcionalidade UTM.
 *
 * Este ViewModel é responsável por gerenciar o serviço de localização e fornecer atualizações de
 * localização para a UI. Ele inicia o serviço de localização no construtor e o interrompe quando
 * o ViewModel é limpo.
 *
 * @property application O contexto da aplicação.
 */
@HiltViewModel
internal class UtmViewModel @Inject constructor(
    private val application: Application,
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
            initialValue = null
        )

    // Estado do status do sinal Gnss.
    val gnssStatus = refreshSignal
        .flatMapLatest { observeGnssStatusUseCase() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    // Estado da orientação do dispositivo.
    val orientation = refreshSignal
        .flatMapLatest { observeOrientationUseCase() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    /**
     * Atualiza as informações de localização.
     * */
    fun refresh() {
        viewModelScope.launch {
            refreshSignal.emit(Unit)
        }
    }
}
