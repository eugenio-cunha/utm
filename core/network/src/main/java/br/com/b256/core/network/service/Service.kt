package br.com.b256.core.network.service

import br.com.b256.core.common.Resource
import br.com.b256.core.model.Pong
import kotlinx.coroutines.flow.Flow

interface Service {
    suspend fun ping(): Flow<Resource<Pong>>
}
