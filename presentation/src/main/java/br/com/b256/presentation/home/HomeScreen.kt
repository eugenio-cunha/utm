package br.com.b256.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

/**
 * Composable "stateful" da feature (ver [HomeViewModel]): obtém o `ViewModel` via `hiltViewModel()`
 * e delega para a versão "stateless" abaixo. É este composable — não o privado — que deve ser
 * chamado pela navegação (ver `home/navigation/HomeNavigation.kt`).
 */
@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    HomeScreen(onClick = viewModel::onClick)
}

/**
 * Versão "stateless" da tela: recebe apenas callbacks/estado como parâmetros, sem depender do
 * `ViewModel` — mantém a UI facilmente pré-visualizável (`@Preview`) e testável sem Hilt.
 * `internal` (em vez de `private`) para ficar visível a partir de `HomeScreenTest`, no
 * `androidTest` deste módulo.
 */
@Composable
internal fun HomeScreen(
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Home Screen")

        Button(onClick = onClick) {
            Text("Click")
        }
    }
}
