package br.com.b256.presentation.settings

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.b256.domain.entities.enums.Datum
import br.com.b256.domain.entities.enums.Theme
import br.com.b256.presentation.settings.SettingsUiState.Success
import br.com.b256.presentation.settings.SettingsUiState.Loading
import br.com.b256.presentation.R

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
        onChangeDatum = viewModel::onChangeDatum,
    )
}

@Composable
fun SettingsDialog(
    uiState: SettingsUiState,
    onDismiss: () -> Unit,
    onChangeTheme: (theme: Theme) -> Unit,
    onChangeDatum: (datum: Datum) -> Unit,
) {
    val configuration = LocalConfiguration.current

    AlertDialog(
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = stringResource(R.string.presentation_settings_title),
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
                            text = stringResource(R.string.presentation_settings_loading),
                        )
                    }

                    is Success -> {
                        SettingsPanel(
                            theme = uiState.theme,
                            onChangeTheme = onChangeTheme,
                            datum = uiState.datum,
                            onChangeDatum = onChangeDatum,
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
                text = stringResource(R.string.presentation_settings_dismiss_dialog_button_text),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        },
    )
}

// [ColumnScope] é usado para usar a sobrecarga de extensão [ColumnScope.AnimatedVisibility] composable.
@Composable
private fun ColumnScope.SettingsPanel(
    theme: Theme,
    onChangeTheme: (theme: Theme) -> Unit,
    datum: Datum,
    onChangeDatum: (datum: Datum) -> Unit,
) {
    Text(
        text = stringResource(R.string.presentation_settings_theme),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    )

    Column(Modifier.selectableGroup()) {
        SettingsDialogChooserRow(
            text = stringResource(R.string.presentation_settings_theme_default),
            selected = theme == Theme.FOLLOW_SYSTEM,
            onClick = { onChangeTheme(Theme.FOLLOW_SYSTEM) },
        )
        SettingsDialogChooserRow(
            text = stringResource(R.string.presentation_settings_theme_light),
            selected = theme == Theme.LIGHT,
            onClick = { onChangeTheme(Theme.LIGHT) },
        )
        SettingsDialogChooserRow(
            text = stringResource(R.string.presentation_settings_theme_dark),
            selected = theme == Theme.DARK,
            onClick = { onChangeTheme(Theme.DARK) },
        )
    }

    HorizontalDivider(Modifier.padding(vertical = 8.dp))

    Text(
        text = stringResource(R.string.presentation_settings_datum),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Column(Modifier.selectableGroup()) {
        SettingsDialogChooserRow(
            text = stringResource(R.string.presentation_settings_datum_wgs84),
            selected = datum == Datum.WGS84,
            onClick = { onChangeDatum(Datum.WGS84) },
        )

        SettingsDialogChooserRow(
            text = stringResource(R.string.presentation_settings_datum_sirgas2000),
            selected = datum == Datum.SIRGAS2000,
            onClick = { onChangeDatum(Datum.SIRGAS2000) },
        )
        SettingsDialogChooserRow(
            text = stringResource(R.string.presentation_settings_datum_sad69),
            selected = datum == Datum.SAD69,
            onClick = { onChangeDatum(Datum.SAD69) },
        )
        SettingsDialogChooserRow(
            text = stringResource(R.string.presentation_settings_datum_corrego_alegre),
            selected = datum == Datum.CORREGO_ALEGRE,
            onClick = { onChangeDatum(Datum.CORREGO_ALEGRE) },
        )

        SettingsDialogChooserRow(
            text = stringResource(R.string.presentation_settings_datum_nad83),
            selected = datum == Datum.NAD83,
            onClick = { onChangeDatum(Datum.NAD83) },
        )
        SettingsDialogChooserRow(
            text = stringResource(R.string.presentation_settings_datum_nad27),
            selected = datum == Datum.NAD27,
            onClick = { onChangeDatum(Datum.NAD27) },
        )

        SettingsDialogChooserRow(
            text = stringResource(R.string.presentation_settings_datum_etrs89),
            selected = datum == Datum.ETRS89,
            onClick = { onChangeDatum(Datum.ETRS89) },
        )
        SettingsDialogChooserRow(
            text = stringResource(R.string.presentation_settings_datum_ed50),
            selected = datum == Datum.ED50,
            onClick = { onChangeDatum(Datum.ED50) },
        )
    }
}

@Composable
fun SettingsDialogChooserRow(
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
