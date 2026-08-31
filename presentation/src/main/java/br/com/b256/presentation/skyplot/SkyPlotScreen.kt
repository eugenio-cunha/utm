package br.com.b256.presentation.skyplot

import android.Manifest
import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import br.com.b256.domain.entities.GnssInfo
import br.com.b256.domain.entities.GnssSatellite
import br.com.b256.domain.entities.GpsLocation
import br.com.b256.domain.entities.Orientation
import br.com.b256.presentation.R
import br.com.b256.presentation.designsystem.asset.Asset
import br.com.b256.presentation.designsystem.component.B256TopAppBar
import br.com.b256.presentation.designsystem.theme.PaddingDouble
import br.com.b256.presentation.designsystem.theme.PaddingSingle
import br.com.b256.presentation.settings.SettingsDialog
import br.com.b256.presentation.skyplot.components.GnssSkyPlot
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.util.Locale

/**
 * Composable "stateful" da feature (ver [SkyPlotViewModel]): obtém o `ViewModel` via `hiltViewModel()`
 * e delega para a versão "stateless" abaixo. É este composable — não o privado — que deve ser
 * chamado pela navegação (ver `home/navigation/HomeNavigation.kt`).
 */
@Composable
internal fun SkyPlotScreen(
    viewModel: SkyPlotViewModel = hiltViewModel(),
) {
    val gnssState by viewModel.gnssStatus.collectAsStateWithLifecycle()
    val locationState by viewModel.locationState.collectAsStateWithLifecycle()
    val orientationState by viewModel.orientation.collectAsStateWithLifecycle()

    LocationPermissionEffect {
        viewModel.refresh()
    }

    SkyPlotScreen(
        locationState = locationState,
        gnssState = gnssState,
        orientationState = orientationState,
    )
}

/**
 * Versão "stateless" da tela: recebe apenas callbacks/estado como parâmetros, sem depender do
 * `ViewModel` — mantém a UI facilmente pré-visualizável (`@Preview`) e testável sem Hilt.
 * `internal` (em vez de `private`) para ficar visível a partir de `HomeScreenTest`, no
 * `androidTest` deste módulo.
 *
 * A tela não declara um `Scaffold` próprio — o `Scaffold` da aplicação vive em
 * [B256App]. Como nem toda tela usa a mesma topbar, cada feature posiciona a
 * sua ([B256TopAppBar]) no topo do próprio conteúdo.
 */
@Composable
internal fun SkyPlotScreen(
    modifier: Modifier = Modifier,
    gnssState: GnssInfo?,
    locationState: GpsLocation?,
    orientationState: Orientation?,
) {
    val locale = LocalConfiguration.current.locales[0]
    var showSettingsDialog by remember { mutableStateOf(false) }

    if (showSettingsDialog) {
        SettingsDialog(onDismiss = { showSettingsDialog = false })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        B256TopAppBar(
            titleRes = R.string.presentation_skyplot_title,
            actionIcon = Asset.Settings,
            actionIconContentDescription = stringResource(
                R.string.presentation_skyplot_top_app_bar_action_settings,
            ),
            onActionClick = { showSettingsDialog = true },
        )

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
}

/**
 * Placeholder animado (efeito "shimmer") exibido no lugar do card [Location] enquanto a
 * localização ainda não foi obtida.
 *
 * @param modifier [Modifier] aplicado ao [Card] raiz.
 */
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

/**
 * Bloco individual do efeito "shimmer": desenha um rótulo curto e um valor mais largo, ambos
 * como retângulos preenchidos, usados para compor os skeletons de carregamento.
 *
 * @param modifier [Modifier] aplicado à [Column] raiz.
 * @param alpha Opacidade atual da animação, usada para dar o efeito de pulsação.
 */
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

/**
 * Placeholder animado (efeito "shimmer") exibido no lugar do card [SkyPlot] enquanto os dados
 * de GNSS e orientação ainda não estão disponíveis.
 *
 * @param modifier [Modifier] aplicado ao [Card] raiz.
 */
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

/**
 * Card que envolve o [GnssSkyPlot], exibindo a posição dos satélites GNSS em relação à
 * orientação atual do dispositivo.
 *
 * @param modifier [Modifier] aplicado tanto ao [Card] quanto ao [GnssSkyPlot] interno.
 * @param gnssState Estado atual do GNSS, com a lista de satélites a serem plotados.
 * @param orientationState Orientação atual do dispositivo, usada para alinhar o plot.
 */
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

/**
 * Adiciona à [LazyListScope] o cabeçalho da tabela de satélites ([SatelliteHeader]) seguido de
 * uma linha ([SatelliteRow]) para cada satélite em [gnssInfo].
 *
 * @param gnssInfo Estado atual do GNSS, com a lista de satélites a serem listados.
 * @param locale [Locale] usado para formatar os valores numéricos de cada satélite.
 */
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

/**
 * Cabeçalho da tabela de satélites: exibe o título da seção e os rótulos de cada coluna
 * (ID, constelação, sinal, elevação e azimute).
 */
@Composable
private fun SatelliteHeader() {
    Column(modifier = Modifier.padding(PaddingSingle)) {
        Text(
            text = stringResource(R.string.presentation_skyplot_satellites).uppercase(),
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
                text = stringResource(R.string.presentation_skyplot_satellite_id),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(40.dp),
            )
            Text(
                text = "CONST.",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.presentation_skyplot_satellite_signal),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(60.dp),
                textAlign = TextAlign.End,
            )
            Text(
                text = stringResource(R.string.presentation_skyplot_satellite_elevation),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.End,
            )
            Text(
                text = stringResource(R.string.presentation_skyplot_satellite_azimuth),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.End,
            )
        }

        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}

/**
 * Linha da tabela de satélites com os dados de um único [GnssSatellite]: ID, constelação
 * (com indicador de cor), força de sinal (CN0), elevação e azimute. O ID é exibido em negrito
 * quando o satélite está sendo usado no cálculo da posição (`usedInFix`).
 *
 * @param satellite Satélite cujos dados serão exibidos na linha.
 * @param locale [Locale] usado para formatar os valores numéricos.
 */
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

/**
 * Card com os detalhes da localização atual: coordenadas geográficas (latitude/longitude),
 * coordenadas UTM (zona, easting, northing), altitude e precisão. Também expõe um botão para
 * compartilhar esses dados como texto via [share].
 *
 * Não renderiza nada caso [locationState] seja `null`.
 *
 * @param modifier [Modifier] aplicado ao [Card] raiz.
 * @param locationState Localização atual a ser exibida, ou `null` para não renderizar o card.
 */
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
                    text = stringResource(R.string.presentation_skyplot_location_title).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                val shareTitle = stringResource(R.string.presentation_skyplot_location_title)
                val latLabel = stringResource(R.string.presentation_skyplot_latitude)
                val lonLabel = stringResource(R.string.presentation_skyplot_longitude)
                val zoneLabel = stringResource(R.string.presentation_skyplot_zone)
                val eastingLabel = stringResource(R.string.presentation_skyplot_easting)
                val northingLabel = stringResource(R.string.presentation_skyplot_northing)
                val altitudeLabel = stringResource(R.string.presentation_skyplot_altitude)
                val accuracyLabel = stringResource(R.string.presentation_skyplot_accuracy)

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
                        imageVector = Asset.Share,
                        contentDescription = stringResource(R.string.presentation_skyplot_share),
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
                    label = stringResource(R.string.presentation_skyplot_latitude),
                    value = String.format(locale, "%.6f", locationState.latitude),
                    modifier = Modifier.weight(1f),
                )
                LocationInfoItem(
                    label = stringResource(R.string.presentation_skyplot_longitude),
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
                    label = stringResource(R.string.presentation_skyplot_zone),
                    value = locationState.utm.zone,
                    modifier = Modifier.weight(0.5f),
                )
                LocationInfoItem(
                    label = stringResource(R.string.presentation_skyplot_easting),
                    value = locationState.utm.easting,
                    modifier = Modifier.weight(1f),
                )
                LocationInfoItem(
                    label = stringResource(R.string.presentation_skyplot_northing),
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
                    label = stringResource(R.string.presentation_skyplot_altitude),
                    value = stringResource(
                        R.string.presentation_skyplot_meters,
                        locationState.altitude ?: 0.0,
                    ),
                    modifier = Modifier.weight(1f),
                )
                LocationInfoItem(
                    label = stringResource(R.string.presentation_skyplot_accuracy),
                    value = stringResource(
                        R.string.presentation_skyplot_meters,
                        locationState.accuracy ?: 0.0,
                    ),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/**
 * Par rótulo/valor usado dentro do card [Location] para exibir um único dado da localização
 * (por exemplo, latitude, zona UTM ou altitude).
 *
 * @param label Rótulo do campo, exibido em caixa alta acima do valor.
 * @param value Valor já formatado a ser exibido.
 * @param modifier [Modifier] aplicado à [Column] raiz.
 */
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
