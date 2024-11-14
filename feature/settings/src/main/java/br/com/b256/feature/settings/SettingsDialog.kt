package br.com.b256.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.b256.core.model.Theme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import br.com.b256.core.model.Settings
import br.com.b256.feature.settings.SettingsUiState.Loading
import br.com.b256.feature.settings.SettingsUiState.Success

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsDialog(
        uiState = uiState,
        onDismiss = onDismiss,
        onChangeTheme = viewModel::onChangeTheme,
    )
}

@Composable
fun SettingsDialog(
    uiState: SettingsUiState,
    onDismiss: () -> Unit,
    onChangeTheme: (theme: Theme) -> Unit,
) {
    val configuration = LocalConfiguration.current

    AlertDialog(
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = stringResource(R.string.feature_settings_title),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            HorizontalDivider()
            Column(Modifier.verticalScroll(rememberScrollState())) {
                when (uiState) {
                    Loading -> {
                        Text(
                            modifier = Modifier.padding(vertical = 16.dp),
                            text = stringResource(R.string.feature_settings_loading),
                        )
                    }

                    is Success -> {
                        SettingsPanel(
                            settings = uiState.settings,
                            onChangeTheme = onChangeTheme,
                        )
                    }
                }
                HorizontalDivider(Modifier.padding(top = 8.dp))
            }
        },
        confirmButton = {
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onDismiss() },
                text = stringResource(R.string.feature_settings_dismiss_dialog_button_text),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        },
    )
}

// [ColumnScope] é usado para usar a sobrecarga de extensão [ColumnScope.AnimatedVisibility] composable.
@Composable
private fun ColumnScope.SettingsPanel(
    settings: Settings,
    onChangeTheme: (theme: Theme) -> Unit,
) {
    Text(
        text = stringResource(R.string.feature_settings_theme),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    )

    Column(Modifier.selectableGroup()) {
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.feature_settings_theme_default),
            selected = settings.theme == Theme.FOLLOW_SYSTEM,
            onClick = { onChangeTheme(Theme.FOLLOW_SYSTEM) },
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.feature_settings_theme_light),
            selected = settings.theme == Theme.LIGHT,
            onClick = { onChangeTheme(Theme.LIGHT) },
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.feature_settings_theme_dark),
            selected = settings.theme == Theme.DARK,
            onClick = { onChangeTheme(Theme.DARK) },
        )
    }
}

@Composable
fun SettingsDialogThemeChooserRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}
