package br.com.b256.domain.entities.enums

import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeTest {
    @Test
    fun `from reconhece o nome do enum, case-insensitive`() {
        assertEquals(Theme.DARK, Theme.from("DARK"))
        assertEquals(Theme.DARK, Theme.from("dark"))
    }

    @Test
    fun `from reconhece o value armazenado, case-insensitive`() {
        assertEquals(Theme.LIGHT, Theme.from("light"))
        assertEquals(Theme.LIGHT, Theme.from("LIGHT"))
    }

    @Test
    fun `from cai para FOLLOW_SYSTEM quando o valor e desconhecido ou vazio`() {
        assertEquals(Theme.FOLLOW_SYSTEM, Theme.from("unknown"))
        assertEquals(Theme.FOLLOW_SYSTEM, Theme.from(""))
    }
}
