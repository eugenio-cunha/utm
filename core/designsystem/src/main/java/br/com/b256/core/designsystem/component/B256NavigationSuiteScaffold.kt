package br.com.b256.core.designsystem.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Valores padrão de navegação.
 */
object B256NavigationDefaults {
    @Composable
    fun navigationBarContainerColor() = MaterialTheme.colorScheme.background

    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.primary

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer

    @Composable
    fun navigationRailContainerColor() = Color.Transparent
}

/**
 * Um wrapper em torno de [NavigationSuiteScope] para declarar itens de navegação.
 */
class B256NavigationSuiteScope internal constructor(
    private val navigationSuiteScope: NavigationSuiteScope,
    private val navigationSuiteItemColors: NavigationSuiteItemColors,
) {
    fun item(
        modifier: Modifier = Modifier,
        selected: Boolean,
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        selectedIcon: @Composable () -> Unit = icon,
        label: @Composable (() -> Unit)? = null,
    ) = navigationSuiteScope.item(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        icon = {
            if (selected) {
                selectedIcon()
            } else {
                icon()
            }
        },
        label = label,
        colors = navigationSuiteItemColors,
    )
}

@Composable
fun B256NavigationSuiteScaffold(
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    navigationSuiteItems: B256NavigationSuiteScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    val layoutType = NavigationSuiteScaffoldDefaults
        .calculateFromAdaptiveInfo(windowAdaptiveInfo)

    val navigationSuiteItemColors = NavigationSuiteItemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = B256NavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = B256NavigationDefaults.navigationContentColor(),
            selectedTextColor = B256NavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = B256NavigationDefaults.navigationContentColor(),
            indicatorColor = B256NavigationDefaults.navigationIndicatorColor(),
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = B256NavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = B256NavigationDefaults.navigationContentColor(),
            selectedTextColor = B256NavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = B256NavigationDefaults.navigationContentColor(),
            indicatorColor = B256NavigationDefaults.navigationIndicatorColor(),
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = B256NavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = B256NavigationDefaults.navigationContentColor(),
            selectedTextColor = B256NavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = B256NavigationDefaults.navigationContentColor(),
        ),
    )

    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            B256NavigationSuiteScope(
                navigationSuiteScope = this,
                navigationSuiteItemColors = navigationSuiteItemColors,
            ).run(navigationSuiteItems)
        },
        layoutType = layoutType,
        containerColor = Color.Transparent,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = B256NavigationDefaults.navigationBarContainerColor(),
            navigationBarContentColor = B256NavigationDefaults.navigationContentColor(),
            navigationRailContainerColor = B256NavigationDefaults.navigationRailContainerColor(),
        ),
    ) {
        content()
    }
}
