package br.com.core.b256.domain

import br.com.b256.core.data.repository.LocationRepository
import br.com.b256.core.model.GpsLocation
import javax.inject.Inject

/**
 * Caso de uso responsável por obter a localização atual do dispositivo.
 *
 * Esta classe abstrai a lógica de acesso ao serviço de localização, permitindo
 * que a interface do usuário ou outras camadas obtenham as coordenadas GPS
 * de forma simplificada através do ecossistema UTM.
 *
 * @property repository O serviço de localização da plataforma.
 */
class GetLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    /**
     * Executa o caso de uso para obter a localização geográfica atual.
     *
     * @return Um objeto [GpsLocation] contendo as coordenadas atuais, ou null caso a localização não esteja disponível.
     */
    operator fun invoke(): GpsLocation? {
        return repository.getCurrentLocation()
    }
}
