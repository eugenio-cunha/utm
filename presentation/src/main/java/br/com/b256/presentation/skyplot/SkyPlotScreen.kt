package br.com.b256.presentation.skyplot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.b256.presentation.R
import br.com.b256.presentation.designsystem.asset.Asset
import br.com.b256.presentation.designsystem.component.B256TopAppBar
import br.com.b256.presentation.settings.SettingsDialog

/**
 * Composable "stateful" da feature (ver [SkyPlotViewModel]): obtém o `ViewModel` via `hiltViewModel()`
 * e delega para a versão "stateless" abaixo. É este composable — não o privado — que deve ser
 * chamado pela navegação (ver `home/navigation/HomeNavigation.kt`).
 */
@Composable
internal fun SkyPlotScreen(
    viewModel: SkyPlotViewModel = hiltViewModel(),
) {
    SkyPlotScreen(onClick = viewModel::onClick)
}

/**
 * Versão "stateless" da tela: recebe apenas callbacks/estado como parâmetros, sem depender do
 * `ViewModel` — mantém a UI facilmente pré-visualizável (`@Preview`) e testável sem Hilt.
 * `internal` (em vez de `private`) para ficar visível a partir de `HomeScreenTest`, no
 * `androidTest` deste módulo.
 *
 * A tela não declara um `Scaffold` próprio — o `Scaffold` da aplicação vive em
 * [B256App]. Como nem toda tela usa a mesma topbar, cada feature posiciona a
 * sua ([B256TopAppBar]) no topo do próprio conteúdo.
 */
@Composable
internal fun SkyPlotScreen(
    onClick: () -> Unit,
) {
    var showSettingsDialog by remember { mutableStateOf(false) }

    if (showSettingsDialog) {
        SettingsDialog(onDismiss = { showSettingsDialog = false })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        B256TopAppBar(
            titleRes = R.string.presentation_skyplot_title,
            actionIcon = Asset.Settings,
            actionIconContentDescription = stringResource(
                R.string.presentation_skyplot_top_app_bar_action_settings,
            ),
            onActionClick = { showSettingsDialog = true },
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "SkyPlotScreen")

            Button(onClick = onClick) {
                Text("Click")
            }
        }
    }
}
