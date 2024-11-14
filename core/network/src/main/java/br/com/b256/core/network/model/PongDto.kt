package br.com.b256.core.network.model

import br.com.b256.core.model.Pong
import kotlinx.serialization.Serializable

@Serializable
data class PongDto(
    val result: String,
    val success: String,
)

fun PongDto.asModel(): Pong = Pong(
    result = result,
    success = success,
)
