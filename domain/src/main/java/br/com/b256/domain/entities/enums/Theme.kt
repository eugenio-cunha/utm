package br.com.b256.domain.entities.enums

/**
 * Representa os temas disponíveis para a aplicação.
 *
 * Cada tema possui um [value] de string correspondente que pode ser usado para armazenamento ou comunicação.
 */
enum class Theme(val value: String) {
    LIGHT(value = "light"),
    DARK(value = "dark"),
    FOLLOW_SYSTEM(value = "follow_system"),
    ;

    companion object {
        fun from(value: String): Theme =
            entries
                .find {
                    it.name.equals(value, ignoreCase = true) ||
                        it.value.equals(value, ignoreCase = true)
                } ?: FOLLOW_SYSTEM
    }
}
