package br.com.b256.core.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.b256.core.designsystem.theme.IconDouble
import br.com.b256.core.designsystem.theme.PaddingHalf
import br.com.b256.core.designsystem.theme.StrokeHalf

@Composable
fun Timeline(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    content: LazyViewScope.() -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = PaddingValues(PaddingHalf),
    ) {
        toLazyViewScope().content()

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

interface LazyViewScope : LazyListScope

private fun LazyListScope.toLazyViewScope() = object : LazyViewScope {
    override fun item(
        key: Any?,
        contentType: Any?,
        content: @Composable (LazyItemScope.() -> Unit),
    ) = this@toLazyViewScope.item(
        key = key,
        contentType = contentType,
        content = content,
    )

    override fun items(
        count: Int,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable LazyItemScope.(index: Int) -> Unit,
    ) = this@toLazyViewScope.items(
        count = count,
        key = key,
        contentType = contentType,
        itemContent = itemContent,
    )

    @ExperimentalFoundationApi
    override fun stickyHeader(
        key: Any?,
        contentType: Any?,
        content: @Composable LazyItemScope.() -> Unit,
    ) = this@toLazyViewScope.stickyHeader(
        key = key,
        contentType = contentType,
    ) {
        content()
    }
}

fun LazyViewScope.item(
    icon: ImageVector,
    content: @Composable () -> Unit,
) {
    item {
        Point(
            icon = icon,
            content = content,
        )
    }
}

inline fun <T> LazyViewScope.items(
    items: List<T>,
    icon: ImageVector,
    noinline key: ((item: T) -> Any)? = null,
    noinline contentType: (item: T) -> Any? = { null },
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit,
) = items(
    count = items.size,
    key = if (key != null) { index: Int -> key(items[index]) } else null,
    contentType = { index: Int -> contentType(items[index]) },
) {
    Point(icon = icon) {
        itemContent(items[it])
    }
}

@Composable
fun Point(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    content: @Composable () -> Unit,
) {
    val color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        val (circle, topLine, bottomLine, reviewContent) = createRefs()

        VerticalDivider(
            modifier = Modifier.constrainAs(topLine) {
                top.linkTo(parent.top)
                bottom.linkTo(circle.top, 1.dp)
                start.linkTo(circle.start)
                end.linkTo(circle.end)
                width = Dimension.value(1.dp)
                height = Dimension.fillToConstraints
            },
            color = color,
        )

        Icon(
            modifier = Modifier
                .size(IconDouble)
                .constrainAs(circle) {
                    start.linkTo(parent.start)
                    top.linkTo(reviewContent.top)
                    bottom.linkTo(reviewContent.bottom)
                },
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 15.dp)
                .constrainAs(reviewContent) {
                    start.linkTo(circle.end, 5.dp)
                    top.linkTo(parent.top, 15.dp)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                },
            shape = MaterialTheme.shapes.small,
            content = content,
        )

        VerticalDivider(
            modifier = Modifier.constrainAs(bottomLine) {
                top.linkTo(circle.bottom, 1.dp)
                bottom.linkTo(parent.bottom)
                start.linkTo(circle.start)
                end.linkTo(circle.end)
                width =
                    Dimension.value(StrokeHalf)
                height = Dimension.fillToConstraints
            },
            color = color,
        )
    }
}
