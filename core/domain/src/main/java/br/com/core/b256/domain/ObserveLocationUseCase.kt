package br.com.core.b256.domain

import br.com.b256.core.data.repository.LocationRepository
import br.com.b256.core.model.GpsLocation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso responsável por observar as atualizações de localização em tempo real.
 *
 * Fornece um fluxo contínuo de dados GPS, permitindo que a aplicação reaja
 * a mudanças de posição do dispositivo.
 *
 * @property repository O repositório de localização da plataforma.
 */
class ObserveLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    /**
     * Retorna um fluxo de atualizações de localização.
     *
     * @return [Flow] de [GpsLocation].
     */
    operator fun invoke(): Flow<GpsLocation> {
        return repository.getLocations()
    }
}
