package br.com.b256

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.b256.core.model.Settings
import br.com.core.b256.domain.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import br.com.b256.MainActivityUiState.Loading
import br.com.b256.MainActivityUiState.Success
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = getSettingsUseCase().map {
        Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val settings: Settings) : MainActivityUiState
}
