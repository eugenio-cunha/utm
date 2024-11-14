package br.com.b256.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.b256.core.model.Settings
import br.com.b256.core.model.Theme
import br.com.core.b256.domain.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import br.com.b256.feature.settings.SettingsUiState.Loading
import br.com.b256.feature.settings.SettingsUiState.Success
import br.com.core.b256.domain.SetThemeUseCase
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val setThemeUseCase: SetThemeUseCase,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = getSettingsUseCase().map {
        Success(settings = it)
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = Loading,
    )

    fun onChangeTheme(value: Theme) {
        viewModelScope.launch {
            setThemeUseCase(value = value)
        }
    }
}

sealed interface SettingsUiState {
    data object Loading : SettingsUiState

    data class Success(val settings: Settings) : SettingsUiState
}
