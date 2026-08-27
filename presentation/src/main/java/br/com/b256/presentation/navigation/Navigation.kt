package br.com.b256.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import br.com.b256.presentation.home.navigation.HomeRoute
import br.com.b256.presentation.home.navigation.homeScreen

/**
 * Ponto único de composição da navegação (Navigation3), chamado a partir de
 * [br.com.b256.gnss.MainActivity]. Agrega o `entryProvider` de cada feature (ver
 * `homeScreen()` em `home/navigation/HomeNavigation.kt`) — uma nova feature entra aqui como mais
 * uma chamada dentro de `entryProvider { ... }`.
 */
@Composable
fun B256NavDisplay(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(HomeRoute)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeAt(backStack.lastIndex) },
        entryProvider =
            entryProvider<NavKey> {
                homeScreen()
            },
    )
}
