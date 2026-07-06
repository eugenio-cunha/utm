package br.com.core.b256.domain

import br.com.b256.core.data.repository.LocationRepository
import br.com.b256.core.model.GnssInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso responsável por observar o status do sistema GNSS em tempo real.
 *
 * Fornece informações sobre os satélites visíveis, utilizados na fixação
 * e constelações disponíveis através do ecossistema UTM.
 *
 * @property repository O repositório de localização da plataforma.
 */
class ObserveGnssStatusUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    /**
     * Retorna um fluxo de atualizações do status GNSS.
     *
     * @return [Flow] de [GnssInfo].
     */
    operator fun invoke(): Flow<GnssInfo> {
        return repository.getGnssStatus()
    }
}
