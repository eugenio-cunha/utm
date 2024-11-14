package br.com.b256.core.data.repository

import br.com.b256.core.common.Resource
import br.com.b256.core.model.Pong
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    suspend fun ping(): Flow<Resource<Pong>>
}
