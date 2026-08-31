package br.com.b256.data.database.mapper

import br.com.b256.data.database.entities.TelemetryEntity
import br.com.b256.domain.entities.Telemetry
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Instant

class TelemetryMapperTest {
    private val entity = TelemetryEntity(id = "1", success = true, date = Instant.fromEpochMilliseconds(1_000))
    private val domain = Telemetry(id = "1", success = true, date = Instant.fromEpochMilliseconds(1_000))

    @Test
    fun `asDomain converte TelemetryEntity para Telemetry`() {
        assertEquals(domain, entity.asDomain())
    }

    @Test
    fun `asEntity converte Telemetry para TelemetryEntity`() {
        assertEquals(entity, domain.asEntity())
    }

    @Test
    fun `listas sao convertidas elemento a elemento`() {
        assertEquals(listOf(domain), listOf(entity).asDomain())
        assertEquals(listOf(entity), listOf(domain).asEntities())
    }
}
