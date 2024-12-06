package br.com.b256.core.designsystem.component

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import br.com.b256.core.designsystem.icon.B256Icons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun B256TopAppBar(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    actionIcon: ImageVector? = null,
    onActionClick: (() -> Unit)? = null,
    onNavigationClick: (() -> Unit)? = null,
) {
    CenterAlignedTopAppBar(
        modifier = modifier.testTag("B256TopAppBar"),
        title = {
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        colors = colors,
        actions = {
            if (actionIcon != null && onActionClick != null)
                IconButton(onClick = onActionClick) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
        },
        navigationIcon = {
            if (onNavigationClick != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = B256Icons.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun B256TopAppBar(
    modifier: Modifier = Modifier,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    actionIcon: ImageVector,
    onActionClick: (() -> Unit),
) {
    CenterAlignedTopAppBar(
        modifier = modifier.testTag("B256TopAppBar"),
        title = {},
        colors = colors,
        actions = {
            IconButton(onClick = onActionClick) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        navigationIcon = {},
    )
}
