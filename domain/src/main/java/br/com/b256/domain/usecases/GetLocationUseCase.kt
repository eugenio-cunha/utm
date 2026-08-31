package br.com.b256.domain.usecases

import br.com.b256.domain.entities.GpsLocation
import br.com.b256.domain.interfaces.LocationProvider
import javax.inject.Inject

/**
 * Caso de uso responsável por obter a localização atual do dispositivo.
 *
 * Esta classe abstrai a lógica de acesso ao serviço de localização, permitindo
 * que a interface do usuário ou outras camadas obtenham as coordenadas GPS
 * de forma simplificada através do ecossistema UTM.
 *
 * @property service O serviço de localização da plataforma.
 */
class GetLocationUseCase @Inject constructor(
    private val service: LocationProvider
) {
    /**
     * Executa o caso de uso para obter a localização geográfica atual.
     *
     * @return Um objeto [GpsLocation] contendo as coordenadas atuais, ou null caso a localização não esteja disponível.
     */
    operator fun invoke(): GpsLocation? {
        return service.getCurrentLocation()
    }
}
