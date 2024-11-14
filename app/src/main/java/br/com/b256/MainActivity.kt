package br.com.b256

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import br.com.b256.MainActivityUiState.Loading
import br.com.b256.MainActivityUiState.Success
import br.com.b256.core.common.monitor.NetworkMonitor
import br.com.b256.core.common.monitor.TimeZoneMonitor
import br.com.b256.core.designsystem.theme.B256Theme
import br.com.b256.core.model.Theme
import br.com.b256.core.ui.LocalTimeZone
import br.com.b256.ui.B256App
import br.com.b256.ui.rememberB256AppState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: MainActivityUiState by mutableStateOf(Loading)

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { uiState = it }
                    .collect {}
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                Loading -> true
                is Success -> false
            }
        }

        enableEdgeToEdge()

        setContent {
            val darkTheme = shouldUseDarkTheme(uiState)

            // Atualiza a configuração de ponta a ponta para corresponder ao tema
            // Esses são os mesmos parâmetros da chamada enableEdgeToEdge padrão,
            // mas nós manualmente resolvemos se o tema escuro deve ou não ser exibido
            // usando uiState, pois ele pode ser diferente do valor do tema escuro da
            // configuração com base na preferência do usuário.
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim,
                        darkScrim,
                    ) { darkTheme },
                )
                onDispose {}
            }

            val appState = rememberB256AppState(
                networkMonitor = networkMonitor,
                timeZoneMonitor = timeZoneMonitor,
            )

            val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()

            CompositionLocalProvider(
                LocalTimeZone provides currentTimeZone,
            ) {
                B256Theme(
                    darkTheme = darkTheme,
                ) {
                    B256App(appState = appState)
                }
            }
        }
    }
}

/** * Retorna `true` se o tema Dark deve ser usado, como uma função do [uiState] e
 * do * contexto atual do sistema.
 */
@Composable
private fun shouldUseDarkTheme(uiState: MainActivityUiState): Boolean = when (uiState) {
    Loading -> isSystemInDarkTheme()
    is Success -> when (uiState.settings.theme) {
        Theme.LIGHT -> false
        Theme.DARK -> true
        Theme.FOLLOW_SYSTEM -> isSystemInDarkTheme()
    }
}

/**
 * O light scrim padrão, conforme definido pelo androidx e pela plataforma:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * O dark scrim padrão, conforme definido pelo androidx e pela plataforma:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)
