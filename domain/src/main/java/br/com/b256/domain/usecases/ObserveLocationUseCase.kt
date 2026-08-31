package br.com.b256.domain.usecases

import br.com.b256.domain.entities.GpsLocation
import br.com.b256.domain.interfaces.LocationProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso responsável por observar as atualizações de localização em tempo real.
 *
 * Fornece um fluxo contínuo de dados GPS, permitindo que a aplicação reaja
 * a mudanças de posição do dispositivo.
 *
 * @property service O serviço de localização da plataforma.
 */
class ObserveLocationUseCase @Inject constructor(
    private val service: LocationProvider
) {
    /**
     * Retorna um fluxo de atualizações de localização.
     *
     * @return [Flow] de [GpsLocation].
     */
    operator fun invoke(): Flow<GpsLocation> {
        return service.locations
    }
}
