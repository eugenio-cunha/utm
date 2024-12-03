package br.com.b256.core.data.repository

import br.com.b256.core.model.Place
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface PlaceRepository {
    fun find(): Flow<List<Place>>

    fun findById(id: Int): Flow<Place>

    suspend fun create(
        title: String?,
        date: Instant,
        latitude: Double,
        longitude: Double,
        altitude: Double,
        accuracy: Float,
    )

    suspend fun deleteById(id: Int)
}
