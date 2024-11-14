package br.com.b256.core.network.api

import br.com.b256.core.network.model.PongDto
import retrofit2.Response
import retrofit2.http.GET

internal interface Api {
    @GET(value = "client/v4/ping")
    suspend fun ping(): Response<PongDto>
}
