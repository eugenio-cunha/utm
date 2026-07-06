package br.com.b256.core.model

import kotlin.time.Instant

/**
 * Representa um ponto de coordenada geográfica composto por latitude e longitude.
 * Esta classe serve como um modelo de dados para manipulação de informações de posicionamento GPS
 * dentro do contexto de conversão para coordenadas UTM.
 *
 * @property latitude A latitude do ponto de coordenada.
 * @property longitude A longitude do ponto de coordenada.
 * @property altitude A altitude do ponto de coordenada.
 * @property accuracy A precisão do ponto de coordenada.
 * @property speed A velocidade do ponto de coordenada.
 * @property bearing O ângulo de orientação do ponto de coordenada.
 * @property date O timestamp do ponto de coordenada.
 * @property utm As coordenadas UTM correspondentes ao ponto de coordenada.
 */
data class GpsLocation(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double?,
    val accuracy: Float?,
    val speed: Float?,
    val bearing: Float?,
    val date: Instant,
    val utm: UTM
)
