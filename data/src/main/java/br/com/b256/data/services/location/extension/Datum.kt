package br.com.b256.data.services.location.extension

import br.com.b256.domain.entities.enums.Datum
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Parâmetros de um elipsoide de referência: semieixo maior [a] (metros) e achatamento [f].
 */
private data class Ellipsoid(val a: Double, val f: Double) {
    val eccentricitySquared: Double get() = f * (2 - f)
}

/**
 * Translação geocêntrica (ΔX, ΔY, ΔZ, em metros) de um datum local em relação ao WGS84, na
 * convenção do EPSG Geodetic Parameter Dataset: `WGS84 = Local + delta` (coordenadas
 * geocêntricas). Para ir de WGS84 → Local (o sentido que este app precisa, já que o GPS sempre
 * reporta em WGS84), a translação é subtraída em vez de somada — ver [transformToDatum].
 */
private data class GeocentricShift(val dX: Double, val dY: Double, val dZ: Double)

private val WGS84_ELLIPSOID = Ellipsoid(a = 6_378_137.0, f = 1.0 / 298.257223563)

/**
 * Elipsoide de referência de cada [Datum] suportado.
 *
 * - [Datum.SIRGAS2000], [Datum.NAD83] e [Datum.ETRS89] usam o elipsoide GRS80, praticamente
 *   idêntico ao WGS84 para os fins deste app (a diferença de achatamento tem efeito
 *   submilimétrico).
 * - [Datum.SAD69] usa o elipsoide GRS 1967 Modificado, na definição oficial do EPSG:4618.
 * - [Datum.CORREGO_ALEGRE] e [Datum.ED50] usam o elipsoide International 1924 (Hayford), nas
 *   definições oficiais do EPSG:4225 e EPSG:4230, respectivamente.
 * - [Datum.NAD27] usa o elipsoide Clarke 1866, na definição oficial do EPSG:4267.
 */
private val ELLIPSOIDS: Map<Datum, Ellipsoid> = mapOf(
    Datum.WGS84 to WGS84_ELLIPSOID,
    Datum.SIRGAS2000 to Ellipsoid(a = 6_378_137.0, f = 1.0 / 298.257222101),
    Datum.SAD69 to Ellipsoid(a = 6_378_160.0, f = 1.0 / 298.25),
    Datum.CORREGO_ALEGRE to Ellipsoid(a = 6_378_388.0, f = 1.0 / 297.0),
    Datum.NAD83 to Ellipsoid(a = 6_378_137.0, f = 1.0 / 298.257222101),
    Datum.NAD27 to Ellipsoid(a = 6_378_206.4, f = 1.0 / 294.9786982),
    Datum.ETRS89 to Ellipsoid(a = 6_378_137.0, f = 1.0 / 298.257222101),
    Datum.ED50 to Ellipsoid(a = 6_378_388.0, f = 1.0 / 297.0),
)

/**
 * Translação geocêntrica de cada [Datum] em relação ao WGS84, conforme o EPSG Geodetic Parameter
 * Dataset (método "Geocentric translations"):
 * - [Datum.SAD69]: EPSG:6195 (SAD69(96) → WGS 84), válida para o Brasil.
 * - [Datum.CORREGO_ALEGRE]: EPSG:1132 (Córrego Alegre 1970-72 → WGS 84), válida para o Brasil.
 * - [Datum.NAD27]: EPSG:1173 (NAD27 → WGS 84), valores médios para os EUA continentais (CONUS).
 * - [Datum.ED50]: EPSG:1133 (ED50 → WGS 84), valores médios para a Europa ocidental/central.
 *
 * São transformações de 3 parâmetros (Molodensky abreviado), não as grades oficiais de cada
 * país/região (NTv2, ProGriD, etc.) — com exatidão de poucos a dezenas de metros, adequadas para
 * navegação e visualização, mas não para uso cadastral/jurídico. [Datum.NAD27] e [Datum.ED50]
 * têm parâmetros regionais mais precisos por sub-área (estado, país); os valores aqui são a
 * média mais amplamente usada para cada região, suficiente para o propósito deste app.
 */
private val GEOCENTRIC_SHIFTS: Map<Datum, GeocentricShift> = mapOf(
    Datum.WGS84 to GeocentricShift(dX = 0.0, dY = 0.0, dZ = 0.0),
    Datum.SIRGAS2000 to GeocentricShift(dX = 0.0, dY = 0.0, dZ = 0.0),
    Datum.SAD69 to GeocentricShift(dX = -67.35, dY = 3.88, dZ = -38.22),
    Datum.CORREGO_ALEGRE to GeocentricShift(dX = -206.0, dY = 172.0, dZ = -6.0),
    Datum.NAD83 to GeocentricShift(dX = 0.0, dY = 0.0, dZ = 0.0),
    Datum.NAD27 to GeocentricShift(dX = -8.0, dY = 160.0, dZ = 176.0),
    Datum.ETRS89 to GeocentricShift(dX = 0.0, dY = 0.0, dZ = 0.0),
    Datum.ED50 to GeocentricShift(dX = -87.0, dY = -98.0, dZ = -121.0),
)

/**
 * Converte a latitude/longitude informadas — sempre no datum WGS84, como reportado pelo GPS —
 * para o [datum] de destino.
 *
 * A transformação é feita em 3 etapas clássicas de mudança de datum: geodésico → geocêntrico
 * (elipsoide WGS84), translação geocêntrica de 3 parâmetros (ver [GEOCENTRIC_SHIFTS]) e
 * geocêntrico → geodésico no elipsoide de destino (ver [ELLIPSOIDS]). Quando [datum] é
 * [Datum.WGS84], [Datum.SIRGAS2000], [Datum.NAD83] ou [Datum.ETRS89] (translação nula), retorna
 * [lat]/[lon] inalterados.
 *
 * O resultado é usado tanto para exibir latitude/longitude quanto como entrada da projeção UTM
 * (ver `Location.toGpsLocation`); a projeção UTM em si continua usando a fórmula do elipsoide
 * WGS84 (a biblioteca de projeção não permite trocar o elipsoide), então o easting/northing
 * resultante tem um pequeno erro residual adicional em relação ao elipsoide "correto" do
 * [datum] — praticamente desprezível para [Datum.SAD69] (elipsoide bem próximo do WGS84),
 * pequeno para [Datum.NAD27], e um pouco maior para [Datum.CORREGO_ALEGRE] e [Datum.ED50], cujo
 * elipsoide (Hayford) é o mais distinto do WGS84 entre os suportados.
 *
 * @param lat Latitude em graus, no datum WGS84.
 * @param lon Longitude em graus, no datum WGS84.
 * @param altitude Altitude em metros, usada apenas como altura auxiliar no cálculo geocêntrico;
 * um valor impreciso (ou 0.0, quando desconhecido) tem efeito desprezível no resultado horizontal.
 * @param datum Datum de destino.
 * @return Par (latitude, longitude) em graus, no [datum] informado.
 */
internal fun transformToDatum(
    lat: Double,
    lon: Double,
    altitude: Double,
    datum: Datum,
): Pair<Double, Double> {
    val shift = GEOCENTRIC_SHIFTS.getValue(datum)
    if (shift.dX == 0.0 && shift.dY == 0.0 && shift.dZ == 0.0) return lat to lon

    val (x, y, z) = geodeticToGeocentric(lat, lon, altitude, WGS84_ELLIPSOID)

    // WGS84 = Local + delta (convenção EPSG) ⇒ Local = WGS84 - delta.
    val targetX = x - shift.dX
    val targetY = y - shift.dY
    val targetZ = z - shift.dZ

    val (targetLat, targetLon, _) = geocentricToGeodetic(targetX, targetY, targetZ, ELLIPSOIDS.getValue(datum))
    return targetLat to targetLon
}

/**
 * Converte coordenadas geodésicas (latitude/longitude/altura, em graus/graus/metros) para
 * coordenadas geocêntricas (X, Y, Z, em metros), segundo a fórmula padrão de conversão
 * elipsoidal-para-cartesiana.
 */
private fun geodeticToGeocentric(
    latDegrees: Double,
    lonDegrees: Double,
    height: Double,
    ellipsoid: Ellipsoid,
): Triple<Double, Double, Double> {
    val lat = Math.toRadians(latDegrees)
    val lon = Math.toRadians(lonDegrees)
    val e2 = ellipsoid.eccentricitySquared
    val n = ellipsoid.a / sqrt(1 - e2 * sin(lat) * sin(lat))

    val x = (n + height) * cos(lat) * cos(lon)
    val y = (n + height) * cos(lat) * sin(lon)
    val z = (n * (1 - e2) + height) * sin(lat)
    return Triple(x, y, z)
}

/**
 * Converte coordenadas geocêntricas (X, Y, Z, em metros) de volta para coordenadas geodésicas
 * (latitude/longitude em graus, altura em metros), por iteração de ponto fixo (converge em
 * poucas iterações para qualquer posição terrestre não polar).
 */
private fun geocentricToGeodetic(
    x: Double,
    y: Double,
    z: Double,
    ellipsoid: Ellipsoid,
): Triple<Double, Double, Double> {
    val lon = atan2(y, x)
    val p = sqrt(x * x + y * y)
    val e2 = ellipsoid.eccentricitySquared

    var lat = atan2(z, p * (1 - e2))
    var height = 0.0
    repeat(6) {
        val n = ellipsoid.a / sqrt(1 - e2 * sin(lat) * sin(lat))
        height = p / cos(lat) - n
        lat = atan2(z, p * (1 - e2 * n / (n + height)))
    }

    return Triple(Math.toDegrees(lat), Math.toDegrees(lon), height)
}
