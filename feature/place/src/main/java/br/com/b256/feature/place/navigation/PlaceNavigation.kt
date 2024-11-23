package br.com.b256.feature.place.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.b256.feature.place.PlaceScreen
import kotlinx.serialization.Serializable

@Serializable
data object PlaceRoute

fun NavController.navigateToPlace(options: NavOptions? = null) = navigate(route = PlaceRoute, options)

fun NavGraphBuilder.placeScreen() {
    composable<PlaceRoute> {
        PlaceScreen()
    }
}
