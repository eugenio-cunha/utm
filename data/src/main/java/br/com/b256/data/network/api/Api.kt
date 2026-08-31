package br.com.b256.data.network.api

import br.com.b256.data.network.model.PingResponse
import retrofit2.Response
import retrofit2.http.GET

/**
 * Interface Retrofit "crua" da API remota. `internal` de propósito: só [NetworkImpl] deve
 * conhecer os endpoints — o restante do `:data` (e todo o `:domain`) enxerga apenas [Network].
 */
internal interface Api {
    @GET(value = "client/v4/ping")
    suspend fun getPing(): Response<PingResponse>
}
