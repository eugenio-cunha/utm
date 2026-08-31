package br.com.b256.domain.entities

/**
 * Representa a orientação do dispositivo em relação ao Norte magnético.
 *
 * @property azimuth O azimute em graus (0-360). 0 é Norte, 90 é Leste, 180 é Sul e 270 é Oeste.
 */
data class Orientation(
    val azimuth: Float
)
