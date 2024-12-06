package br.com.b256.feature.utm

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.b256.core.designsystem.icon.B256Icons
import br.com.b256.core.designsystem.theme.PaddingHalf
import br.com.b256.core.gps.extension.UTM

@Composable
internal fun UtmScreen(
    modifier: Modifier = Modifier,
    viewModel: UtmViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val location by viewModel.location.collectAsStateWithLifecycle()

    UtmScreen(
        modifier = modifier,
        location = location,
        onShare = {
            share(context = context, value = it)
        },
    )
}

@Composable
private fun UtmScreen(
    modifier: Modifier = Modifier,
    location: Location?,
    onShare: (String) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    location?.UTM?.also {
                        onShare("${it.zone} ${it.easting} ${it.northing}")
                    }
                },
            ) {
                Icon(
                    imageVector = B256Icons.Share,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                location?.UTM?.let {
                    Text(

                        text = it.zone,
                        style = MaterialTheme.typography.displayMedium,
                    )
                    Text(
                        text = it.easting,
                        style = MaterialTheme.typography.displayMedium,
                    )
                    Text(
                        text = it.northing,
                        style = MaterialTheme.typography.displayMedium,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                location?.let {
                    Column(
                        modifier = Modifier.padding(PaddingHalf),
                    ) {
                        Text(
                            text = stringResource(R.string.feature_utm_altitude),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.End,
                        )
                        Text(
                            text = stringResource(R.string.feature_utm_meters, it.altitude.toInt()),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.End,
                        )
                    }

                    VerticalDivider(
                        modifier = Modifier
                            .height(50.dp),
                    )

                    Column(
                        modifier = Modifier.padding(PaddingHalf),
                    ) {
                        Text(
                            text = stringResource(R.string.feature_utm_accuracy),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Start,
                        )
                        Text(
                            text = stringResource(R.string.feature_utm_meters, it.accuracy.toInt()),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Start,
                        )
                    }
                }
            }
        }
    }
}


private fun share(context: Context, value: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, value)
        type = "text/plain"
    }

    Intent.createChooser(sendIntent, null).also {
        context.startActivity(it)
    }
}
