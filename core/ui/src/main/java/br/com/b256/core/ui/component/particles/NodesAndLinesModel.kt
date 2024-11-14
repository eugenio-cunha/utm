package br.com.b256.core.ui.component.particles

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import br.com.b256.core.ui.component.particles.Node.Companion.distanceTo
import br.com.b256.core.ui.component.particles.NodesAndLinesState.Companion.next
import br.com.b256.core.ui.component.particles.NodesAndLinesState.Companion.populationControl
import br.com.b256.core.ui.component.particles.NodesAndLinesState.Companion.sizeChanged
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.isActive
import kotlin.math.pow
import kotlin.math.sqrt

internal fun Modifier.nodesAndLines(
    contentColor: Color = Color.White,
    threshold: Float,
    maxThickness: Float,
    dotRadius: Float,
    speed: Float,
    populationFactor: Float
) = this.composed {
    val nodesAndLinesModel = remember {
        NodesAndLinesModel(
            NodesAndLinesState(
                dotRadius = dotRadius,
                speed = speed
            )
        )
    }

    LaunchedEffect(speed, dotRadius, populationFactor) {
        nodesAndLinesModel.populationControl(speed, dotRadius, populationFactor)
    }

    LaunchedEffect(Unit) {
        var lastFrame = 0L
        while (isActive) {
            val nextFrame = awaitFrame() / 100_000L
            if (lastFrame != 0L) {
                val period = nextFrame - lastFrame
                nodesAndLinesModel.next(period)
            }
            lastFrame = nextFrame
        }
    }

    pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { offset ->
                nodesAndLinesModel.pointerDown(offset)
            },
            onDragEnd = {
                nodesAndLinesModel.pointerUp()
            },
            onDragCancel = {
                nodesAndLinesModel.pointerUp()
            },
            onDrag = { change, dragAmount ->
                nodesAndLinesModel.pointerMove(dragAmount)
                change.consume()
            }
        )
    }
        .onSizeChanged {
            nodesAndLinesModel.sizeChanged(it, populationFactor)
        }
        .drawBehind {
            val allDots = with(nodesAndLinesModel.nodesAndLinesState) { (nodes + pointer).filterNotNull() }

            allDots.forEach {
                drawCircle(contentColor, radius = dotRadius, center = it.position)
            }

            val realThreshold = threshold * sqrt(size.width.pow(2) + size.height.pow(2))

            allDots.nestedForEach { first, second ->
                val distance = first distanceTo second

                if (distance <= realThreshold) {
                    drawLine(
                        contentColor,
                        first.position,
                        second.position,
                        0.5f + (realThreshold - distance) * maxThickness / realThreshold
                    )
                }
            }
        }
}

@Immutable
internal class NodesAndLinesModel(
    initialNodesAndLinesState: NodesAndLinesState
) {
    var nodesAndLinesState by mutableStateOf(initialNodesAndLinesState)

    fun populationControl(
        speed: Float,
        dotRadius: Float,
        populationFactor: Float
    ) {
        nodesAndLinesState = nodesAndLinesState.copy(
            speed = speed,
            dotRadius = dotRadius
        ).populationControl(populationFactor)
    }

    fun next(period: Long) {
        nodesAndLinesState = nodesAndLinesState.next(period)
    }

    fun sizeChanged(size: IntSize, populationFactor: Float) {
        nodesAndLinesState = nodesAndLinesState.sizeChanged(
            size = size,
            populationFactor = populationFactor
        )
    }

    fun pointerDown(offset: Offset) {
        nodesAndLinesState = nodesAndLinesState.copy(
            pointer = Node(
                position = offset,
                vector = Offset.Zero
            )
        )
    }

    fun pointerMove(offset: Offset) {
        val currentPointer = nodesAndLinesState.pointer ?: return

        nodesAndLinesState = nodesAndLinesState.copy(
            pointer = nodesAndLinesState.pointer?.copy(
                position = currentPointer.position + offset,
                vector = Offset.Zero
            )
        )
    }

    fun pointerUp() {
        nodesAndLinesState = nodesAndLinesState.copy(pointer = null)
    }
}

private fun <T> List<T>.nestedForEach(block: (T, T) -> Unit) {
    for (i in this.indices) {
        for (j in i + 1 until this.size) {
            block(this[i], this[j])
        }
    }
}
