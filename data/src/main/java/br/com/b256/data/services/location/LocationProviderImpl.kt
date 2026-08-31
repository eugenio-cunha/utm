package br.com.b256.data.services.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.GnssStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import br.com.b256.data.services.location.extension.toGpsLocation
import br.com.b256.domain.entities.GnssInfo
import br.com.b256.domain.entities.GnssSatellite
import br.com.b256.domain.entities.GpsLocation
import br.com.b256.domain.entities.Orientation
import br.com.b256.domain.entities.enums.Constellation
import br.com.b256.domain.entities.enums.Datum
import br.com.b256.domain.interfaces.LocationProvider
import br.com.b256.domain.interfaces.SettingsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

internal class LocationProviderImpl(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
) : LocationProvider {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(): GpsLocation? {
        if (!hasLocationPermission()) return null

        val datum = runBlocking { settingsRepository.getDatum().first() }

        return locationManager
            .getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?.toGpsLocation(datum)
    }

    // O datum é combinado com cada nova localização (em vez de lido uma única vez) para que a
    // troca de datum nas configurações seja refletida imediatamente, sem esperar um novo fix GPS.
    @SuppressLint("MissingPermission")
    override val locations: Flow<GpsLocation> =
        combine(rawLocations(), settingsRepository.getDatum()) { location, datum ->
            location.toGpsLocation(datum)
        }

    @SuppressLint("MissingPermission")
    private fun rawLocations(): Flow<Location> =
        callbackFlow {
            if (!hasLocationPermission()) {
                close()
                return@callbackFlow
            }

            val listener = LocationListener { location ->
                trySend(location)
            }

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1_000L,
                0f,
                listener,
            )

            awaitClose {
                locationManager.removeUpdates(listener)
            }
        }

    @SuppressLint("MissingPermission")
    override val gnssStatus: Flow<GnssInfo> =
        callbackFlow {
            if (!hasLocationPermission()) {
                close()
                return@callbackFlow
            }

            val callback = createGnssCallback {
                trySend(it)
            }

            val dummyListener = LocationListener { /* Mantém o GPS ativo */ }

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1_000L,
                0f,
                dummyListener,
            )

            locationManager.registerGnssStatusCallback(callback, Handler(Looper.getMainLooper()))

            awaitClose {
                locationManager.unregisterGnssStatusCallback(callback)
            }
        }

    override val orientation: Flow<Orientation> =
        callbackFlow {

            val rotationMatrix = FloatArray(9)
            val orientationAngles = FloatArray(3)

            val lastAccelerometer = FloatArray(3)
            val lastMagnetometer = FloatArray(3)

            var hasAccelerometer = false
            var hasMagnetometer = false

            val alpha = 0.15f

            val listener = object : SensorEventListener {

                override fun onSensorChanged(event: SensorEvent) {

                    when (event.sensor.type) {

                        Sensor.TYPE_ACCELEROMETER -> {

                            repeat(3) {
                                lastAccelerometer[it] +=
                                    alpha * (event.values[it] - lastAccelerometer[it])
                            }

                            hasAccelerometer = true
                        }

                        Sensor.TYPE_MAGNETIC_FIELD -> {

                            repeat(3) {
                                lastMagnetometer[it] +=
                                    alpha * (event.values[it] - lastMagnetometer[it])
                            }

                            hasMagnetometer = true
                        }
                    }

                    if (!hasAccelerometer || !hasMagnetometer)
                        return

                    SensorManager.getRotationMatrix(
                        rotationMatrix,
                        null,
                        lastAccelerometer,
                        lastMagnetometer,
                    )

                    SensorManager.getOrientation(
                        rotationMatrix,
                        orientationAngles,
                    )

                    val azimuth =
                        (Math.toDegrees(orientationAngles[0].toDouble())
                            .toFloat() + 360f) % 360f

                    trySend(Orientation(azimuth))
                }

                override fun onAccuracyChanged(
                    sensor: Sensor?,
                    accuracy: Int,
                ) = Unit
            }

            sensorManager.registerListener(
                listener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI,
            )

            sensorManager.registerListener(
                listener,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_UI,
            )

            awaitClose {
                sensorManager.unregisterListener(listener)
            }
        }

    private fun createGnssCallback(
        onUpdate: (GnssInfo) -> Unit,
    ): GnssStatus.Callback {

        return object : GnssStatus.Callback() {

            override fun onSatelliteStatusChanged(status: GnssStatus) {

                val constellations = mutableSetOf<Constellation>()
                val satellites = mutableListOf<GnssSatellite>()

                var usedInFix = 0

                for (i in 0 until status.satelliteCount) {

                    val constellationType = status.getConstellationType(i)
                    val constellation = constellationType.toConstellationName()
                    val svid = status.getSvid(i)
                    val used = status.usedInFix(i)

                    if (used) usedInFix++

                    constellations += constellation

                    satellites += GnssSatellite(
                        svid = svid,
                        name = buildSatelliteName(
                            constellationType,
                            svid,
                        ),
                        constellation = constellation,
                        constellationType = constellationType,
                        azimuthDegrees = status.getAzimuthDegrees(i),
                        elevationDegrees = status.getElevationDegrees(i),
                        cn0DbHz = status.getCn0DbHz(i),
                        usedInFix = used,
                        carrierFrequencyHz =
                            if (status.hasCarrierFrequencyHz(i)) {
                                status.getCarrierFrequencyHz(i)
                            } else {
                                null
                            },
                        basebandCn0DbHz =
                            if (
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                                status.hasBasebandCn0DbHz(i)
                            ) {
                                status.getBasebandCn0DbHz(i)
                            } else {
                                null
                            },
                    )
                }

                onUpdate(
                    GnssInfo(
                        satellitesVisible = status.satelliteCount,
                        satellitesUsedInFix = usedInFix,
                        constellations = constellations,
                        satellites = satellites.sortedByDescending {
                            it.cn0DbHz
                        },
                    ),
                )
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}

private fun Int.toConstellationName(): Constellation =
    when (this) {
        GnssStatus.CONSTELLATION_GPS -> Constellation.GPS
        GnssStatus.CONSTELLATION_GLONASS -> Constellation.GLONASS
        GnssStatus.CONSTELLATION_GALILEO -> Constellation.GALILEO
        GnssStatus.CONSTELLATION_BEIDOU -> Constellation.BEIDOU
        GnssStatus.CONSTELLATION_QZSS -> Constellation.QZSS
        GnssStatus.CONSTELLATION_IRNSS -> Constellation.IRNSS
        GnssStatus.CONSTELLATION_SBAS -> Constellation.SBAS
        else -> Constellation.UNKNOWN
    }

private fun buildSatelliteName(
    constellationType: Int,
    svid: Int,
): String =
    when (constellationType) {
        GnssStatus.CONSTELLATION_GPS -> "$svid NAVSTAR"
        GnssStatus.CONSTELLATION_GLONASS -> "$svid GLONASS"
        GnssStatus.CONSTELLATION_GALILEO -> "$svid GALILEO"
        GnssStatus.CONSTELLATION_BEIDOU -> "$svid BEIDOU"
        GnssStatus.CONSTELLATION_QZSS -> "$svid QZSS"
        GnssStatus.CONSTELLATION_IRNSS -> "$svid IRNSS"
        GnssStatus.CONSTELLATION_SBAS -> "$svid SBAS"
        else -> "$svid UNKNOWN"
    }
