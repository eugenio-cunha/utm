package br.com.b256.presentation.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

/**
 * Topbar padrão do design system, centralizada no título e com espaço para um ícone de navegação
 * (à esquerda) e um ícone de ação (à direita). Cada feature reaproveita este composable em vez de
 * montar um [CenterAlignedTopAppBar] na mão — ver uso em
 * [SkyPlotScreen].
 *
 * @param titleRes Recurso de string exibido como título.
 * @param modifier Modificador aplicado à topbar.
 * @param navigationIcon Ícone opcional à esquerda; quando `null`, nenhum ícone é exibido.
 * @param navigationIconContentDescription Descrição de acessibilidade do ícone de navegação.
 * @param actionIcon Ícone opcional à direita; quando `null`, nenhuma ação é exibida.
 * @param actionIconContentDescription Descrição de acessibilidade do ícone de ação.
 * @param colors Cores da topbar (por padrão, as do [TopAppBarDefaults.centerAlignedTopAppBarColors]).
 * @param onNavigationClick Callback do clique no ícone de navegação.
 * @param onActionClick Callback do clique no ícone de ação.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun B256TopAppBar(
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = null,
    navigationIconContentDescription: String? = null,
    actionIcon: ImageVector? = null,
    actionIconContentDescription: String? = null,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = colors,
        title = { Text(text = stringResource(id = titleRes)) },
        navigationIcon = {
            if (navigationIcon != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        actions = {
            if (actionIcon != null) {
                IconButton(onClick = onActionClick) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = actionIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
    )
}
