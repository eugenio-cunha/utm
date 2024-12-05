package br.com.b256.feature.utm.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.b256.feature.utm.UtmScreen
import kotlinx.serialization.Serializable

@Serializable
data object UtmRoute

fun NavController.navigateToUtm(options: NavOptions? = null) = navigate(route = UtmRoute, options)

fun NavGraphBuilder.utmScreen() {
    composable<UtmRoute> {
        UtmScreen()
    }
}
