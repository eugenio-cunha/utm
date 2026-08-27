package br.com.b256.domain.usecases

import app.cash.turbine.test
import br.com.b256.domain.entities.enums.Theme
import br.com.b256.domain.interfaces.SettingsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Teste de referência para casos de uso do `:domain`: mocka o repository via MockK e usa Turbine
 * para verificar a emissão do `Flow` retornado, sem precisar de nenhuma dependência de Android.
 */
class GetThemeUseCaseTest {
    private val repository = mockk<SettingsRepository>()
    private val useCase = GetThemeUseCase(repository)

    @Test
    fun `invoke repassa o tema emitido pelo repository`() =
        runTest {
            every { repository.getTheme() } returns flowOf(Theme.DARK)

            useCase().test {
                assertEquals(Theme.DARK, awaitItem())
                awaitComplete()
            }
        }
}
