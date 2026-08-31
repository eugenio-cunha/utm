package br.com.b256.domain.entities.enums

/**
 * Representa os diferentes sistemas de navegação global por satélite (GNSS) e sistemas de aumento.
 *
 * Cada constante define uma constelação específica, associando um nome de exibição e uma
 * cor característica para representação visual em mapas ou gráficos de intensidade de sinal.
 *
 * @property value O nome formal ou rótulo de exibição da constelação de satélites.
 * @property color A cor em formato hexadecimal associada à constelação de satélites.
 */
enum class Constellation(val value: String, val color: Long) {
    GPS(
        value = "NAVSTAR",
        color = 0xFFE53935 // Vermelho
    ),
    GLONASS(
        value = "GLONASS",
        color = 0xFF43A047 // Verde
    ),
    GALILEO(
        value = "GALILEO",
        color = 0xFF1E88E5 // Azul
    ),
    BEIDOU(
        value = "BEIDOU",
        color = 0xFF00ACC1 // Ciano
    ),
    QZSS(
        value = "QZSS",
        color = 0xFF8E24AA // Roxo
    ),
    SBAS(
        value = "SBAS",
        color = 0xFFFB8C00 // Laranja
    ),
    IRNSS(
        value = "IRNSS",
        color = 0xFFFDD835 // Amarelo
    ),
    UNKNOWN(
        value = "-",
        color = 0xFF888888 // Cinza
    );

    companion object {
        /**
         * Retorna o [Constellation] correspondente a um determinado valor de string.
         * A correspondência é feita tanto pelo nome da enumeração quanto pelo seu valor associado,
         * ignorando maiúsculas/minúsculas.
         *
         * Se nenhum tipo de grupo correspondente for encontrado, retorna [UNKNOWN].
         *
         * @param value A string a ser convertida em [Constellation].
         * @return O [Constellation] correspondente ou [UNKNOWN] se não encontrado.
         */
        fun from(value: String): Constellation = Constellation.entries
            .find {
                it.name.equals(value, ignoreCase = true) ||
                        it.value.equals(value, ignoreCase = true)
            } ?: UNKNOWN
    }
}
