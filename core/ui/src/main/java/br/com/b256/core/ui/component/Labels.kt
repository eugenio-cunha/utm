package br.com.b256.core.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import kotlin.math.max

@Composable
fun Label(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    Text(
        modifier = modifier,
        text = "$text:",
        style = style,
        color = color.copy(alpha = 0.5f),
    )
}

@Composable
fun Description(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    overflow: Int = 30,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    Text(
        text = if (overflow > 0 && text.length > overflow) text.substring(
            startIndex = 0,
            endIndex = overflow,
        )
            .padEnd(length = overflow + 3, padChar = '.') else text,
        modifier = modifier,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        style = style,
        color = color,
    )
}

@Composable
fun Labels(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measures, constraints ->
        val looseConstraints = constraints.copy(
            minWidth = 0,
            minHeight = 0,
        )

        val placeable = measures.map { measurable ->
            measurable.measure(looseConstraints)
        }

        val labels = List(placeable.size / 2) { index ->
            placeable[2 * index]
        }

        val descriptions = List(placeable.size / 2) { index ->
            placeable[2 * index + 1]
        }

        val maxLabelWidth = labels.maxByOrNull { it.width }?.width ?: 0

        val height = List(labels.size) { index ->
            max(labels[index].height, descriptions[index].height) + 5
        }.sum()

        layout(
            width = constraints.minWidth,
            height = height.coerceAtMost(constraints.maxHeight),
        ) {
            var yPosition = 0

            for (i in labels.indices) {
                val label = labels[i]
                val description = descriptions[i]

                label.placeRelative(
                    x = 0,
                    y = yPosition,
                )

                description.placeRelative(
                    x = maxLabelWidth + 15,
                    y = yPosition,
                )
                yPosition += label.height.coerceAtLeast(description.height) + 5
            }
        }
    }
}
