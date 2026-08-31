package br.com.b256.data.network

import br.com.b256.domain.entities.Ping
import dagger.Lazy
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

/**
 * Teste de referência para a camada de rede: em vez de mockar o Retrofit, sobe um [MockWebServer]
 * local e aponta [NetworkImpl] para ele (via o `baseUrl` injetável) — exercita o mesmo caminho de
 * serialização/HTTP usado em produção, cobrindo tanto o caminho de sucesso quanto o de erro.
 */
class NetworkImplTest {
    private val server = MockWebServer()
    private lateinit var network: NetworkImpl

    @Before
    fun setUp() {
        server.start()
        network =
            NetworkImpl(
                json = Json { ignoreUnknownKeys = true },
                okhttp = Lazy<Call.Factory> { OkHttpClient() },
                baseUrl = server.url("/").toString(),
            )
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `getPing converte uma resposta de sucesso para a entidade de dominio`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setBody("""{"result":"ok","success":true}""")
                    .setHeader("Content-Type", "application/json"),
            )

            val result = network.getPing()

            assertEquals(Ping(result = "ok", success = true), result)
        }

    @Test
    fun `getPing lanca excecao quando a resposta nao e bem sucedida`() =
        runTest {
            server.enqueue(MockResponse().setResponseCode(500).setBody("erro interno"))

            assertFailsWith<Exception> {
                network.getPing()
            }
        }
}
