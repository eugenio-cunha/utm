package br.com.b256.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.b256.feature.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavController.navigateToHome(options: NavOptions? = null) = navigate(route = HomeRoute, options)

fun NavGraphBuilder.homeScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<HomeRoute> {
        HomeScreen(onShowSnackbar = onShowSnackbar)
    }
}
