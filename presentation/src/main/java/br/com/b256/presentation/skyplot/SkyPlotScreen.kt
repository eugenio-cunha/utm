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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.b256.domain.entities.GnssInfo
import br.com.b256.domain.entities.GnssSatellite
import br.com.b256.domain.entities.GpsLocation
import br.com.b256.domain.entities.Orientation
import br.com.b256.presentation.R
import br.com.b256.presentation.designsystem.asset.Asset
import br.com.b256.presentation.designsystem.theme.BorderHalf
import br.com.b256.presentation.designsystem.theme.IconDouble
import br.com.b256.presentation.designsystem.theme.IconSingle
import br.com.b256.presentation.designsystem.theme.IconTreble
import br.com.b256.presentation.designsystem.theme.PaddingDouble
import br.com.b256.presentation.designsystem.theme.PaddingHalf
import br.com.b256.presentation.designsystem.theme.PaddingSingle
import br.com.b256.presentation.designsystem.theme.PaddingTreble
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
 * O layout é inspirado em painéis de controle de missão: um cabeçalho compacto ([MissionHeader])
 * com o título e o acesso às configurações, uma barra de status de telemetria ([MissionStatusBar])
 * e painéis técnicos ([MissionPanel]) para posição, sky plot e satélites, cada um com bordas
 * finas, marcas de canto e tipografia monoespaçada.
 *
 * A tela não declara um `Scaffold` próprio — o `Scaffold` da aplicação vive em
 * [B256App]. Diferente das demais features, esta não usa a topbar padrão do design system
 * (`B256TopAppBar`): o cabeçalho é o próprio [MissionHeader], para manter a estética de console.
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
        MissionHeader(onSettingsClick = { showSettingsDialog = true })

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = PaddingDouble),
            contentPadding = PaddingValues(vertical = PaddingDouble),
            verticalArrangement = Arrangement.spacedBy(PaddingTreble),
        ) {
            item(key = "status") {
                MissionStatusBar(gnssState = gnssState)
            }

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
                item(key = "satellites") {
                    SatellitePanel(
                        gnssInfo = it,
                        locale = locale,
                    )
                }
            }
        }
    }
}

/**
 * Cabeçalho compacto da tela, no lugar da topbar padrão do design system ([B256TopAppBar]):
 * exibe o título em caixa alta e fonte monoespaçada à esquerda e o botão de acesso às
 * configurações ([SettingsDialog]) à direita, mantendo a estética de console de controle de
 * missão do restante da tela.
 *
 * @param onSettingsClick Callback disparado ao tocar no botão de configurações.
 * @param modifier [Modifier] aplicado à [Row] raiz.
 */
@Composable
private fun MissionHeader(onSettingsClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingDouble, vertical = PaddingSingle),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.presentation_skyplot_title).uppercase(),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier.size(IconTreble),
        ) {
            Icon(
                imageVector = Asset.Settings,
                contentDescription = stringResource(
                    R.string.presentation_skyplot_top_app_bar_action_settings,
                ),
                modifier = Modifier.size(IconDouble),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

/**
 * Faixa de telemetria exibida no topo da lista, resumindo o status geral do GNSS: um indicador
 * pulsante (cinza sem dados, laranja adquirindo sinal, verde com fix) seguido do rótulo textual
 * correspondente, e a contagem de satélites usados no fix em relação ao total visível.
 *
 * @param gnssState Estado atual do GNSS, ou `null` enquanto nenhum dado foi recebido ainda.
 * @param modifier [Modifier] aplicado à [Row] raiz.
 */
@Composable
private fun MissionStatusBar(gnssState: GnssInfo?, modifier: Modifier = Modifier) {
    val hasFix = (gnssState?.satellitesUsedInFix ?: 0) > 0

    val statusColor = when {
        gnssState == null -> MaterialTheme.colorScheme.outline
        hasFix -> Color(0xFF4CAF50)
        else -> MaterialTheme.colorScheme.primary
    }
    val statusTextRes = when {
        gnssState == null -> R.string.presentation_skyplot_status_no_signal
        hasFix -> R.string.presentation_skyplot_status_fix_acquired
        else -> R.string.presentation_skyplot_status_acquiring
    }
    val pulseAlpha = shimmerAlpha()

    MissionPanelSurface {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = pulseAlpha)),
                )
                Spacer(modifier = Modifier.width(PaddingSingle))
                Text(
                    text = stringResource(statusTextRes).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Text(
                text = if (gnssState != null) {
                    stringResource(
                        R.string.presentation_skyplot_satellites_in_fix,
                        gnssState.satellitesUsedInFix,
                        gnssState.satellitesVisible,
                    )
                } else {
                    "--"
                },
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

//    Row(
//        modifier = modifier
//            .fillMaxWidth()
//            .clip(MaterialTheme.shapes.large)
//            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
//            .border(
//                width = BorderHalf,
//                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
//                shape = MaterialTheme.shapes.large,
//            )
//            .padding(horizontal = PaddingDouble, vertical = PaddingSingle),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Box(
//                modifier = Modifier
//                    .size(8.dp)
//                    .clip(CircleShape)
//                    .background(statusColor.copy(alpha = pulseAlpha)),
//            )
//            Spacer(modifier = Modifier.width(PaddingSingle))
//            Text(
//                text = stringResource(statusTextRes).uppercase(),
//                style = MaterialTheme.typography.labelMedium,
//                fontFamily = FontFamily.Monospace,
//                fontWeight = FontWeight.Bold,
//                letterSpacing = 1.sp,
//                color = MaterialTheme.colorScheme.onSurface,
//            )
//        }
//
//        Text(
//            text = if (gnssState != null) {
//                stringResource(
//                    R.string.presentation_skyplot_satellites_in_fix,
//                    gnssState.satellitesUsedInFix,
//                    gnssState.satellitesVisible,
//                )
//            } else {
//                "--"
//            },
//            style = MaterialTheme.typography.labelMedium,
//            fontFamily = FontFamily.Monospace,
//            color = MaterialTheme.colorScheme.onSurfaceVariant,
//        )
//    }
}

/**
 * Valor de opacidade que oscila continuamente entre 0.3 e 0.7, usado tanto para o efeito
 * "shimmer" dos skeletons quanto para o pulsar do indicador em [MissionStatusBar].
 */
@Composable
private fun shimmerAlpha(): Float {
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
    return alpha
}

/**
 * Placeholder animado (efeito "shimmer") exibido no lugar do painel [Location] enquanto a
 * localização ainda não foi obtida. O título do painel já é exibido normalmente; apenas os
 * valores, ainda desconhecidos, pulsam como blocos cinza.
 *
 * @param modifier [Modifier] aplicado ao [MissionPanel] raiz.
 */
@Composable
private fun LocationSkeleton(modifier: Modifier = Modifier) {
    val alpha = shimmerAlpha()

    MissionPanel(
        title = stringResource(R.string.presentation_skyplot_location_title),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PaddingDouble),
        ) {
            SkeletonItem(Modifier.weight(1f), alpha)
            SkeletonItem(Modifier.weight(1f), alpha)
        }

        MissionDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PaddingDouble),
        ) {
            SkeletonItem(Modifier.weight(0.5f), alpha)
            SkeletonItem(Modifier.weight(1f), alpha)
            SkeletonItem(Modifier.weight(1f), alpha)
        }

        MissionDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PaddingDouble),
        ) {
            SkeletonItem(Modifier.weight(1f), alpha)
            SkeletonItem(Modifier.weight(1f), alpha)
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
 * Placeholder animado (efeito "shimmer") exibido no lugar do painel [SkyPlot] enquanto os dados
 * de GNSS e orientação ainda não estão disponíveis: círculos concêntricos pulsantes no lugar da
 * grade de satélites.
 *
 * @param modifier [Modifier] aplicado ao [MissionPanel] raiz.
 */
@Composable
private fun SkyPlotSkeleton(modifier: Modifier = Modifier) {
    val alpha = shimmerAlpha()

    MissionPanel(
        title = stringResource(R.string.presentation_skyplot_panel_skyview),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
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
 * Painel técnico reutilizado por todas as seções da tela, no estilo de um instrumento de painel
 * de controle de missão: um cabeçalho com um marcador quadrado, o [title] em caixa alta e fonte
 * monoespaçada, e um espaço opcional para conteúdo à direita ([trailing]); abaixo, uma superfície
 * com borda fina e marcas de canto ([MissionPanelSurface]) envolvendo o [content].
 *
 * @param title Título do painel, exibido em caixa alta no cabeçalho.
 * @param modifier [Modifier] aplicado à [Column] raiz.
 * @param trailing Conteúdo opcional exibido à direita do cabeçalho (ex.: contadores, ações).
 * @param content Conteúdo do painel, disposto verticalmente dentro de [MissionPanelSurface].
 */
@Composable
private fun MissionPanel(
    title: String,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingHalf, vertical = PaddingHalf),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(MaterialTheme.colorScheme.primary),
                )
                Spacer(modifier = Modifier.width(PaddingSingle))
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            trailing()
        }

        Spacer(modifier = Modifier.height(PaddingSingle))

        MissionPanelSurface(content = content)
    }
}

/**
 * Superfície com borda fina, cantos arredondados e marcas de canto em "L" (ver
 * [drawMissionCorners]) que dá aos painéis da tela a aparência de um instrumento de HUD/console
 * de controle de missão. Usada internamente por [MissionPanel].
 *
 * @param modifier [Modifier] aplicado ao [Box] raiz.
 * @param content Conteúdo disposto verticalmente dentro da superfície.
 */
@Composable
private fun MissionPanelSurface(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val cornerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(PaddingDouble),
            verticalArrangement = Arrangement.spacedBy(PaddingDouble),
            content = content,
        )

        Canvas(modifier = Modifier.matchParentSize()) {
            drawMissionCorners(color = cornerColor)
        }
    }
}

/**
 * Desenha marcas de canto em "L" nos quatro cantos da área disponível, evocando as marcas de
 * mira de um HUD ou de um instrumento de painel de controle de missão.
 *
 * @param color Cor das marcas de canto.
 * @param length Comprimento de cada segmento da marca.
 * @param inset Distância entre a marca e a borda da área disponível.
 * @param strokeWidth Espessura do traço.
 */
private fun DrawScope.drawMissionCorners(
    color: Color,
    length: Dp = 12.dp,
    inset: Dp = 1.dp,
    strokeWidth: Dp = 2.dp,
) {
    val lengthPx = length.toPx()
    val insetPx = inset.toPx()
    val strokePx = strokeWidth.toPx()
    val right = size.width - insetPx
    val bottom = size.height - insetPx

    // Superior esquerdo
    drawLine(color, Offset(insetPx, insetPx), Offset(insetPx + lengthPx, insetPx), strokePx, StrokeCap.Round)
    drawLine(color, Offset(insetPx, insetPx), Offset(insetPx, insetPx + lengthPx), strokePx, StrokeCap.Round)
    // Superior direito
    drawLine(color, Offset(right, insetPx), Offset(right - lengthPx, insetPx), strokePx, StrokeCap.Round)
    drawLine(color, Offset(right, insetPx), Offset(right, insetPx + lengthPx), strokePx, StrokeCap.Round)
    // Inferior esquerdo
    drawLine(color, Offset(insetPx, bottom), Offset(insetPx + lengthPx, bottom), strokePx, StrokeCap.Round)
    drawLine(color, Offset(insetPx, bottom), Offset(insetPx, bottom - lengthPx), strokePx, StrokeCap.Round)
    // Inferior direito
    drawLine(color, Offset(right, bottom), Offset(right - lengthPx, bottom), strokePx, StrokeCap.Round)
    drawLine(color, Offset(right, bottom), Offset(right, bottom - lengthPx), strokePx, StrokeCap.Round)
}

/**
 * Linha divisória fina usada dentro dos painéis técnicos ([MissionPanel]) para separar seções
 * de dados, mantendo o mesmo tom discreto da borda dos painéis.
 */
@Composable
private fun MissionDivider() {
    HorizontalDivider(
        thickness = BorderHalf,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
    )
}

/**
 * Painel com o [GnssSkyPlot], exibindo a posição dos satélites GNSS em relação à orientação
 * atual do dispositivo, com a contagem de satélites usados no fix ao lado do título.
 *
 * @param modifier [Modifier] aplicado ao [MissionPanel] raiz.
 * @param gnssState Estado atual do GNSS, com a lista de satélites a serem plotados.
 * @param orientationState Orientação atual do dispositivo, usada para alinhar o plot.
 */
@Composable
private fun SkyPlot(
    modifier: Modifier = Modifier,
    gnssState: GnssInfo,
    orientationState: Orientation,
) {
    MissionPanel(
        title = stringResource(R.string.presentation_skyplot_panel_skyview),
        modifier = modifier,
        trailing = {
            Text(
                text = stringResource(
                    R.string.presentation_skyplot_satellites_in_fix,
                    gnssState.satellitesUsedInFix,
                    gnssState.satellitesVisible,
                ),
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
    ) {
        GnssSkyPlot(
            gnssInfo = gnssState,
            orientation = orientationState,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * Painel com a tabela de satélites: um cabeçalho de colunas ([SatelliteTableHeader]) seguido de
 * uma linha ([SatelliteRow]) para cada satélite em [gnssInfo], separadas por divisórias finas.
 *
 * @param gnssInfo Estado atual do GNSS, com a lista de satélites a serem listados.
 * @param locale [Locale] usado para formatar os valores numéricos de cada satélite.
 * @param modifier [Modifier] aplicado ao [MissionPanel] raiz.
 */
@Composable
private fun SatellitePanel(
    gnssInfo: GnssInfo,
    locale: Locale,
    modifier: Modifier = Modifier,
) {
    MissionPanel(
        title = stringResource(R.string.presentation_skyplot_satellites),
        modifier = modifier,
        trailing = {
            Text(
                text = stringResource(
                    R.string.presentation_skyplot_satellites_in_fix,
                    gnssInfo.satellitesUsedInFix,
                    gnssInfo.satellitesVisible,
                ),
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
    ) {
        SatelliteTableHeader()

        gnssInfo.satellites.forEachIndexed { index, satellite ->
            SatelliteRow(
                satellite = satellite,
                locale = locale,
            )
            if (index != gnssInfo.satellites.lastIndex) {
                MissionDivider()
            }
        }
    }
}

/**
 * Cabeçalho da tabela de satélites: exibe os rótulos de cada coluna (ID, constelação, sinal,
 * elevação e azimute), seguidos de uma divisória.
 */
@Composable
private fun SatelliteTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = PaddingHalf),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.presentation_skyplot_satellite_id),
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(40.dp),
        )
        Text(
            text = stringResource(R.string.presentation_skyplot_satellite_constellation),
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(R.string.presentation_skyplot_satellite_signal),
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(60.dp),
            textAlign = TextAlign.End,
        )
        Text(
            text = stringResource(R.string.presentation_skyplot_satellite_elevation),
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(50.dp),
            textAlign = TextAlign.End,
        )
        Text(
            text = stringResource(R.string.presentation_skyplot_satellite_azimuth),
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(50.dp),
            textAlign = TextAlign.End,
        )
    }

    MissionDivider()
}

/**
 * Linha da tabela de satélites com os dados de um único [GnssSatellite]: ID, constelação
 * (com indicador de cor), força de sinal (CN0), elevação e azimute, todos em fonte monoespaçada.
 * O ID é exibido em negrito e na cor de destaque quando o satélite está sendo usado no cálculo
 * da posição (`usedInFix`).
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
            .padding(vertical = PaddingSingle),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = satellite.svid.toString(),
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (satellite.usedInFix) FontWeight.Bold else FontWeight.Normal,
            color = if (satellite.usedInFix) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
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
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            text = String.format(locale, "%.1f", satellite.cn0DbHz),
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(60.dp),
            textAlign = TextAlign.End,
        )
        Text(
            text = String.format(locale, "%.0f°", satellite.elevationDegrees),
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(50.dp),
            textAlign = TextAlign.End,
        )
        Text(
            text = String.format(locale, "%.0f°", satellite.azimuthDegrees),
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(50.dp),
            textAlign = TextAlign.End,
        )
    }
}

/**
 * Painel com os detalhes da localização atual: coordenadas geográficas (latitude/longitude),
 * coordenadas UTM (zona, easting, northing), altitude e precisão. Também expõe um botão para
 * compartilhar esses dados como texto via [share].
 *
 * Não renderiza nada caso [locationState] seja `null`.
 *
 * @param modifier [Modifier] aplicado ao [MissionPanel] raiz.
 * @param locationState Localização atual a ser exibida, ou `null` para não renderizar o painel.
 */
@Composable
private fun Location(
    modifier: Modifier = Modifier,
    locationState: GpsLocation?,
) {
    if (locationState == null) return

    val locale = LocalConfiguration.current.locales[0]
    val context = LocalContext.current

    val shareTitle = stringResource(R.string.presentation_skyplot_location_title)
    val latLabel = stringResource(R.string.presentation_skyplot_latitude)
    val lonLabel = stringResource(R.string.presentation_skyplot_longitude)
    val zoneLabel = stringResource(R.string.presentation_skyplot_zone)
    val eastingLabel = stringResource(R.string.presentation_skyplot_easting)
    val northingLabel = stringResource(R.string.presentation_skyplot_northing)
    val altitudeLabel = stringResource(R.string.presentation_skyplot_altitude)
    val accuracyLabel = stringResource(R.string.presentation_skyplot_accuracy)
    val shareContentDescription = stringResource(R.string.presentation_skyplot_share)

    MissionPanel(
        title = shareTitle,
        modifier = modifier,
        trailing = {
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
                modifier = Modifier.size(IconDouble),
            ) {
                Icon(
                    imageVector = Asset.Share,
                    contentDescription = shareContentDescription,
                    modifier = Modifier.size(IconSingle),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
    ) {
        // Geographic Coordinates
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PaddingDouble),
        ) {
            LocationInfoItem(
                label = latLabel,
                value = String.format(locale, "%.6f", locationState.latitude),
                modifier = Modifier.weight(1f),
            )
            LocationInfoItem(
                label = lonLabel,
                value = String.format(locale, "%.6f", locationState.longitude),
                modifier = Modifier.weight(1f),
            )
        }

        MissionDivider()

        // UTM Coordinates
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PaddingDouble),
        ) {
            LocationInfoItem(
                label = zoneLabel,
                value = locationState.utm.zone,
                modifier = Modifier.weight(0.5f),
            )
            LocationInfoItem(
                label = eastingLabel,
                value = locationState.utm.easting,
                modifier = Modifier.weight(1f),
            )
            LocationInfoItem(
                label = northingLabel,
                value = locationState.utm.northing,
                modifier = Modifier.weight(1f),
            )
        }

        MissionDivider()

        // Altitude and Accuracy
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PaddingDouble),
        ) {
            LocationInfoItem(
                label = altitudeLabel,
                value = stringResource(
                    R.string.presentation_skyplot_meters,
                    locationState.altitude ?: 0.0,
                ),
                modifier = Modifier.weight(1f),
            )
            LocationInfoItem(
                label = accuracyLabel,
                value = stringResource(
                    R.string.presentation_skyplot_meters,
                    locationState.accuracy ?: 0.0,
                ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * Par rótulo/valor usado dentro do painel [Location] para exibir um único dado da localização
 * (por exemplo, latitude, zona UTM ou altitude). O valor é exibido em fonte monoespaçada,
 * reforçando a leitura como um dado de telemetria.
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
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface,
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
