package br.com.b256.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.b256.navigation.B256Destination
import kotlinx.coroutines.CoroutineScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import br.com.b256.core.common.monitor.NetworkMonitor
import br.com.b256.core.common.monitor.TimeZoneMonitor
import br.com.b256.feature.home.navigation.navigateToHome
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import androidx.navigation.navOptions
import br.com.b256.feature.place.navigation.navigateToPlace

@Composable
fun rememberB256AppState(
    networkMonitor: NetworkMonitor,
    timeZoneMonitor: TimeZoneMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): B256AppState {

    return B256AppState(
        navController = navController,
        coroutineScope = coroutineScope,
        networkMonitor = networkMonitor,
        timeZoneMonitor = timeZoneMonitor,
    )
}

@Stable
class B256AppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    timeZoneMonitor: TimeZoneMonitor,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: B256Destination?
        @Composable get() {
            return B256Destination.entries.firstOrNull { destination ->
                currentDestination?.hasRoute(route = destination.route) ?: false
            }
        }

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    /**
     * Mapa de destinos de nível superior a serem usados no TopBar, BottomBar e NavRail.
     * A chave é a rota.
     */
    val topLevelDestinations: List<B256Destination> = B256Destination.entries

    val currentTimeZone = timeZoneMonitor.currentTimeZone
        .stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            TimeZone.currentSystemDefault(),
        )

    /**
     * Lógica de UI para navegar para um destino de nível superior no aplicativo.
     * Os destinos de nível superior têm apenas uma cópia do destino da pilha de retorno e
     * salvam e restauram o estado sempre que você navega de e para ele.
     *
     * @param destination: O destino para o qual o aplicativo precisa navegar.
     */
    fun navigateToDestination(destination: B256Destination) {
        val options = navOptions {
            // Pop up para o destino inicial do gráfico para
            // evitar a construção de uma grande pilha de destinos
            // na pilha de volta conforme os usuários selecionam itens
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Evite cópias múltiplas do mesmo destino ao
            // selecionar novamente o mesmo item
            launchSingleTop = true
            // Restaurar estado ao resselecionar um item selecionado anteriormente
            restoreState = true
        }

        when (destination) {
            B256Destination.PLACE -> navController.navigateToPlace(options)
            B256Destination.HOME -> navController.navigateToHome(options)
        }
    }

    fun navigateToHome() = navController.navigateToHome()
}

