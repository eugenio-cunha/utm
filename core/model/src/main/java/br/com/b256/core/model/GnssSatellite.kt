package br.com.b256.core.model

import br.com.b256.core.model.enums.Constellation

/**
 * Representa um satélite de um Sistema Global de Navegação por Satélite (GNSS).
 *
 * Esta classe consolida informações sobre a identificação, posicionamento orbital
 * e qualidade do sinal de rádio captado pelo dispositivo, facilitando a exibição
 * em interfaces de usuário e o monitoramento do status do GPS/GNSS.
 *
 * @property svid O identificador único do satélite.
 * @property name O nome amigável exibido na UI.
 * @property constellation A constelação de satélites.
 * @property constellationType O tipo original retornado pelo Android.
 * @property azimuthDegrees O azimute em graus.
 * @property elevationDegrees A elevação em graus.
 * @property cn0DbHz Intensidade do sinal.
 * @property usedInFix Indica se o satélite participa do cálculo da posição.
 */
data class GnssSatellite(
    val svid: Int,

    /**
     * Nome amigável exibido na UI.
     * Ex.: "23 NAVSTAR", "11 GALILEO"
     */
    val name: String,

    /**
     * Nome da constelação.
     */
    val constellation: Constellation,

    /**
     * Tipo original retornado pelo Android.
     */
    val constellationType: Int,

    /**
     * Azimute em graus.
     * 0° = Norte
     * 90° = Leste
     * 180° = Sul
     * 270° = Oeste
     */
    val azimuthDegrees: Float,

    /**
     * Elevação em graus.
     * 0° = horizonte
     * 90° = zênite
     */
    val elevationDegrees: Float,

    /**
     * Intensidade do sinal.
     */
    val cn0DbHz: Float,

    /**
     * Indica se participa do cálculo da posição.
     */
    val usedInFix: Boolean,

    /**
     * Frequência do sinal em Hz.
     * Disponível a partir da API 26.
     */
    val carrierFrequencyHz: Float?,

    /**
     * Baseband C/N0.
     * Disponível a partir da API 30.
     */
    val basebandCn0DbHz: Float?
)
