package br.com.core.b256.domain

import br.com.b256.core.data.repository.LocationRepository
import br.com.b256.core.model.Orientation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso responsável por observar a orientação (bússola) do dispositivo em tempo real.
 *
 * @property repository O repositório de localização e sensores da plataforma.
 */
class ObserveOrientationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    /**
     * Retorna um fluxo de atualizações da orientação do dispositivo.
     *
     * @return [Flow] de [Orientation].
     */
    operator fun invoke(): Flow<Orientation> {
        return repository.getOrientation()
    }
}
