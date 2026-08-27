package br.com.b256.gnss

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.Consumer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import br.com.b256.presentation.designsystem.theme.B256Theme
import br.com.b256.presentation.navigation.B256NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Única `Activity` do app (padrão single-activity: as demais telas são destinos Navigation3 dentro
 * de [B256NavDisplay]). Padrão de referência aqui: sincronizar splash screen e cor de status/nav
 * bar com o tema de UI (claro/escuro/seguir sistema) lido via [MainActivityViewModel], em vez de
 * decidir o tema só a partir da configuração do sistema.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Mantemos isso como um estado mutável para podermos rastrear alterações dentro da composição.
        // Isso nos permite reagir a mudanças entre os modos claro e escuro.
        var themeSettings by mutableStateOf(resources.configuration.isSystemInDarkTheme)

        // Atualiza o uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    isSystemInDarkTheme(),
                    viewModel.uiState,
                ) { systemDark, uiState ->
                    uiState.shouldUseDarkTheme(systemDark)
                }.onEach { themeSettings = it }
                    .distinctUntilChanged()
                    .collect { darkTheme ->
                        // Atualiza a configuração de ponta a ponta para corresponder ao tema
                        // Esses são os mesmos parâmetros da chamada enableEdgeToEdge padrão,
                        // mas nós manualmente resolvemos se o tema escuro deve ou não ser exibido
                        // usando uiState, pois ele pode ser diferente do valor do tema escuro da
                        // configuração com base na preferência do usuário.
                        enableEdgeToEdge(
                            statusBarStyle =
                                SystemBarStyle.auto(
                                    lightScrim = Color.TRANSPARENT,
                                    darkScrim = Color.TRANSPARENT,
                                ) { darkTheme },
                            navigationBarStyle =
                                SystemBarStyle.auto(
                                    lightScrim = lightScrim,
                                    darkScrim = darkScrim,
                                ) { darkTheme },
                        )
                    }
            }
        }

        // Mantém o SplashScreen a depender do estado
        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value.shouldKeepSplashScreen()
        }

        setContent {
            B256Theme(darkTheme = themeSettings) {
                B256NavDisplay(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

/**
 * O light scrim padrão, conforme definido pelo androidx e pela plataforma:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * O dark scrim padrão, conforme definido pelo androidx e pela plataforma:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

/**
 * Wrapper de conveniência para verificar o modo escuro
 */
private val Configuration.isSystemInDarkTheme
    get() = (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

/**
 * Registra um ouvinte para alterações de configuração, a fim de verificar se o sistema está
 * usando o tema escuro ou não. Imediatamente após a inscrição, envia o valor atual e, em seguida,
 * registra o ouvinte para alterações.
 */
fun ComponentActivity.isSystemInDarkTheme() =
    callbackFlow {
        channel.trySend(resources.configuration.isSystemInDarkTheme)

        val listener =
            Consumer<Configuration> {
                channel.trySend(it.isSystemInDarkTheme)
            }

        addOnConfigurationChangedListener(listener)

        awaitClose { removeOnConfigurationChangedListener(listener) }
    }.distinctUntilChanged().conflate()
