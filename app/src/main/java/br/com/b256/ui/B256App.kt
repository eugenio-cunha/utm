package br.com.b256.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.b256.R
import br.com.b256.core.designsystem.component.B256Background
import br.com.b256.core.designsystem.component.B256TopAppBar
import br.com.b256.core.designsystem.icon.B256Icons
import br.com.b256.feature.settings.SettingsDialog
import br.com.b256.navigation.B256NavHost

@Composable
fun B256App(
    modifier: Modifier = Modifier,
    appState: B256AppState,
) {
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }

    B256Background(modifier = modifier) {
        val snackbarHostState = remember { SnackbarHostState() }

        val isOffline by appState.isOffline.collectAsStateWithLifecycle()

        // Se o usuário não estiver conectado à internet, mostre uma barra de ferramentas para informá-lo.
        val offlineMessage = stringResource(R.string.app_offline)
        LaunchedEffect(isOffline) {
            if (isOffline) {
                snackbarHostState.showSnackbar(
                    message = offlineMessage,
                    duration = Short,
                )
            }
        }

        B256App(
            modifier = modifier,
            appState = appState,
            snackbarHostState = snackbarHostState,
            showSettingsDialog = showSettingsDialog,
            onSettingsDismissed = { showSettingsDialog = false },
            onTopAppBarActionClick = { showSettingsDialog = true },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun B256App(
    modifier: Modifier = Modifier,
    appState: B256AppState,
    snackbarHostState: SnackbarHostState,
    showSettingsDialog: Boolean,
    onTopAppBarActionClick: () -> Unit,
    onSettingsDismissed: () -> Unit,
) {
    if (showSettingsDialog) {
        SettingsDialog(
            onDismiss = { onSettingsDismissed() },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            B256TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                ),
                actionIcon = B256Icons.More,
                onActionClick = onTopAppBarActionClick,
            )
        },
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal,
                    ),
                ),
        ) {
            B256NavHost(
                appState = appState,
            )
        }
    }
}
