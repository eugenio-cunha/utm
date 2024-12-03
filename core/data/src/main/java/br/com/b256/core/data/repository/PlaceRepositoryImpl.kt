package br.com.b256.core.data.repository

import br.com.b256.core.database.RoomDatabase
import br.com.b256.core.database.model.PlaceEntity
import br.com.b256.core.database.model.asModel
import br.com.b256.core.model.Place
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(
    private val database: RoomDatabase,
) : PlaceRepository {
    override fun find(): Flow<List<Place>> =
        database.place().find().map { item -> item.map { it.asModel() } }

    override fun findById(id: Int): Flow<Place> = flow {
        database.place().findById(id = id).asModel().also {
            emit(value = it)
        }
    }

    override suspend fun create(
        title: String?,
        date: Instant,
        latitude: Double,
        longitude: Double,
        altitude: Double,
        accuracy: Float,
    ) {
        database.place().create(
            value = PlaceEntity(
                title = title,
                date = date,
                latitude = latitude,
                longitude = longitude,
                altitude = altitude,
                accuracy = accuracy,
            ),
        )
    }

    override suspend fun deleteById(id: Int) {
        database.place().deleteById(id = id)
    }
}
