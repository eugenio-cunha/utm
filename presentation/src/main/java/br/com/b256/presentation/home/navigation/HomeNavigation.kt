package br.com.b256.presentation.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import br.com.b256.presentation.home.HomeScreen
import kotlinx.serialization.Serializable

/**
 * Rota de referência da feature `home`: um `NavKey` `@Serializable` por destino (aqui um
 * `data object` por não ter argumentos; uma rota com parâmetros seria uma `data class`).
 */
@Serializable
data object HomeRoute : NavKey

/**
 * Registro da(s) tela(s) da feature `home` no grafo de navegação. Convenção do projeto: cada
 * feature expõe uma extension `EntryProviderScope<NavKey>.xScreen()` própria, chamada a partir de
 * [br.com.b256.presentation.navigation.B256NavDisplay] — mantém a feature autocontida, sem editar
 * o `Navigation.kt` central além de adicionar essa chamada.
 */
fun EntryProviderScope<NavKey>.homeScreen() {
    entry<HomeRoute> {
        HomeScreen()
    }
}
