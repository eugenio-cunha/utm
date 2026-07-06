package br.com.b256.core.data.repository

import br.com.b256.core.model.GnssInfo
import br.com.b256.core.model.GpsLocation
import br.com.b256.core.model.Orientation
import kotlinx.coroutines.flow.Flow

/**
 * Repositório responsável por gerenciar o acesso aos dados de geolocalização e sensores de hardware.
 *
 * Esta interface abstrai a obtenção de coordenadas geográficas, o status dos satélites GNSS
 * e a orientação espacial do dispositivo, fornecendo fluxos de dados em tempo real.
 */
interface LocationRepository {

    /**
     * Obtém a localização atual do dispositivo de forma assíncrona.
     *
     * @return O [GpsLocation] atual, ou `null` caso a localização não esteja disponível.
     */
    fun getCurrentLocation(): GpsLocation?

    /**
     * Retorna um [Flow] que emite atualizações contínuas sobre o status do GNSS.
     *
     * Este fluxo fornece informações em tempo real sobre os satélites visíveis,
     * incluindo detalhes sobre as constelações, força do sinal (SNR/C/N0) e
     * se os satélites estão sendo usados para obter a fixação da localização.
     *
     * @return Um fluxo de objetos [GnssInfo].
     */
    fun getGnssStatus(): Flow<GnssInfo>

    /**
     * Retorna um [Flow] que emite atualizações contínuas da localização do dispositivo.
     *
     * @return Um fluxo de objetos [GpsLocation] representando a posição em tempo real.
     */
    fun getLocations(): Flow<GpsLocation>

    /**
     * Fornece um fluxo contínuo de atualizações da orientação física do dispositivo.
     *
     * @return Um [Flow] que emite objetos [Orientation], contendo dados de azimute, inclinação (pitch) e rotação (roll).
     */
    fun getOrientation(): Flow<Orientation>
}
