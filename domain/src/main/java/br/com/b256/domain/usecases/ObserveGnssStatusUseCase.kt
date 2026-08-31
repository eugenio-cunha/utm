package br.com.b256.domain.usecases

import br.com.b256.domain.entities.GnssInfo
import br.com.b256.domain.interfaces.LocationProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso responsável por observar o status do sistema GNSS em tempo real.
 *
 * Fornece informações sobre os satélites visíveis, utilizados na fixação
 * e constelações disponíveis através do ecossistema UTM.
 *
 * @property service O serviço de localização da plataforma.
 */
class ObserveGnssStatusUseCase @Inject constructor(
    private val service: LocationProvider
) {
    /**
     * Retorna um fluxo de atualizações do status GNSS.
     *
     * @return [Flow] de [GnssInfo].
     */
    operator fun invoke(): Flow<GnssInfo> {
        return service.gnssStatus
    }
}
