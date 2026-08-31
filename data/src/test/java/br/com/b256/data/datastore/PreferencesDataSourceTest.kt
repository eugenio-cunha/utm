package br.com.b256.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import br.com.b256.domain.entities.enums.Datum
import br.com.b256.domain.entities.enums.Theme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Teste de referência para fontes de dados sobre DataStore: por não depender de `Context`
 * ([PreferenceDataStoreFactory.create] recebe só um `produceFile`), roda como teste unitário puro
 * (JVM), com um arquivo temporário em vez de instrumentação em dispositivo/emulador.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PreferencesDataSourceTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val testScope = TestScope(UnconfinedTestDispatcher())
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dataSource: PreferencesDataSource

    @Before
    fun setUp() {
        dataStore =
            PreferenceDataStoreFactory.create(
                scope = testScope,
                produceFile = { temporaryFolder.newFile("test.preferences_pb") },
            )
        dataSource = PreferencesDataSource(dataStore)
    }

    @Test
    fun `getTheme retorna FOLLOW_SYSTEM quando nada foi salvo ainda`() =
        testScope.runTest {
            dataSource.getTheme().test {
                assertEquals(Theme.FOLLOW_SYSTEM, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `setTheme persiste o valor e getTheme passa a refleti-lo`() =
        testScope.runTest {
            dataSource.setTheme(Theme.DARK)

            dataSource.getTheme().test {
                assertEquals(Theme.DARK, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getDatum retorna WGS84 quando nada foi salvo ainda`() =
        testScope.runTest {
            dataSource.getDatum().test {
                assertEquals(Datum.WGS84, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `setDatum persiste o valor e getDatum passa a refleti-lo`() =
        testScope.runTest {
            dataSource.setDatum(Datum.SAD69)

            dataSource.getDatum().test {
                assertEquals(Datum.SAD69, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
}
