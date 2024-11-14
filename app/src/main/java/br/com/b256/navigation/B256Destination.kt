package br.com.b256.navigation

import androidx.annotation.StringRes
import br.com.b256.R
import br.com.b256.feature.home.navigation.HomeRoute
import kotlin.reflect.KClass

enum class B256Destination(
    @StringRes val titleTextId: Int,
    val route: KClass<*>,
) {
    HOME(
        titleTextId = R.string.app_name,
        route = HomeRoute::class
    )
}
