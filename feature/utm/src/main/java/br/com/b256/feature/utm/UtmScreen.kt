package br.com.b256.feature.utm

import android.Manifest
import android.content.Context
import android.content.Intent
import java.util.Locale
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.b256.core.designsystem.icon.B256Icons
import br.com.b256.core.designsystem.theme.PaddingDouble
import br.com.b256.core.designsystem.theme.PaddingSingle
import br.com.b256.core.model.GnssInfo
import br.com.b256.core.model.GnssSatellite
import br.com.b256.core.model.GpsLocation
import br.com.b256.core.model.Orientation
import br.com.b256.feature.utm.components.GnssSkyPlot
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Composable que representa a tela principal para exibição de coordenadas UTM.
 *
 * Esta tela é responsável por observar o estado da interface vindo do [UtmViewModel],
 * lidar com efeitos colaterais como o compartilhamento de coordenadas e gerenciar
 * solicitações de permissão de localização. Utiliza o [LocationPermissionEffect] para
 * garantir que as permissões necessárias sejam concedidas antes de iniciar o serviço
 * de localização. A interface real é delegada para uma função `UtmScreen` privada.
 *
 * @param modifier O modificador a ser aplicado à tela.
 * @param viewModel O [UtmViewModel] responsável por gerenciar o estado e a lógica da tela.
 *                  Injetado automaticamente via Hilt.
 */
@Composable
internal fun UtmScreen(
    modifier: Modifier = Modifier,
    viewModel: UtmViewModel = hiltViewModel(),
) {
    val locationState by viewModel.locationState.collectAsStateWithLifecycle()
    val gnssState by viewModel.gnssStatus.collectAsStateWithLifecycle()
    val orientationState by viewModel.orientation.collectAsStateWithLifecycle()

    LocationPermissionEffect {
        viewModel.refresh()
    }

    UtmScreen(
        modifier = modifier,
        locationState = locationState,
        gnssState = gnssState,
        orientationState = orientationState,
    )
}

@Composable
private fun UtmScreen(
    modifier: Modifier = Modifier,
    gnssState: GnssInfo?,
    locationState: GpsLocation?,
    orientationState: Orientation?,
) {
    val locale = LocalConfiguration.current.locales[0]

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(PaddingSingle),
        verticalArrangement = Arrangement.spacedBy(PaddingSingle),
    ) {
        item(key = "location") {
            if (locationState != null) {
                Location(
                    locationState = locationState,
                )
            } else {
                LocationSkeleton()
            }
        }

        item(key = "skyplot") {
            if (gnssState != null && orientationState != null) {
                SkyPlot(
                    gnssState = gnssState,
                    orientationState = orientationState,
                )
            } else {
                SkyPlotSkeleton()
            }
        }

        gnssState?.let {
            satelliteItems(it, locale = locale)
        }
    }
}

@Composable
private fun LocationSkeleton(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingSingle),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(PaddingDouble),
            verticalArrangement = Arrangement.spacedBy(PaddingDouble),
        ) {
            // Geographic Coordinates Skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PaddingDouble),
            ) {
                SkeletonItem(Modifier.weight(1f), alpha)
                SkeletonItem(Modifier.weight(1f), alpha)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // UTM Coordinates Skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PaddingDouble),
            ) {
                SkeletonItem(Modifier.weight(0.5f), alpha)
                SkeletonItem(Modifier.weight(1f), alpha)
                SkeletonItem(Modifier.weight(1f), alpha)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Altitude and Accuracy Skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PaddingDouble),
            ) {
                SkeletonItem(Modifier.weight(1f), alpha)
                SkeletonItem(Modifier.weight(1f), alpha)
            }
        }
    }
}

@Composable
private fun SkeletonItem(modifier: Modifier = Modifier, alpha: Float) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(12.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha * 0.2f))
        )
        Spacer(modifier = Modifier.height(PaddingSingle))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(20.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha))
        )
    }
}

@Composable
private fun SkyPlotSkeleton(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingSingle),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(PaddingDouble),
            contentAlignment = Alignment.Center
        ) {
            // Large circular shimmer representing the SkyPlot grid
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha * 0.1f))
            )
            // Smaller concentric circles
            Box(
                modifier = Modifier
                    .fillMaxSize(0.66f)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha * 0.15f))
            )
            Box(
                modifier = Modifier
                    .fillMaxSize(0.33f)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha * 0.2f))
            )
        }
    }
}

@Composable
private fun SkyPlot(
    modifier: Modifier = Modifier,
    gnssState: GnssInfo,
    orientationState: Orientation,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingSingle),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        ),
    ) {
        GnssSkyPlot(
            modifier = modifier,
            gnssInfo = gnssState,
            orientation = orientationState,
        )
    }
}

private fun LazyListScope.satelliteItems(
    gnssInfo: GnssInfo,
    locale: Locale,
) {
    item("satellite_header") {
        SatelliteHeader()
    }

    items(
        items = gnssInfo.satellites,
        key = { "${it.constellation}_${it.svid}" },
    ) {
        SatelliteRow(
            satellite = it,
            locale = locale,
        )
    }
}

@Composable
private fun SatelliteHeader() {
    Column(modifier = Modifier.padding(PaddingSingle)) {
        Text(
            text = stringResource(R.string.feature_utm_satellites).uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = PaddingSingle),
        )

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.feature_utm_satellite_id),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(40.dp),
            )
            Text(
                text = "CONST.",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.feature_utm_satellite_signal),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(60.dp),
                textAlign = TextAlign.End,
            )
            Text(
                text = stringResource(R.string.feature_utm_satellite_elevation),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.End,
            )
            Text(
                text = stringResource(R.string.feature_utm_satellite_azimuth),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.End,
            )
        }

        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun SatelliteRow(
    satellite: GnssSatellite,
    locale: Locale,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingSingle),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = satellite.svid.toString(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (satellite.usedInFix) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.width(40.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(satellite.constellation.color)),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = satellite.constellation.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            text = String.format(locale, "%.1f", satellite.cn0DbHz),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(60.dp),
            textAlign = TextAlign.End,
        )
        Text(
            text = String.format(locale, "%.0f°", satellite.elevationDegrees),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(50.dp),
            textAlign = TextAlign.End,
        )
        Text(
            text = String.format(locale, "%.0f°", satellite.azimuthDegrees),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(50.dp),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun Location(
    modifier: Modifier = Modifier,
    locationState: GpsLocation?,
) {
    if (locationState == null) return

    val locale = LocalConfiguration.current.locales[0]
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingSingle),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(PaddingDouble),
            verticalArrangement = Arrangement.spacedBy(PaddingDouble),
        ) {
            // Header with Title and Share Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.feature_utm_location_title).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                val shareTitle = stringResource(R.string.feature_utm_location_title)
                val latLabel = stringResource(R.string.feature_utm_latitude)
                val lonLabel = stringResource(R.string.feature_utm_longitude)
                val zoneLabel = stringResource(R.string.feature_utm_zone)
                val eastingLabel = stringResource(R.string.feature_utm_easting)
                val northingLabel = stringResource(R.string.feature_utm_northing)
                val altitudeLabel = stringResource(R.string.feature_utm_altitude)
                val accuracyLabel = stringResource(R.string.feature_utm_accuracy)

                IconButton(
                    onClick = {
                        val shareText = buildString {
                            appendLine("$shareTitle:")
                            appendLine("$latLabel: ${String.format(locale, "%.6f", locationState.latitude)}°")
                            appendLine("$lonLabel: ${String.format(locale, "%.6f", locationState.longitude)}°")
                            appendLine("$zoneLabel: ${locationState.utm.zone}")
                            appendLine("$eastingLabel: ${locationState.utm.easting}")
                            appendLine("$northingLabel: ${locationState.utm.northing}")
                            locationState.altitude?.let {
                                appendLine("$altitudeLabel: ${String.format(locale, "%.2f", it)}m")
                            }
                            locationState.accuracy?.let {
                                appendLine("$accuracyLabel: ±${String.format(locale, "%.1f", it)}m")
                            }
                        }
                        share(context, shareText)
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = B256Icons.Share,
                        contentDescription = stringResource(R.string.feature_utm_share),
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Geographic Coordinates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PaddingDouble),
            ) {
                LocationInfoItem(
                    label = stringResource(R.string.feature_utm_latitude),
                    value = String.format(locale, "%.6f", locationState.latitude),
                    modifier = Modifier.weight(1f),
                )
                LocationInfoItem(
                    label = stringResource(R.string.feature_utm_longitude),
                    value = String.format(locale, "%.6f", locationState.longitude),
                    modifier = Modifier.weight(1f),
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // UTM Coordinates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PaddingDouble),
            ) {
                LocationInfoItem(
                    label = stringResource(R.string.feature_utm_zone),
                    value = locationState.utm.zone,
                    modifier = Modifier.weight(0.5f),
                )
                LocationInfoItem(
                    label = stringResource(R.string.feature_utm_easting),
                    value = locationState.utm.easting,
                    modifier = Modifier.weight(1f),
                )
                LocationInfoItem(
                    label = stringResource(R.string.feature_utm_northing),
                    value = locationState.utm.northing,
                    modifier = Modifier.weight(1f),
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Altitude and Accuracy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PaddingDouble),
            ) {
                LocationInfoItem(
                    label = stringResource(R.string.feature_utm_altitude),
                    value = stringResource(
                        R.string.feature_utm_meters,
                        locationState.altitude ?: 0.0,
                    ),
                    modifier = Modifier.weight(1f),
                )
                LocationInfoItem(
                    label = stringResource(R.string.feature_utm_accuracy),
                    value = stringResource(
                        R.string.feature_utm_meters,
                        locationState.accuracy ?: 0.0,
                    ),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun LocationInfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Função Composable que lida com as solicitações de permissão de localização.
 *
 * Esta função verifica as permissões de localização necessárias (ACCESS_FINE_LOCATION,
 * ACCESS_COARSE_LOCATION e FOREGROUND_SERVICE_LOCATION para Android U e superior).
 * Se as permissões não forem concedidas, ela inicia uma solicitação de permissão.
 * Assim que todas as permissões forem concedidas, ela executa a lambda [onPermissionGranted].
 *
 * Esta função não faz nada se estiver em execução no modo de inspeção (por exemplo, no Android Studio Layout Editor).
 *
 * @param onPermissionGranted Uma função, lambda a ser executada quando todas as permissões de localização necessárias forem concedidas.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionEffect(onPermissionGranted: () -> Unit) {
    if (LocalInspectionMode.current) return

    val permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val permissionState = rememberMultiplePermissionsState(
        permissions = permissions,
        onPermissionsResult = { result ->
            if (result.values.any { it }) {
                onPermissionGranted()
            }
        },
    )

    val anyPermissionGranted = permissionState.permissions.any { it.status.isGranted }

    LaunchedEffect(anyPermissionGranted) {
        if (anyPermissionGranted) {
            onPermissionGranted()
        } else {
            permissionState.launchMultiplePermissionRequest()
        }
    }
}

/**
 * Compartilha o [value] informado usando uma Intent do Android.
 *
 * Esta função cria uma Intent com a ação `Intent.ACTION_SEND` e define o tipo como "text/plain".
 * O [value] é adicionado como um extra com a chave `Intent.EXTRA_TEXT`.
 * Em seguida, cria um seletor para a Intent e inicia a atividade.
 *
 * @param context O [Context] usado para iniciar a atividade.
 * @param value O valor [String] a ser compartilhado.
 */
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
