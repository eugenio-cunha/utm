package br.com.b256.data.services.location.extension

import br.com.b256.domain.entities.enums.Datum
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/**
 * Teste de sanidade para [transformToDatum]. Como a transformação depende de parâmetros
 * geodésicos (EPSG:6195, EPSG:1132, EPSG:1173 e EPSG:1133) e não há, neste projeto, uma
 * referência externa para conferir um valor exato esperado ponto a ponto, o teste verifica
 * invariantes: identidade para datums sem translação, e magnitude do deslocamento dentro de uma
 * faixa plausível (nem zero, nem uma ordem de grandeza maior que a diferença real entre os
 * datums) para os demais — cada um usando um ponto de referência dentro da região onde seus
 * parâmetros são válidos.
 */
class DatumTest {
    // Praça dos Três Poderes, Brasília — dentro do Brasil, onde os parâmetros de
    // SAD69/Córrego Alegre usados aqui são válidos.
    private val brasiliaLat = -15.7801
    private val brasiliaLon = -47.9292

    // Washington, D.C. — dentro dos EUA continentais (CONUS), onde os parâmetros de NAD27/NAD83
    // usados aqui são válidos.
    private val washingtonLat = 38.8951
    private val washingtonLon = -77.0364

    // Paris — dentro da Europa ocidental, onde os parâmetros de ED50/ETRS89 usados aqui são
    // válidos.
    private val parisLat = 48.8566
    private val parisLon = 2.3522

    @Test
    fun `WGS84 nao altera a coordenada`() {
        assertIdentity(brasiliaLat, brasiliaLon, Datum.WGS84)
    }

    @Test
    fun `SIRGAS2000 nao altera a coordenada`() {
        assertIdentity(brasiliaLat, brasiliaLon, Datum.SIRGAS2000)
    }

    @Test
    fun `NAD83 nao altera a coordenada`() {
        assertIdentity(washingtonLat, washingtonLon, Datum.NAD83)
    }

    @Test
    fun `ETRS89 nao altera a coordenada`() {
        assertIdentity(parisLat, parisLon, Datum.ETRS89)
    }

    @Test
    fun `SAD69 desloca a coordenada numa magnitude plausivel (nem zero, nem absurda)`() {
        val (resultLat, resultLon) = transformToDatum(brasiliaLat, brasiliaLon, altitude = 0.0, datum = Datum.SAD69)

        assertShiftWithinPlausibleRange(brasiliaLat, brasiliaLon, resultLat, resultLon)
    }

    @Test
    fun `CORREGO_ALEGRE desloca a coordenada numa magnitude plausivel (nem zero, nem absurda)`() {
        val (resultLat, resultLon) =
            transformToDatum(brasiliaLat, brasiliaLon, altitude = 0.0, datum = Datum.CORREGO_ALEGRE)

        assertShiftWithinPlausibleRange(brasiliaLat, brasiliaLon, resultLat, resultLon)
    }

    @Test
    fun `NAD27 desloca a coordenada numa magnitude plausivel (nem zero, nem absurda)`() {
        val (resultLat, resultLon) = transformToDatum(washingtonLat, washingtonLon, altitude = 0.0, datum = Datum.NAD27)

        assertShiftWithinPlausibleRange(washingtonLat, washingtonLon, resultLat, resultLon)
    }

    @Test
    fun `ED50 desloca a coordenada numa magnitude plausivel (nem zero, nem absurda)`() {
        val (resultLat, resultLon) = transformToDatum(parisLat, parisLon, altitude = 0.0, datum = Datum.ED50)

        assertShiftWithinPlausibleRange(parisLat, parisLon, resultLat, resultLon)
    }

    private fun assertIdentity(lat: Double, lon: Double, datum: Datum) {
        val (resultLat, resultLon) = transformToDatum(lat, lon, altitude = 0.0, datum = datum)

        assertEquals(lat, resultLat, 0.0)
        assertEquals(lon, resultLon, 0.0)
    }

    /**
     * As diferenças reais entre WGS84 e os datums legados suportados ficam, tipicamente, na
     * casa de dezenas a poucas centenas de metros (~0.0001° a ~0.003° de latitude/longitude).
     * Os limites abaixo (~1m a ~1,1km) são deliberadamente folgados: o objetivo é pegar erros
     * grosseiros (transformação identidade por engano, ou parâmetro/sinal trocado que "explode"
     * o resultado), não validar a exatidão geodésica fina.
     */
    private fun assertShiftWithinPlausibleRange(
        originalLat: Double,
        originalLon: Double,
        resultLat: Double,
        resultLon: Double,
    ) {
        val deltaLat = abs(resultLat - originalLat)
        val deltaLon = abs(resultLon - originalLon)

        assertTrue("deltaLat=$deltaLat deveria ser > 0.00001°", deltaLat > 0.00001)
        assertTrue("deltaLat=$deltaLat deveria ser < 0.01°", deltaLat < 0.01)
        assertTrue("deltaLon=$deltaLon deveria ser > 0.00001°", deltaLon > 0.00001)
        assertTrue("deltaLon=$deltaLon deveria ser < 0.01°", deltaLon < 0.01)
    }
}
