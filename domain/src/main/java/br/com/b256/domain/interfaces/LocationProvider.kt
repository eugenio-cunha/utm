package br.com.b256.domain.interfaces

import br.com.b256.domain.entities.GnssInfo
import br.com.b256.domain.entities.GpsLocation
import br.com.b256.domain.entities.Orientation
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define as operações de fornecimento de localização,
 * informações GNSS e orientação do dispositivo.
 */
interface LocationProvider {
    /**
     * Retorna a última localização conhecida.
     */
    fun getCurrentLocation(): GpsLocation?

    /**
     * Fluxo contínuo de localização.
     */
    val locations: Flow<GpsLocation>

    /**
     * Fluxo contendo informações GNSS.
     */
    val gnssStatus: Flow<GnssInfo>

    /**
     * Fluxo contendo a orientação (bússola).
     */
    val orientation: Flow<Orientation>

    /**
     * Fornece um fluxo de atualizações de localização (compatibilidade com ViewModel).
     */
    fun locationUpdatesFlow(): Flow<GpsLocation> = locations
}
