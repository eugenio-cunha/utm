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
import br.com.b256.core.common.monitor.NetworkMonitor
import br.com.b256.core.common.monitor.TimeZoneMonitor
import br.com.b256.feature.home.navigation.navigateToHome
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone

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

    val currentTimeZone = timeZoneMonitor.currentTimeZone
        .stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            TimeZone.currentSystemDefault(),
        )

    fun navigateToHome() = navController.navigateToHome()
}
