package br.com.b256.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import br.com.b256.R
import br.com.b256.core.designsystem.icon.B256Icons
import br.com.b256.feature.utm.navigation.UtmRoute
import kotlin.reflect.KClass

enum class B256Destination(
    val icon: ImageVector,
    @StringRes val titleTextId: Int,
    val route: KClass<*>,
) {
    UTM(
        icon = B256Icons.Place,
        titleTextId = R.string.app_place,
        route = UtmRoute::class,
    ),
}
