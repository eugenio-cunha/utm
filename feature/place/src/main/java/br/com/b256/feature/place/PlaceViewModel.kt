package br.com.b256.feature.place

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.b256.core.gps.service.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PlaceViewModel @Inject constructor(
    private val application: Application,
) : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private var locationService: LocationService? = null

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            locationService = (service as LocationService.LocationServiceBinder).getService()
            startCollectingLocationUpdates()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
        }
    }

    init {
        startService()
    }

    private fun startCollectingLocationUpdates() {
        locationService?.locationUpdatesFlow()
            ?.onEach { location ->
                _location.value = location
            }
            ?.launchIn(viewModelScope)
    }

    private fun startService() {
        Intent(application, LocationService::class.java).also {
            application.startForegroundService(it)
            application.bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun stopService() {
        Intent(application, LocationService::class.java).also {
            application.stopService(it)
        }

        application.unbindService(serviceConnection)
    }

    override fun onCleared() {
        super.onCleared()

        stopService()
    }
}
