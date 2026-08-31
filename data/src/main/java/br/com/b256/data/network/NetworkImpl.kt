package br.com.b256.data.network

import androidx.tracing.trace
import br.com.b256.data.network.api.Api
import br.com.b256.data.network.mapper.asDomain
import br.com.b256.domain.entities.Ping
import dagger.Lazy
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Implementação de referência de [Network]: monta o `Retrofit` sob demanda (via `Lazy<Call.Factory>`,
 * evitando criar o `OkHttpClient` antes de ser realmente necessário), delega a chamada HTTP para
 * [Api] e converte a resposta para a entidade de domínio com o mapper (`asDomain()`). Erros HTTP
 * (`!isSuccessful`) viram exceção — se um repository precisar tratar falha sem propagar exceção,
 * capture aqui e retorne `Resource.Failure`.
 *
 * [baseUrl] é recebido via construtor (provido em `NetworkModule` a partir de
 * `BuildConfig.NETWORK_BASE_URL`) em vez de lido diretamente do `BuildConfig` aqui dentro — isso
 * permite instanciar esta classe em teste apontando para um `MockWebServer` (ver `NetworkImplTest`).
 */
@Singleton
internal class NetworkImpl
    @Inject
    constructor(
        json: Json,
        okhttp: Lazy<Call.Factory>,
        @Named("networkBaseUrl") baseUrl: String,
    ) : Network {
        private val api =
            trace("RetrofitB256Network") {
                Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .callFactory { okhttp.get().newCall(it) }
                    .addConverterFactory(
                        json.asConverterFactory("application/json".toMediaType()),
                    )
                    .build()
                    .create(Api::class.java)
            }

        override suspend fun getPing(): Ping {
            val response = api.getPing()
            if (response.isSuccessful) {
                return response.body()!!.asDomain()
            }
            throw Exception(response.errorBody()!!.string())
        }
    }
