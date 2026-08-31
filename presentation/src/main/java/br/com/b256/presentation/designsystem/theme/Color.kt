package br.com.b256.presentation.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * B256 colors.
 *
 * Paleta inspirada em um traje espacial branco, laranja e cinza, construída
 * a partir de 3 cores base: laranja (#DD912E), cinza (#989DA4) e branco
 * (#FFFFFF). As demais variações (containers, superfícies, tons claro/escuro)
 * são tintas/sombras derivadas dessas mesmas 3 cores.
 */
internal val lightPrimary = Color(0xFFDD912E)
internal val lightOnPrimary = Color(0xFF3A2400)
internal val lightPrimaryContainer = Color(0xFFF8E9D5)
internal val lightOnPrimaryContainer = Color(0xFF211607)
internal val lightSecondary = Color(0xFF989DA4)
internal val lightOnSecondary = Color(0xFF212328)
internal val lightSecondaryContainer = Color(0xFFF0F0F1)
internal val lightOnSecondaryContainer = Color(0xFF171819)
internal val lightTertiary = Color(0xFF6E7379)
internal val lightOnTertiary = Color(0xFFFFFFFF)
internal val lightTertiaryContainer = Color(0xFFE9EAEB)
internal val lightOnTertiaryContainer = Color(0xFF1E2124)
internal val lightError = Color(0xFFBA1A1A)
internal val lightErrorContainer = Color(0xFFFFDAD6)
internal val lightOnError = Color(0xFFFFFFFF)
internal val lightOnErrorContainer = Color(0xFF410002)
internal val lightBackground = Color(0xFFFFFFFF)
internal val lightOnBackground = Color(0xFF1B1C1E)
internal val lightSurface = Color(0xFFFFFFFF)
internal val lightOnSurface = Color(0xFF1B1C1E)
internal val lightSurfaceVariant = Color(0xFFE5E7E8)
internal val lightOnSurfaceVariant = Color(0xFF3D3F42)
internal val lightOutline = Color(0xFF7D8288)
internal val lightInverseOnSurface = Color(0xFFF2F0F2)
internal val lightInverseSurface = Color(0xFF303032)
internal val lightInversePrimary = Color(0xFFF1D3AB)
internal val lightShadow = Color(0xFF000000)
internal val lightSurfaceTint = Color(0xFFDD912E)
internal val lightOutlineVariant = Color(0xFF989DA4)
internal val lightScrim = Color(0xFF000000)

internal val darkPrimary = Color(0xFFECC38C)
internal val darkOnPrimary = Color(0xFF422C0E)
internal val darkPrimaryContainer = Color(0xFF905E1E)
internal val darkOnPrimaryContainer = Color(0xFFFFEDD9)
internal val darkSecondary = Color(0xFFD1D3D6)
internal val darkOnSecondary = Color(0xFF2B2D30)
internal val darkSecondaryContainer = Color(0xFF5B5E62)
internal val darkOnSecondaryContainer = Color(0xFFE0E2E4)
internal val darkTertiary = Color(0xFFBEC0C3)
internal val darkOnTertiary = Color(0xFF1C1D1E)
internal val darkTertiaryContainer = Color(0xFF484B4F)
internal val darkOnTertiaryContainer = Color(0xFFDBDCDE)
internal val darkError = Color(0xFFFFB4AB)
internal val darkErrorContainer = Color(0xFF93000A)
internal val darkOnError = Color(0xFF690005)
internal val darkOnErrorContainer = Color(0xFFFFDAD6)
internal val darkBackground = Color(0xFF1B1C1E)
internal val darkOnBackground = Color(0xFFE3E4E6)
internal val darkSurface = Color(0xFF1B1C1E)
internal val darkOnSurface = Color(0xFFE3E4E6)
internal val darkSurfaceVariant = Color(0xFF46474A)
internal val darkOnSurfaceVariant = Color(0xFFC5C7CA)
internal val darkOutline = Color(0xFFB2B6BB)
internal val darkInverseOnSurface = Color(0xFF1B1C1E)
internal val darkInverseSurface = Color(0xFFE3E4E6)
internal val darkInversePrimary = Color(0xFFDD912E)
internal val darkShadow = Color(0xFF000000)
internal val darkSurfaceTint = Color(0xFFECC38C)
internal val darkOutlineVariant = Color(0xFF94969B)
internal val darkScrim = Color(0xFF000000)

/**
 * Cores fixas (independentes do tema claro/escuro) do "instrumento" de sky plot
 * ([br.com.b256.presentation.skyplot.components.GnssSkyPlot]): o radar deve sempre se parecer
 * com uma tela de céu noturno, como um instrumento físico, e não com uma superfície do Material
 * que se inverte junto com o tema. Usar `inverseSurface`/`inverseOnSurface` para isso fazia o
 * radar virar um círculo quase branco no tema escuro, prejudicando a legibilidade dos ícones de
 * satélite (cores saturadas como amarelo/ciano perdem contraste sobre fundo claro).
 */
internal val SkyPlotSurface = Color(0xFF102A54)
internal val SkyPlotOnSurface = Color(0xFFF2F0F2)
