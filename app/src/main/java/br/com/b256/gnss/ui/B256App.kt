package br.com.b256.gnss.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import br.com.b256.presentation.navigation.B256NavDisplay

/**
 * O ponto de entrada principal para a interface do usuário do aplicativo B256 GNSS.
 *
 * Esta função configura a estrutura base da tela utilizando um [Scaffold], gerencia o preenchimento
 * dos insets do sistema (janelas) para garantir uma exibição correta em modo "edge-to-edge" e
 */
@Composable
internal fun B256App(
    modifier: Modifier = Modifier,
){
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ){ padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal,
                    ),
                ),
        ) {
            B256NavDisplay(modifier = Modifier.fillMaxSize())
        }
    }
}
