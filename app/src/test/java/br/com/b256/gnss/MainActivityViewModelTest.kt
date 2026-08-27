package br.com.b256.gnss

import app.cash.turbine.test
import br.com.b256.domain.entities.enums.Theme
import br.com.b256.domain.usecases.GetThemeUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Teste de referência para `ViewModel`s que expõem `StateFlow` a partir de um caso de uso:
 * substitui o `Dispatchers.Main` (usado internamente por `viewModelScope`) por um dispatcher de
 * teste, mocka [GetThemeUseCase] com MockK e usa Turbine para verificar as emissões do `uiState`.
 *
 * O caso de uso é mocado devolvendo um [MutableSharedFlow] "frio" (sem valor inicial), em vez de
 * `flowOf(...)`: assim conseguimos observar o `Loading` inicial antes de emitir o valor — com uma
 * fonte que já nasce resolvida, a transição Loading → Success aconteceria de forma síncrona,
 * antes do teste conseguir se inscrever no `Flow` para observá-la.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityViewModelTest {
    private val getThemeUseCase = mockk<GetThemeUseCase>()
    private val themeFlow = MutableSharedFlow<Theme>()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { getThemeUseCase() } returns themeFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState comeca em Loading e passa a Success quando o caso de uso emite`() =
        runTest {
            val viewModel = MainActivityViewModel(getThemeUseCase)

            viewModel.uiState.test {
                assertEquals(MainActivityUiState.Loading, awaitItem())

                themeFlow.emit(Theme.DARK)

                assertEquals(MainActivityUiState.Success(Theme.DARK), awaitItem())
            }
        }
}
