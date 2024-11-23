package br.com.b256.feature.place

import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun PlaceScreen(
    modifier: Modifier = Modifier,
    viewModel: PlaceViewModel = hiltViewModel(),
){
    val location by viewModel.location.collectAsState()

    PlaceScreen(
        modifier = modifier,
        location = location
    )
}

@Composable
private fun PlaceScreen(
    modifier: Modifier = Modifier,
    location: Location?
){
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Text(text = "Place")
        location?.let {
            Text(text = "Latitude: ${it.latitude}")
            Text(text = "Longitude: ${it.longitude}")
            Text(text = "Altitude: ${it.altitude}")
            Text(text = "Accuracy: ${it.accuracy}")
        } ?: Text(text = "Waiting for location...")

    }
}
