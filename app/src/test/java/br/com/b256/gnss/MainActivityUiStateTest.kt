package br.com.b256.gnss

import br.com.b256.domain.entities.enums.Theme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MainActivityUiStateTest {
    @Test
    fun `Loading mantem a splash screen visivel`() {
        assertTrue(MainActivityUiState.Loading.shouldKeepSplashScreen())
    }

    @Test
    fun `Success esconde a splash screen`() {
        assertFalse(MainActivityUiState.Success(Theme.DARK).shouldKeepSplashScreen())
    }

    @Test
    fun `tema LIGHT nunca usa modo escuro`() {
        val state = MainActivityUiState.Success(Theme.LIGHT)

        assertFalse(state.shouldUseDarkTheme(isSystemDarkTheme = true))
        assertFalse(state.shouldUseDarkTheme(isSystemDarkTheme = false))
    }

    @Test
    fun `tema DARK sempre usa modo escuro`() {
        val state = MainActivityUiState.Success(Theme.DARK)

        assertTrue(state.shouldUseDarkTheme(isSystemDarkTheme = true))
        assertTrue(state.shouldUseDarkTheme(isSystemDarkTheme = false))
    }

    @Test
    fun `tema FOLLOW_SYSTEM segue o tema do sistema`() {
        val state = MainActivityUiState.Success(Theme.FOLLOW_SYSTEM)

        assertTrue(state.shouldUseDarkTheme(isSystemDarkTheme = true))
        assertFalse(state.shouldUseDarkTheme(isSystemDarkTheme = false))
    }
}
