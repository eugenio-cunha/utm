package br.com.b256.core.ui.extension

import androidx.compose.ui.Modifier

/**
 * Condicional do Modifier
 *
 * @param condition Condição de modificação
 * */
fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}
