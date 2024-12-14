package br.com.b256.navigation

import br.com.b256.feature.utm.navigation.UtmRoute
import kotlin.reflect.KClass

enum class B256Destination(
    val route: KClass<*>,
) {
    UTM(
        route = UtmRoute::class,
    ),
}
