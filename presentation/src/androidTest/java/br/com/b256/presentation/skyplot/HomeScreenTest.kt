package br.com.b256.presentation.skyplot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Teste de referência de UI em Compose: exercita a versão "stateless" de [SkyPlotScreen]
 * (ver `HomeScreen.kt`), sem precisar de Hilt/ViewModel, e verifica o comportamento via semantics
 * (`onNodeWithText`) em vez de referências diretas a componentes internos.
 */
class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun exibeOTextoDaTela() {
        composeTestRule.setContent {
            SkyPlotScreen(onClick = {})
        }

        composeTestRule.onNodeWithText("Home Screen").assertIsDisplayed()
    }

    @Test
    fun clicarNoBotaoInvocaOCallbackOnClick() {
        var clicked = false

        composeTestRule.setContent {
            SkyPlotScreen(onClick = { clicked = true })
        }

        composeTestRule.onNodeWithText("Click").performClick()

        assertTrue(clicked)
    }
}
