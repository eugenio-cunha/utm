package br.com.b256.feature.utm

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun UtmScreen(
    modifier: Modifier = Modifier,
    viewModel: UtmViewModel = hiltViewModel(),
) {
    UtmScreen(
        modifier = modifier,
    )
}

@Composable
private fun UtmScreen(modifier: Modifier = Modifier) {
    Text("utm")
}
