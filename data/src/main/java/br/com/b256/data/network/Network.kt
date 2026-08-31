package br.com.b256.data.network

import br.com.b256.domain.entities.Ping

/**
 * Fachada de rede do `:data`: expõe as chamadas remotas já convertidas para entidades de
 * `:domain`, escondendo Retrofit/OkHttp/`Api` (ver [NetworkImpl]) dos repositories que a
 * consomem. Uma nova chamada de API deveria ganhar um método aqui, e não expor [Api] diretamente.
 */
internal interface Network {
    suspend fun getPing(): Ping
}
