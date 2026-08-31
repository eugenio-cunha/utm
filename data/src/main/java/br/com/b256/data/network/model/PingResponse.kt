package br.com.b256.data.network.model

import kotlinx.serialization.Serializable

/**
 * Modelo de resposta da API (formato de rede), convertido para a entidade de domínio [Ping]
 * pelo mapper `asDomain()` (`network/mapper/PingMapper.kt`) antes de sair de [NetworkImpl].
 */
@Serializable
internal data class PingResponse(
    val result: String,
    val success: Boolean,
)
