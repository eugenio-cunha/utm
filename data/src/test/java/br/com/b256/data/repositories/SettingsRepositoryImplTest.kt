package br.com.b256.data.repositories

import app.cash.turbine.test
import br.com.b256.data.datastore.PreferencesDataSource
import br.com.b256.domain.entities.enums.Datum
import br.com.b256.domain.entities.enums.Theme
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Teste de referência para implementações de repository: mocka a fonte de dados
 * ([PreferencesDataSource]) via MockK e verifica que a implementação apenas delega, sem lógica
 * própria — se um repository real tiver alguma transformação, é aqui que ela seria testada.
 */
class SettingsRepositoryImplTest {
    private val dataSource = mockk<PreferencesDataSource>()
    private val repository = SettingsRepositoryImpl(dataSource)

    @Test
    fun `getTheme repassa a emissao da fonte de dados`() =
        runTest {
            every { dataSource.getTheme() } returns flowOf(Theme.DARK)

            repository.getTheme().test {
                assertEquals(Theme.DARK, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `setTheme delega a persistencia para a fonte de dados`() =
        runTest {
            coEvery { dataSource.setTheme(Theme.LIGHT) } returns Unit

            repository.setTheme(Theme.LIGHT)

            coVerify(exactly = 1) { dataSource.setTheme(Theme.LIGHT) }
        }

    @Test
    fun `getDatum repassa a emissao da fonte de dados`() =
        runTest {
            every { dataSource.getDatum() } returns flowOf(Datum.SAD69)

            repository.getDatum().test {
                assertEquals(Datum.SAD69, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `setDatum delega a persistencia para a fonte de dados`() =
        runTest {
            coEvery { dataSource.setDatum(Datum.SAD69) } returns Unit

            repository.setDatum(Datum.SAD69)

            coVerify(exactly = 1) { dataSource.setDatum(Datum.SAD69) }
        }
}
