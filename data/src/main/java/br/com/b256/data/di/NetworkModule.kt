package br.com.b256.data.di

import androidx.tracing.trace
import br.com.b256.data.BuildConfig
import br.com.b256.data.network.Network
import br.com.b256.data.network.NetworkImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Named
import javax.inject.Singleton

/**
 * Módulo Hilt de referência para prover dependências de terceiros (`Json`, `OkHttpClient`) via
 * `@Provides`, e vincular a implementação concreta de [Network] através de um método `@Provides`
 * que simplesmente devolve o parâmetro injetado (`network: NetworkImpl -> Network`) — alternativa
 * a `@Binds` quando o tipo já vem de outro `@Provides` no mesmo módulo. Instalado em
 * [SingletonComponent] (escopo de aplicação); `HttpLoggingInterceptor` só loga corpo da
 * requisição em builds de debug.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun providesNetworkJson(): Json =
        Json {
            ignoreUnknownKeys = true
        }

    /** Isolado num `@Provides` próprio (em vez de lido direto de [NetworkImpl]) para permitir
     * substituir a URL base em teste sem depender do `BuildConfig`. */
    @Provides
    @Singleton
    @Named("networkBaseUrl")
    fun providesNetworkBaseUrl(): String = BuildConfig.NETWORK_BASE_URL

    @Provides
    @Singleton
    fun okHttpCallFactory(): Call.Factory =
        trace("B256OkHttpClient") {
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor()
                        .apply {
                            if (BuildConfig.DEBUG) {
                                setLevel(HttpLoggingInterceptor.Level.BODY)
                            }
                        },
                )
                .build()
        }

    @Provides
    @Singleton
    fun providesNetwork(network: NetworkImpl): Network = network
}
