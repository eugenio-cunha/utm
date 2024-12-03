package br.com.core.b256.domain

import br.com.b256.core.data.repository.PlaceRepository
import br.com.b256.core.model.Place
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlaceUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
) {
    operator fun invoke(): Flow<List<Place>> = placeRepository.find()

    operator fun invoke(id: Int): Flow<Place> = placeRepository.findById(id = id)
}
