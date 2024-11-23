package br.com.b256.feature.home

import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val location by viewModel.location.collectAsState()

    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        location = location,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    location: Location?,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(text = "Location Updates")
        location?.let {
            Text(text = "Latitude: ${it.latitude}")
            Text(text = "Longitude: ${it.longitude}")
            Text(text = "Altitude: ${it.altitude}")
            Text(text = "Accuracy: ${it.accuracy}")
        } ?: Text(text = "Waiting for location...")

    }
}
