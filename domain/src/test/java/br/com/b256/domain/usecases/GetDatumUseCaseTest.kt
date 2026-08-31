package br.com.b256.domain.usecases

import app.cash.turbine.test
import br.com.b256.domain.entities.enums.Datum
import br.com.b256.domain.interfaces.SettingsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetDatumUseCaseTest {
    private val repository = mockk<SettingsRepository>()
    private val useCase = GetDatumUseCase(repository)

    @Test
    fun `invoke repassa o datum emitido pelo repository`() =
        runTest {
            every { repository.getDatum() } returns flowOf(Datum.SAD69)

            useCase().test {
                assertEquals(Datum.SAD69, awaitItem())
                awaitComplete()
            }
        }
}
