package br.com.b256.domain.usecases

import br.com.b256.domain.entities.Orientation
import br.com.b256.domain.interfaces.LocationProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso responsável por observar a orientação (bússola) do dispositivo em tempo real.
 *
 * @property service O serviço de localização e sensores da plataforma.
 */
class ObserveOrientationUseCase @Inject constructor(
    private val service: LocationProvider
) {
    /**
     * Retorna um fluxo de atualizações da orientação do dispositivo.
     *
     * @return [Flow] de [Orientation].
     */
    operator fun invoke(): Flow<Orientation> {
        return service.orientation
    }
}
