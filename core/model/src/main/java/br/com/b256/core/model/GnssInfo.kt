package br.com.b256.core.model

import br.com.b256.core.model.enums.Constellation


/**
 * Representa as informações provenientes de Sistemas Globais de Navegação por Satélite (GNSS).
 * Esta classe encapsula dados de posicionamento, qualidade do sinal e métricas relacionadas
 * aos satélites para fins de geolocalização e navegação.
 *
 * @property satellitesVisible O número total de satélites visíveis.
 * @property satellitesUsedInFix O número de satélites utilizados no fix atual.
 * @property constellations O conjunto de constelações utilizadas no fix atual.
 * @property satellites Uma lista de detalhes detalhados sobre cada satélite.
 *
 */
data class GnssInfo(
    val satellitesVisible: Int,
    val satellitesUsedInFix: Int,
    val constellations: Set<Constellation>,
    val satellites: List<GnssSatellite>
)
