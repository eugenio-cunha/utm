package br.com.b256.core.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import br.com.b256.core.designsystem.theme.B256Theme
import br.com.b256.core.designsystem.theme.BackgroundTheme
import br.com.b256.core.designsystem.theme.DarkDefaultColorScheme
import br.com.b256.core.designsystem.theme.GradientColors
import br.com.b256.core.designsystem.theme.LightDefaultColorScheme
import br.com.b256.core.designsystem.theme.LocalBackgroundTheme
import br.com.b256.core.designsystem.theme.LocalGradientColors
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ThemeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dark_theme_false() {
        composeTestRule.setContent {
            B256Theme(
                darkTheme = false,
            ) {
                val colorScheme = LightDefaultColorScheme

                val gradientColors = defaultGradientColors(colorScheme)
                assertEquals(gradientColors, LocalGradientColors.current)

                val backgroundTheme = defaultBackgroundTheme(colorScheme)
                assertEquals(backgroundTheme, LocalBackgroundTheme.current)
            }
        }
    }

    @Test
    fun dark_theme_true() {
        composeTestRule.setContent {
            B256Theme(
                darkTheme = true,
            ) {
                val colorScheme = DarkDefaultColorScheme

                val gradientColors = defaultGradientColors(colorScheme)
                assertEquals(gradientColors, LocalGradientColors.current)

                val backgroundTheme = defaultBackgroundTheme(colorScheme)
                assertEquals(backgroundTheme, LocalBackgroundTheme.current)
            }
        }
    }

    private fun defaultGradientColors(colorScheme: ColorScheme): GradientColors = GradientColors(
        top = colorScheme.inverseOnSurface,
        bottom = colorScheme.primaryContainer,
        container = colorScheme.surface,
    )

    private fun defaultBackgroundTheme(colorScheme: ColorScheme): BackgroundTheme = BackgroundTheme(
        color = colorScheme.surface,
        tonalElevation = 2.dp,
    )
}
