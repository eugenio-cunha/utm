package br.com.b256.domain.entities.enums

/**
 * Representa os datums geodésicos disponíveis para exibir latitude/longitude e coordenadas UTM:
 * o [WGS84] (padrão global) e os principais datums legados/locais do Brasil, da América do
 * Norte e da Europa.
 *
 * O GPS sempre entrega a posição no datum [WGS84] — os demais valores representam uma
 * transformação aplicada sobre essa posição bruta (ver
 * `br.com.b256.data.services.location.extension.transformToDatum`) antes de ela ser exibida ou
 * convertida para UTM.
 *
 * - Brasil: [SIRGAS2000] é o datum oficial vigente (Decreto nº 5.334/2004); [SAD69] e
 *   [CORREGO_ALEGRE] são datums anteriores, ainda comuns em documentos e levantamentos legados.
 * - América do Norte: [NAD83] é o datum oficial vigente nos EUA/Canadá/México; [NAD27] é o
 *   datum anterior, ainda comum em mapas e documentos legados dos EUA/Canadá.
 * - Europa: [ETRS89] é o datum oficial vigente na União Europeia; [ED50] é o datum anterior,
 *   ainda comum em cartas náuticas e documentos da indústria de óleo e gás.
 *
 * [SIRGAS2000], [NAD83] e [ETRS89] são, na prática, coincidentes com o [WGS84] (diferença de
 * poucos centímetros, irrelevante para navegação/GIS) — por isso não recebem uma translação
 * própria em `transformToDatum`, ao contrário de [SAD69], [CORREGO_ALEGRE], [NAD27] e [ED50].
 *
 * @property value Nome usado para armazenamento/persistência (DataStore).
 */
enum class Datum(val value: String) {
    WGS84(value = "wgs84"),

    // Brasil
    SIRGAS2000(value = "sirgas2000"),
    SAD69(value = "sad69"),
    CORREGO_ALEGRE(value = "corrego_alegre"),

    // América do Norte
    NAD83(value = "nad83"),
    NAD27(value = "nad27"),

    // Europa
    ETRS89(value = "etrs89"),
    ED50(value = "ed50"),
    ;

    companion object {
        /**
         * Retorna o [Datum] correspondente a um determinado valor de string.
         * A correspondência é feita tanto pelo nome da enumeração quanto pelo seu [value]
         * associado, ignorando maiúsculas/minúsculas.
         *
         * Se nenhum datum correspondente for encontrado, retorna [WGS84].
         *
         * @param value A string a ser convertida em [Datum].
         * @return O [Datum] correspondente ou [WGS84] se não encontrado.
         */
        fun from(value: String): Datum =
            entries
                .find {
                    it.name.equals(value, ignoreCase = true) ||
                        it.value.equals(value, ignoreCase = true)
                } ?: WGS84
    }
}
