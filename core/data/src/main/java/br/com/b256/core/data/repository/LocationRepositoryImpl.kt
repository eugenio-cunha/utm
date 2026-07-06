package br.com.b256.core.data.repository

import br.com.b256.core.gps.LocationProvider
import br.com.b256.core.model.GnssInfo
import br.com.b256.core.model.GpsLocation
import br.com.b256.core.model.Orientation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class LocationRepositoryImpl @Inject constructor(
    private val provider: LocationProvider,
): LocationRepository {
    override fun getCurrentLocation(): GpsLocation? = provider.getCurrentLocation()

    override fun getGnssStatus(): Flow<GnssInfo> = provider.gnssStatus

    override fun getLocations(): Flow<GpsLocation> = provider.locations

    override fun getOrientation(): Flow<Orientation> = provider.orientation
}
