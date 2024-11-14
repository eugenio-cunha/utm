package br.com.b256.core.data.repository

import br.com.b256.core.common.Resource
import br.com.b256.core.model.Pong
import br.com.b256.core.network.service.Service
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    private val service: Service,
) : NetworkRepository {
    override suspend fun ping(): Flow<Resource<Pong>> = service.ping()
}
