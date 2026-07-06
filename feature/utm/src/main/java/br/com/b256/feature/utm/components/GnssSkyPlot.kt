package br.com.b256.feature.utm.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.b256.core.model.GnssInfo
import br.com.b256.core.model.GnssSatellite
import br.com.b256.core.model.Orientation
import kotlin.math.cos
import kotlin.math.sin

/**
 * Componente Visual que exibe um Sky Plot dos satélites GNSS (Global Navigation Satellite System).
 * Representa a posição (azimute e elevação) dos satélites no céu e sua visibilidade.
 */
@Composable
fun GnssSkyPlot(
    gnssInfo: GnssInfo,
    orientation: Orientation,
    modifier: Modifier = Modifier
) {
    SkyPlotCanvas(
        gnssInfo = gnssInfo,
        azimuth = orientation.azimuth,
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
    )
}

/**
 * Componente interno que realiza o desenho técnico do Sky Plot utilizando a API Canvas do Jetpack Compose.
 *
 * Este componente gerencia a animação da rotação do mapa com base no azimute do dispositivo,
 * renderiza a grade circular de coordenadas (azimute e elevação) e posiciona os ícones
 * dos satélites em suas coordenadas celestes correspondentes.
 *
 * @param gnssInfo Objeto contendo a lista de satélites e seus dados de telemetria.
 * @param azimuth O azimute atual do dispositivo para orientar o mapa (Norte verdadeiro).
 * @param modifier Modificador de layout para customização de tamanho e comportamento.
 */
@Composable
private fun SkyPlotCanvas(
    gnssInfo: GnssInfo,
    azimuth: Float,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val outlineColor = Color.White.copy(alpha = 0.25f)
    val satelliteIconPainter = rememberVectorPainter(Icons.Default.SatelliteAlt)

    // Lógica para suavizar a rotação e lidar com o wrap-around (0/360 graus)
    var smoothedAzimuth by remember { mutableStateOf(azimuth) }
    LaunchedEffect(azimuth) {
        val delta = (azimuth - (smoothedAzimuth % 360) + 540) % 360 - 180
        smoothedAzimuth += delta
    }

    val animatedAzimuth by animateFloatAsState(
        targetValue = smoothedAzimuth,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioLowBouncy
        ),
        label = "azimuthAnimation"
    )

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color = Color(0xFF102A54))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - 20.dp.toPx()

            rotate(-animatedAzimuth, pivot = center) {
                drawSkyPlotGrid(center, radius, outlineColor, textMeasurer, animatedAzimuth)

                // Desenhar Satélites
                gnssInfo.satellites.forEach { satellite ->
                    drawSatellite(
                        satellite = satellite,
                        center = center,
                        radius = radius,
                        animatedAzimuth = animatedAzimuth,
                        iconPainter = satelliteIconPainter,
                        textMeasurer = textMeasurer
                    )
                }
            }
        }
    }
}

/**
 * Desenha a grade de referência do Sky Plot, incluindo círculos de elevação,
 * linhas de azimute e rótulos de pontos cardeais.
 *
 * @param center O ponto central (x, y) onde o gráfico será desenhado.
 * @param radius O raio máximo do círculo externo (representando 0° de elevação).
 * @param outlineColor A cor utilizada para as linhas e círculos da grade.
 * @param textMeasurer Utilitário para medir e desenhar os rótulos de texto.
 * @param animatedAzimuth O valor atual do azimute animado, utilizado para rotacionar
 * os textos individualmente para que permaneçam legíveis (verticalmente alinhados)
 * enquanto o mapa gira.
 */
private fun DrawScope.drawSkyPlotGrid(
    center: Offset,
    radius: Float,
    outlineColor: Color,
    textMeasurer: TextMeasurer,
    animatedAzimuth: Float
) {
    // Desenhar círculos de elevação (30°, 60°)
    drawCircle(
        color = outlineColor,
        radius = radius,
        style = Stroke(
            width = 1.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(0f, 3.dp.toPx()),
                phase = 0f
            )
        )
    )
    drawCircle(
        color = outlineColor,
        radius = radius * 2 / 3,
        style = Stroke(
            width = 1.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(0f, 3.dp.toPx()),
                phase = 0f
            )
        )
    )
    drawCircle(
        color = outlineColor,
        radius = radius / 3,
        style = Stroke(
            width = 1.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(0f, 3.dp.toPx()),
                phase = 0f
            )
        ),
    )

    // Desenhar linhas de azimute a cada 30 graus
    val labelStyle = TextStyle(fontSize = 10.sp, color = Color.White)
    for (angle in 0 until 360 step 30) {
        val angleRad = Math.toRadians(angle.toDouble() - 90.0)
        val startX = center.x + (radius * 0.1f) * cos(angleRad).toFloat()
        val startY = center.y + (radius * 0.1f) * sin(angleRad).toFloat()
        val endX = center.x + radius * cos(angleRad).toFloat()
        val endY = center.y + radius * sin(angleRad).toFloat()

        drawLine(
            color = outlineColor.copy(alpha = 0.5f),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 1.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(0f, 3.dp.toPx()),
                phase = 0f
            )
        )

        // Pular os ângulos dos pontos cardeais principais para não sobrepor (0, 90, 180, 270)
        if (angle % 90 != 0) {
            val labelRadius = radius + 10.dp.toPx()
            val labelX = center.x + labelRadius * cos(angleRad).toFloat()
            val labelY = center.y + labelRadius * sin(angleRad).toFloat()

            rotate(animatedAzimuth, pivot = Offset(labelX, labelY)) {
                drawText(
                    textMeasurer,
                    "$angle°",
                    Offset(labelX - 10.dp.toPx(), labelY - 6.dp.toPx()),
                    style = labelStyle
                )
            }
        }
    }

    // Pontos Cardeais principais
    val cardinalStyle = TextStyle(
        fontSize = 12.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
    drawText(
        textMeasurer,
        "N",
        Offset(center.x - 5.dp.toPx(), center.y - radius - 15.dp.toPx()),
        style = cardinalStyle
    )
    drawText(
        textMeasurer,
        "S",
        Offset(center.x - 5.dp.toPx(), center.y + radius + 2.dp.toPx()),
        style = cardinalStyle
    )
    drawText(
        textMeasurer,
        "E",
        Offset(center.x + radius + 5.dp.toPx(), center.y - 8.dp.toPx()),
        style = cardinalStyle
    )
    drawText(
        textMeasurer,
        "W",
        Offset(center.x - radius - 15.dp.toPx(), center.y - 8.dp.toPx()),
        style = cardinalStyle
    )
}

/**
 * Desenha um único satélite no Canvas do Sky Plot.
 *
 * Calcula a posição do satélite com base em seu azimute e elevação, aplica cores
 * baseadas na constelação e no status de fixação, e renderiza o ícone com uma
 * orientação aleatória para evitar uniformidade visual. Também exibe o SVID (ID do satélite)
 * se o satélite estiver sendo usado na correção ou possuir um sinal forte.
 *
 * @param satellite Os dados do satélite contendo azimute, elevação, SVID e constelação.
 * @param center O ponto central do canvas onde o Sky Plot está sendo desenhado.
 * @param radius O raio total disponível para a representação do céu.
 * @param animatedAzimuth O valor atual da rotação do azimute para compensar a orientação do dispositivo.
 * @param iconPainter O [Painter] responsável por desenhar o ícone do satélite.
 * @param textMeasurer O medidor de texto utilizado para renderizar o ID do satélite.
 */
private fun DrawScope.drawSatellite(
    satellite: GnssSatellite,
    center: Offset,
    radius: Float,
    animatedAzimuth: Float,
    iconPainter: Painter,
    textMeasurer: TextMeasurer
) {
    val angleRad = Math.toRadians(satellite.azimuthDegrees.toDouble() - 90.0)
    val r = radius * (1f - satellite.elevationDegrees / 90f)

    val x = center.x + r * cos(angleRad).toFloat()
    val y = center.y + r * sin(angleRad).toFloat()

    val satelliteColor = if (!satellite.usedInFix) {
        Color.LightGray.copy(alpha = 0.5f)
    } else {
        Color(satellite.constellation.color)
    }

    val iconSize = (satellite.cn0DbHz / 10f).coerceIn(12f, 18f).dp.toPx()

    val random = kotlin.random.Random(satellite.svid.toLong())
    val scaleX = if (random.nextBoolean()) -1f else 1f
    val scaleY = if (random.nextBoolean()) -1f else 1f

    translate(left = x - iconSize / 2, top = y - iconSize / 2) {
        scale(scaleX, scaleY, pivot = Offset(iconSize / 2, iconSize / 2)) {
            rotate(animatedAzimuth, pivot = Offset(iconSize / 2, iconSize / 2)) {
                with(iconPainter) {
                    draw(
                        size = Size(iconSize, iconSize),
                        colorFilter = ColorFilter.tint(satelliteColor)
                    )
                }
            }
        }
    }

    if (satellite.usedInFix || satellite.cn0DbHz > 25f) {
        rotate(animatedAzimuth, pivot = Offset(x, y)) {
            drawText(
                textMeasurer,
                satellite.svid.toString(),
                Offset(x + iconSize / 2, y - iconSize / 2),
                style = TextStyle(fontSize = 8.sp, color = Color.White)
            )
        }
    }
}
