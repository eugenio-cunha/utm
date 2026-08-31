package br.com.b256.data.services.location.extension

import android.location.Location
import br.com.b256.data.services.location.LocaleHelper
import br.com.b256.domain.entities.GpsLocation
import br.com.b256.domain.entities.UTM
import br.com.b256.domain.entities.enums.Datum
import gov.nasa.worldwind.geom.Angle
import gov.nasa.worldwind.geom.coords.UTMCoord
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Converte esta [Location] — sempre reportada pelo GPS no datum WGS84 — para [GpsLocation],
 * expressando latitude/longitude e UTM no [datum] selecionado pelo usuário (ver
 * [transformToDatum]). Quando [datum] é [Datum.WGS84], a posição não sofre alteração.
 *
 * @param datum Datum geodésico no qual a posição deve ser expressa.
 */
internal fun Location.toGpsLocation(datum: Datum): GpsLocation {
    val altitudeMeters = if (hasAltitude()) altitude else 0.0
    val (datumLat, datumLon) = transformToDatum(latitude, longitude, altitudeMeters, datum)

    return GpsLocation(
        latitude = datumLat,
        longitude = datumLon,
        altitude = if (hasAltitude()) altitude else null,
        accuracy = if (hasAccuracy()) accuracy else null,
        speed = if (hasSpeed()) speed else null,
        bearing = if (hasBearing()) bearing else null,
        date = Instant.fromEpochMilliseconds(time),
        utm = utmOf(lat = datumLat, lon = datumLon, datum = datum),
    )
}

private fun utmOf(lat: Double, lon: Double, datum: Datum): UTM {
    val coord = getUtmCoord(lat, lon)
    return UTM(
        zone = "${coord.zone}${getUtmLatBand(lat)}".trim(),
        easting = formatUtmValue(coord.easting, "E"),
        northing = formatUtmValue(coord.northing, "N"),
        centralMeridian = coord.centralMeridian.toString(),
        datum = datum,
    )
}

private fun formatUtmValue(value: Double, hemisphereLabel: String): String {
    return "${String.format(LocaleHelper.getAppLocale(), "%7.0f", value)}m $hemisphereLabel".trim()
}

private fun getUtmCoord(lat: Double, lon: Double): UTMCoord {
    return UTMCoord.fromLatLon(Angle.fromDegreesLatitude(lat), Angle.fromDegreesLongitude(lon))
}

private fun getUtmLatBand(lat: Double): String {
    return when {
        -80.0 <= lat && lat < -72.0 -> "C"
        -72.0 <= lat && lat < -64.0 -> "D"
        -72.0 <= lat && lat < -56.0 -> "E"
        -72.0 <= lat && lat < -48.0 -> "F"
        -72.0 <= lat && lat < -40.0 -> "G"
        -72.0 <= lat && lat < -32.0 -> "H"
        -72.0 <= lat && lat < -24.0 -> "J"
        -72.0 <= lat && lat < -16.0 -> "K"
        -72.0 <= lat && lat < -8.0 -> "L"
        -72.0 <= lat && lat < 0.0 -> "M"
        00.0 <= lat && lat < 08.0 -> "N"
        08.0 <= lat && lat < 16.0 -> "P"
        16.0 <= lat && lat < 24.0 -> "Q"
        24.0 <= lat && lat < 32.0 -> "R"
        32.0 <= lat && lat < 40.0 -> "S"
        40.0 <= lat && lat < 48.0 -> "T"
        48.0 <= lat && lat < 56.0 -> "U"
        56.0 <= lat && lat < 64.0 -> "V"
        64.0 <= lat && lat < 72.0 -> "W"
        72.0 <= lat && lat < 84.0 -> "X"
        else -> ""
    }
}

internal fun Location.instant() = Instant.fromEpochMilliseconds(time)

internal fun Location.localDateTime() =
    instant().toLocalDateTime(timeZone = TimeZone.currentSystemDefault())

internal fun Location.localDateTimeString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm", Locale.getDefault())
    return localDateTime().toJavaLocalDateTime().format(formatter)
}
