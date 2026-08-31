package br.com.b256.data.database.util

import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InstantConverterTest {
    private val converter = InstantConverter()

    @Test
    fun `longToInstant converte epoch millis para Instant`() {
        assertEquals(Instant.fromEpochMilliseconds(1_000), converter.longToInstant(1_000))
    }

    @Test
    fun `longToInstant retorna null para entrada null`() {
        assertNull(converter.longToInstant(null))
    }

    @Test
    fun `instantToLong converte Instant para epoch millis`() {
        assertEquals(1_000L, converter.instantToLong(Instant.fromEpochMilliseconds(1_000)))
    }

    @Test
    fun `instantToLong retorna null para entrada null`() {
        assertNull(converter.instantToLong(null))
    }
}
