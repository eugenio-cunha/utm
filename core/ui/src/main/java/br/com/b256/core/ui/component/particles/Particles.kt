package br.com.b256.core.ui.component.particles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Exibe uma animação de particulas nós e aresta interativa
 *
 * @param modifier O modificador a ser aplicado ao layout.
 * @param content O conteúdo do layout (opcional)
 *
 * */
@Composable
fun Particles(
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .nodesAndLines(
                contentColor = MaterialTheme.colorScheme.onSurface,
                threshold = 0.09f,
                maxThickness = 2f,
                dotRadius = 5f,
                speed = 0.05f,
                populationFactor = 0.5f
            ),
        contentAlignment = Alignment.Center
    ) {
        content?.invoke()
    }
}
