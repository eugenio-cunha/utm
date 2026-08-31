package br.com.b256.data.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.com.b256.data.database.RoomDatabase
import br.com.b256.data.database.entities.TelemetryEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.time.Instant

/**
 * Teste de referência para DAOs do Room: banco **em memória** rodando sob Robolectric, em vez de
 * teste instrumentado em dispositivo/emulador — muito mais rápido, sem abrir mão de exercitar o
 * SQLite real por trás do Room.
 *
 * `sdk` fixado explicitamente porque o `compileSdk`/`targetSdk` do projeto (37) ainda não tem
 * suporte no Robolectric usado aqui — ajuste (ou remova) conforme novas versões do Robolectric
 * passem a suportar SDKs mais recentes.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TelemetryDaoTest {
    private lateinit var database: RoomDatabase
    private lateinit var dao: TelemetryDao

    @Before
    fun setUp() {
        database =
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RoomDatabase::class.java,
            ).build()
        dao = database.telemetryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insertTelemetry e getAll retornam o registro inserido`() =
        runTest {
            val entity = TelemetryEntity(id = "1", success = true, date = Instant.fromEpochMilliseconds(1_000))

            dao.insertTelemetry(entity)

            assertEquals(listOf(entity), dao.getAll())
        }

    @Test
    fun `insertTelemetry com o mesmo id substitui o registro (REPLACE)`() =
        runTest {
            dao.insertTelemetry(TelemetryEntity(id = "1", success = false, date = Instant.fromEpochMilliseconds(1_000)))
            dao.insertTelemetry(TelemetryEntity(id = "1", success = true, date = Instant.fromEpochMilliseconds(2_000)))

            val all = dao.getAll()

            assertEquals(1, all.size)
            assertTrue(all.single().success)
        }

    @Test
    fun `deleteTelemetryById remove apenas o registro com o id informado`() =
        runTest {
            dao.insertTelemetry(TelemetryEntity(id = "1", success = true, date = Instant.fromEpochMilliseconds(1_000)))
            dao.insertTelemetry(TelemetryEntity(id = "2", success = true, date = Instant.fromEpochMilliseconds(1_000)))

            dao.deleteTelemetryById("1")

            assertEquals(listOf("2"), dao.getAll().map { it.id })
        }
}
