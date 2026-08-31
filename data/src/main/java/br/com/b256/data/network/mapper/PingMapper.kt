package br.com.b256.data.network.mapper

import br.com.b256.data.network.model.PingResponse
import br.com.b256.domain.entities.Ping

/**
 * Mapper de referência: converte o modelo de rede ([PingResponse]) para a entidade de domínio
 * ([Ping]). Convenção do projeto para mappers: extension function `asDomain()`/`asEntity()`,
 * `internal`, mantendo os modelos de infraestrutura fora do `:domain`.
 */
internal fun PingResponse.asDomain(): Ping =
    Ping(
        result = result,
        success = success,
    )
