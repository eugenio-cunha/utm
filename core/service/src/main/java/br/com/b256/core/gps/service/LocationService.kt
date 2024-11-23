package br.com.b256.core.gps.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.b256.core.common.Permission
import br.com.b256.core.gps.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class LocationService : Service(), LocationListener {
    private var locationManager: LocationManager? = null
    private var handler = Handler(Looper.getMainLooper())
    private var delay: Long = 1000
    private val notificationId = 12345
    private val channelId = "LocationServiceChannel"

    private val locationFlow = MutableSharedFlow<Location>(replay = 1)

    inner class LocationServiceBinder : Binder() {
        fun getService(): LocationService {
            return this@LocationService
        }
    }

    private val binder = LocationServiceBinder()

    fun locationUpdatesFlow(): Flow<Location> = locationFlow

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = baseContext.getSystemService(LOCATION_SERVICE) as LocationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("GPS_UTM", "Start")
        requestLastKnownLocation()
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        locationManager?.removeUpdates(this)
        handler.removeCallbacks(locationUpdater)
        super.onDestroy()
    }

    override fun onLocationChanged(location: Location) {
        locationFlow.tryEmit(value = location)
        Intent().also { intent ->
            intent.action = "location"
            intent.putExtra("location", location)
            LocalBroadcastManager.getInstance(baseContext).sendBroadcast(intent)
        }
    }

    /**
     * Somente para  API < 29
     * */
    @Deprecated("Depreciado no Java")
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        Intent().also { intent ->
            intent.action = "provider"
            intent.putExtra("location_provider", provider)
            LocalBroadcastManager.getInstance(baseContext).sendBroadcast(intent)
        }
    }

    override fun onProviderEnabled(provider: String) {
        requestLastKnownLocation()
        Intent().also { intent ->
            intent.action = "provider"
            intent.putExtra("location_provider", provider)
            LocalBroadcastManager.getInstance(baseContext).sendBroadcast(intent)
        }
    }

    override fun onProviderDisabled(provider: String) {
        handler.removeCallbacks(locationUpdater)
        Intent().also { intent ->
            intent.action = "provider"
            intent.putExtra("location_provider", provider)
            LocalBroadcastManager.getInstance(baseContext).sendBroadcast(intent)
        }
    }

    private val locationUpdater: Runnable = object : Runnable {
        override fun run() {
            requestLocation()
            handler.postDelayed(this, delay)
        }
    }

    private fun requestLastKnownLocation() {
        if (Permission.checkPermission(applicationContext)) {
            var location: Location? = null

            when {
                locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                    location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }

                locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> {
                    location =
                        locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }

                locationManager!!.isProviderEnabled(LocationManager.PASSIVE_PROVIDER) -> {
                    location =
                        locationManager?.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                }
            }

            if (location == null) {
                handler.post(locationUpdater)
            } else {
                location.provider = "Last Location"
                onLocationChanged(location)
                handler.post(locationUpdater)
            }
        }
    }

    private fun requestLocation() {
        if (Permission.checkPermission(applicationContext)) {
            when {
                locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                    locationManager?.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        delay,
                        0f,
                        this,
                    )
                }

                locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> {
                    locationManager?.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        delay,
                        0f,
                        this,
                    )
                }

                else -> {
                    locationManager?.requestLocationUpdates(
                        LocationManager.PASSIVE_PROVIDER,
                        delay,
                        0f,
                        this,
                    )
                }
            }
        }
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            channelId,
            "Location Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
        startForeground(
            notificationId,
            NotificationCompat.Builder(this, channelId)
                .setContentTitle("Location Tracking")
                .setContentText("Tracking your location...")
                .setSmallIcon(R.drawable.ic_location)
                .build(),
        )
    }
}
