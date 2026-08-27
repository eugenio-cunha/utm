package br.com.b256.data.network.mapper

import br.com.b256.data.network.model.PingResponse
import br.com.b256.domain.entities.Ping
import org.junit.Assert.assertEquals
import org.junit.Test

class PingMapperTest {
    @Test
    fun `asDomain converte PingResponse para Ping preservando os campos`() {
        val response = PingResponse(result = "ok", success = true)

        val domain = response.asDomain()

        assertEquals(Ping(result = "ok", success = true), domain)
    }
}
