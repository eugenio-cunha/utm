package br.com.b256.gnss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.b256.domain.entities.enums.Theme
import br.com.b256.domain.usecases.GetThemeUseCase
import br.com.b256.gnss.MainActivityUiState.Loading
import br.com.b256.gnss.MainActivityUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * `ViewModel` de referência para o padrão "caso de uso → `StateFlow` de UI state": injeta
 * [GetThemeUseCase] e converte seu `Flow` em [MainActivityUiState] com `stateIn`, mantido "quente"
 * por 5s após o último coletor sair (`WhileSubscribed(5_000)`) para sobreviver a mudanças de
 * configuração sem reassinar o Flow de origem. Consumido em [MainActivity].
 */
@HiltViewModel
class MainActivityViewModel
    @Inject
    constructor(
        private val getThemeUseCase: GetThemeUseCase,
    ) : ViewModel() {
        val uiState: StateFlow<MainActivityUiState> =
            getThemeUseCase().map {
                Success(it)
            }.stateIn(
                scope = viewModelScope,
                initialValue = Loading,
                started = SharingStarted.WhileSubscribed(5_000),
            )
    }

/** Estado de UI da [MainActivity]: [Loading] enquanto o tema ainda não foi lido do repositório. */
sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState

    data class Success(val theme: Theme) : MainActivityUiState {
        override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) =
            when (theme) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.FOLLOW_SYSTEM -> isSystemDarkTheme
            }
    }

    /**
     * Retorna `true` se o estado ainda não foi carregado e a tela de abertura deve continuar sendo exibida.
     */
    fun shouldKeepSplashScreen() = this is Loading

    /**
     * Retorna `true` se o tema escuro deve ser usado.
     */
    fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) = isSystemDarkTheme
}
