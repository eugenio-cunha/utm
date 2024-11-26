package br.com.b256.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.b256.core.designsystem.icon.B256Icons
import br.com.b256.core.designsystem.theme.PaddingHalf
import br.com.b256.core.designsystem.theme.PaddingSingle
import androidx.compose.material3.SearchBar as M3SearchBar

/**
 * Uma barra de pesquisa representa um campo de pesquisa flutuante que permite aos usuários inserir
 * uma palavra-chave ou frase e obter informações relevantes.
 *
 * Ela pode ser usada como uma forma de navegar por um aplicativo por meio de consultas de pesquisa.
 * Uma barra de pesquisa se expande em uma "visualização" de pesquisa e pode ser usada para
 * exibir sugestões dinâmicas ou resultados de pesquisa.
 *
 * @param modifier O modificador a ser aplicado a esta barra de pesquisa.
 * @param enabled O estado habilitado deste campo de entrada.
 * @param placeholder O espaço reservado a ser exibido quando a consulta estiver vazia.
 * @param containerColor A cor do contêiner da barra de pesquisa.
 * @param query O texto da consulta a ser exibido no campo de entrada.
 * @param onQueryChange Predicado da ação de pesquisa, o retorno de chamada a ser invocado.
 * @param onSuggestionSelect Ação de seleção, o retorno da seleção de uma sugestão.
 * @param onSuggestionDisplay Predicado de exibição de sugestões.
 * @param onClose Acão de encerramento da pesquisa
 *
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchBar(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.outlineVariant,
    query: String,
    onQueryChange: (String) -> List<T>,
    onSuggestionDisplay: (T) -> Pair<String, String>,
    onSuggestionSelect: (T) -> Unit = {},
    onClose: (() -> Unit)? = null,
) {
    var queryText by remember { mutableStateOf(query) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    M3SearchBar(
        modifier = modifier,
        inputField = {
            SearchBarDefaults.InputField(
                enabled = enabled,
                query = queryText,
                onQueryChange = {
                    queryText = it
                },
                onSearch = { expanded = false },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = {
                    placeholder?.let {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = B256Icons.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                trailingIcon = {
                    if (expanded) {
                        Icon(
                            modifier = Modifier.clickable {
                                expanded = false
                                onClose?.invoke()
                            },
                            imageVector = B256Icons.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            )
        },
        expanded = expanded,
        onExpandedChange = { expanded = it },
        windowInsets = WindowInsets(0.dp),
        colors = SearchBarDefaults.colors(
            containerColor = containerColor
        ),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(PaddingHalf),
            verticalArrangement = Arrangement.spacedBy(PaddingHalf),
        ) {
            items(items = onQueryChange(queryText)) { item ->
                onSuggestionDisplay(item).let { pair ->
                    if (pair.first.isNotEmpty() || pair.second.isNotEmpty()) {
                        Suggestion(
                            headline = pair.first,
                            overline = pair.second
                        ) {
                            expanded = false
                            onSuggestionSelect(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Suggestion(
    modifier: Modifier = Modifier,
    headline: String,
    overline: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = modifier
                .height(height = 70.dp)
                .fillMaxWidth()
                .clickable {
                    onClick()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                modifier = Modifier.padding(horizontal = PaddingSingle),
                imageVector = B256Icons.Subdirectory,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
            )

            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                if (overline.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PaddingHalf),
                        text = overline,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                if (headline.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PaddingHalf),
                        text = headline,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
