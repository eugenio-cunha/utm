package br.com.b256.core.common.monitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.tracing.trace
import br.com.b256.core.common.B256Dispatchers.IO
import br.com.b256.core.common.Dispatcher
import br.com.b256.core.common.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinTimeZone
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ManagerTimeZoneMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope appScope: CoroutineScope,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : TimeZoneMonitor {
    override val currentTimeZone: SharedFlow<TimeZone> =
        callbackFlow {
            // Envie primeiro o fuso horário padrão.
            trySend(TimeZone.currentSystemDefault())

            // Registra BroadcastReceiver para as alterações de fuso horário
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action != Intent.ACTION_TIMEZONE_CHANGED) return

                    val zoneIdFromIntent = if (VERSION.SDK_INT < VERSION_CODES.R) {
                        null
                    } else {
                        // A partir do Android R também temos o novo fuso horário.
                        intent.getStringExtra(Intent.EXTRA_TIMEZONE)?.let { timeZoneId ->
                            // Precisamos convertê-lo de java.util.Timezone para java.time.ZoneId
                            val zoneId = ZoneId.of(timeZoneId, ZoneId.SHORT_IDS)
                            // Converter para kotlinx.datetime.TimeZone
                            zoneId.toKotlinTimeZone()
                        }
                    }

                    // Se não houver um zoneId na intenção, volte para o systemDefault, que também deve refletir a alteração
                    trySend(zoneIdFromIntent ?: TimeZone.currentSystemDefault())
                }
            }

            trace("TimeZoneBroadcastReceiver.register") {
                context.registerReceiver(receiver, IntentFilter(Intent.ACTION_TIMEZONE_CHANGED))
            }

            // Envie aqui novamente, porque registrar o Broadcast Receiver pode levar até vários milissegundos.
            // Dessa forma, podemos reduzir a probabilidade de que uma mudança de TZ não seja capturada com o Broadcast Receiver.
            trySend(TimeZone.currentSystemDefault())

            awaitClose {
                context.unregisterReceiver(receiver)
            }
        }
            // Usamos para evitar múltiplas emissões do mesmo tipo, porque usamos trySend várias vezes.
            .distinctUntilChanged()
            .conflate()
            .flowOn(ioDispatcher)
            // Compartilhando o retorno de chamada para evitar que vários BroadcastReceivers sejam registrados
            .shareIn(appScope, SharingStarted.WhileSubscribed(5_000), 1)
}
