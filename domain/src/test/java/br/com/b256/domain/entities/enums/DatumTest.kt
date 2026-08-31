package br.com.b256.domain.entities.enums

import org.junit.Assert.assertEquals
import org.junit.Test

class DatumTest {
    @Test
    fun `from reconhece o nome do enum, case-insensitive`() {
        assertEquals(Datum.SAD69, Datum.from("SAD69"))
        assertEquals(Datum.SAD69, Datum.from("sad69"))
    }

    @Test
    fun `from reconhece o value armazenado, case-insensitive`() {
        assertEquals(Datum.CORREGO_ALEGRE, Datum.from("corrego_alegre"))
        assertEquals(Datum.CORREGO_ALEGRE, Datum.from("CORREGO_ALEGRE"))
    }

    @Test
    fun `from reconhece os datums da America do Norte`() {
        assertEquals(Datum.NAD83, Datum.from("nad83"))
        assertEquals(Datum.NAD27, Datum.from("NAD27"))
    }

    @Test
    fun `from reconhece os datums da Europa`() {
        assertEquals(Datum.ETRS89, Datum.from("etrs89"))
        assertEquals(Datum.ED50, Datum.from("ED50"))
    }

    @Test
    fun `from cai para WGS84 quando o valor e desconhecido ou vazio`() {
        assertEquals(Datum.WGS84, Datum.from("unknown"))
        assertEquals(Datum.WGS84, Datum.from(""))
    }
}
