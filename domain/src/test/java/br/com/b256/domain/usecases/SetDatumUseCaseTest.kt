package br.com.b256.domain.usecases

import br.com.b256.domain.entities.enums.Datum
import br.com.b256.domain.interfaces.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SetDatumUseCaseTest {
    private val repository = mockk<SettingsRepository>()
    private val useCase = SetDatumUseCase(repository)

    @Test
    fun `invoke delega a persistencia do datum para o repository`() =
        runTest {
            coEvery { repository.setDatum(Datum.SAD69) } returns Unit

            useCase(Datum.SAD69)

            coVerify(exactly = 1) { repository.setDatum(Datum.SAD69) }
        }
}
