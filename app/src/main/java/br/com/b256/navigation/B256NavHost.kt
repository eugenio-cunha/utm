package br.com.b256.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import br.com.b256.feature.utm.navigation.UtmRoute
import br.com.b256.feature.utm.navigation.utmScreen
import br.com.b256.ui.B256AppState

@Composable
fun B256NavHost(
    modifier: Modifier = Modifier,
    appState: B256AppState,
) {
    val navController = appState.navController
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = UtmRoute,
    ) {
        utmScreen()
    }
}
