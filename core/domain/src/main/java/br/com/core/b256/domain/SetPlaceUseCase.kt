package br.com.core.b256.domain

import br.com.b256.core.data.repository.PlaceRepository
import kotlinx.datetime.Instant
import javax.inject.Inject

class SetPlaceUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
) {
    suspend operator fun invoke(
        title: String?,
        date: Instant,
        latitude: Double,
        longitude: Double,
        altitude: Double,
        accuracy: Float,
    ) {
        placeRepository.create(
            title = title,
            date = date,
            latitude = latitude,
            longitude = longitude,
            altitude = altitude,
            accuracy = accuracy,
        )
    }
}
