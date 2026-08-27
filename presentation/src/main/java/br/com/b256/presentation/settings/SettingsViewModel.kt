package br.com.b256.presentation.settings

import androidx.lifecycle.viewModelScope
import br.com.b256.domain.entities.enums.Theme
import br.com.b256.domain.usecases.GetThemeUseCase
import br.com.b256.domain.usecases.SetThemeUseCase
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import br.com.b256.presentation.settings.SettingsUiState.Success
import br.com.b256.presentation.settings.SettingsUiState.Loading

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = getThemeUseCase().map {
        Success(theme = it)
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

    data class Success(val theme: Theme) : SettingsUiState
}
