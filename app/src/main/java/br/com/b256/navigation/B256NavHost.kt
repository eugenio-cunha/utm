package br.com.b256.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import br.com.b256.feature.home.navigation.HomeRoute
import br.com.b256.feature.home.navigation.homeScreen
import br.com.b256.ui.B256AppState

@Composable
fun B256NavHost(
    modifier: Modifier = Modifier,
    appState: B256AppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
){
    val navController = appState.navController
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = HomeRoute
    ) {
        homeScreen(onShowSnackbar = onShowSnackbar,)
    }
}
