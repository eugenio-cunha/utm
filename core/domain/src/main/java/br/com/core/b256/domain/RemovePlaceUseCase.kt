package br.com.core.b256.domain

import br.com.b256.core.data.repository.PlaceRepository
import javax.inject.Inject

class RemovePlaceUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
) {
    suspend operator fun invoke(id: Int) = placeRepository.deleteById(id = id)
}
