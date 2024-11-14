package br.com.b256.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Uma classe para modelar valores de cor de gradiente para B256.
 *
 * @param top A cor de gradiente superior a ser renderizada.
 * @param bottom A cor de gradiente inferior a ser renderizada.
 * @param container A cor de gradiente do contêiner sobre a qual o gradiente será renderizado.
 */
@Immutable
data class GradientColors(
    val top: Color = Color.Unspecified,
    val bottom: Color = Color.Unspecified,
    val container: Color = Color.Unspecified,
)

/**
 * Uma composição local para [GradientColors].
 */
val LocalGradientColors = staticCompositionLocalOf { GradientColors() }
