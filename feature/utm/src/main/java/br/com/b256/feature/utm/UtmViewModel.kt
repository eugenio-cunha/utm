package br.com.b256.feature.utm

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

/**
 * ViewModel para a funcionalidade UTM.
 *
 * Este ViewModel é responsável por gerenciar o serviço de localização e fornecer atualizações de
 * localização para a UI. Ele inicia o serviço de localização no construtor e o interrompe quando
 * o ViewModel é limpo.
 *
 * @property application O contexto da aplicação.
 */
@HiltViewModel
class UtmViewModel @Inject constructor(private val application: Application) : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private var locationService: LocationService? = null

    /**
     * Um [MutableStateFlow] que mantém a localização atual.
     * É anulável para representar o caso em que a localização ainda não está disponível.
     */
    private val _location = MutableStateFlow<Location?>(null)

    /**
     * Um [StateFlow] que emite a localização atual, ou `null` se a localização ainda não estiver
     * disponível ou se o serviço de localização não estiver conectado.
     *
     * Este fluxo pode ser observado por componentes de IU para exibir a localização atual ou reagir a
     * mudanças de localização.
     */
    val location: StateFlow<Location?> = _location.asStateFlow()

    /**
     * ServiceConnection para vincular ao LocationService.
     *
     * Este objeto lida com os eventos de conexão e desconexão para o LocationService.
     * Quando o serviço é conectado, ele recupera uma instância do LocationService e começa
     * a coletar atualizações de localização.
     * Quando o serviço é desconectado, ele anula a instância do LocationService.
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            locationService = (service as LocationService.LocationServiceBinder).getService()
            startCollectingLocationUpdates()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
        }
    }

    /**
     * Inicia a coleta de atualizações de localização do [LocationService].
     *
     * Esta função observa o `locationUpdatesFlow` do `locationService`.
     * Cada [Location] emitido é então usado para atualizar o `_location` StateFlow,
     * que por sua vez disponibiliza a localização mais recente para os observadores do `location` StateFlow.
     * A coleta é iniciada dentro do `viewModelScope`, garantindo que seja automaticamente
     * cancelada quando o ViewModel for limpo.
     */
    private fun startCollectingLocationUpdates() {
        locationService?.locationUpdatesFlow()
            ?.onEach { location ->
                _location.value = location
            }
            ?.launchIn(viewModelScope)
    }

    /**
     * Inicia o serviço de localização e vincula-se a ele.
     * Esta função cria uma intent para o LocationService, inicia-o como um serviço em primeiro plano
     * e, em seguida, vincula-se a ele usando o serviceConnection.
     */
    fun startService() {
        Intent(application, LocationService::class.java).also {
            application.startForegroundService(it)
            application.bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    /**
     * Interrompe o serviço de localização e o desvincula da aplicação.
     * Esta função é normalmente chamada quando o ViewModel é limpo ou quando as atualizações de localização
     * não são mais necessárias. Garante que o serviço seja desligado corretamente para economizar recursos.
     */
    private fun stopService() {
        Intent(application, LocationService::class.java).also {
            application.stopService(it)
        }

        application.unbindService(serviceConnection)
    }

    /**
     * Este método será chamado quando este ViewModel não for mais usado e será destruído.
     * É útil quando o ViewModel observa alguns dados e você precisa limpar esta inscrição para
     * evitar um vazamento deste ViewModel.
     *
     * Neste caso específico, ele para o [LocationService] para evitar que ele seja executado
     * indefinidamente e consuma recursos quando o ViewModel não estiver mais em uso.
     */
    override fun onCleared() {
        super.onCleared()

        stopService()
    }
}
