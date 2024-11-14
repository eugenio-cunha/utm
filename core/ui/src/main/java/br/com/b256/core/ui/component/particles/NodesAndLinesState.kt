package br.com.b256.core.ui.component.particles

import androidx.compose.ui.unit.IntSize
import br.com.b256.core.ui.component.particles.Node.Companion.next
import kotlin.math.roundToInt

internal data class NodesAndLinesState(
    val nodes: List<Node> = emptyList(),
    val pointer: Node? = null,
    val dotRadius: Float,
    val size: IntSize = IntSize.Zero,
    val speed: Float
) {
    companion object {
        fun NodesAndLinesState.sizeChanged(
            size: IntSize,
            populationFactor: Float
        ) : NodesAndLinesState {
            if (size == this.size) return this
            return copy(
                nodes = (0..size.realPopulation(populationFactor)).map {
                    Node.create(size)
                },
                size = size
            )
        }

        fun NodesAndLinesState.next(durationMillis: Long): NodesAndLinesState {
            return copy(
                nodes = nodes.map {
                    it.next(size, durationMillis, dotRadius, speed)
                }
            )
        }

        fun NodesAndLinesState.populationControl(populationFactor: Float): NodesAndLinesState {
            val count = size.realPopulation(populationFactor = populationFactor)

            return if(count < nodes.size) {
                copy(nodes = nodes.shuffled().take(count))
            } else {
                copy(nodes = nodes + (0..count-nodes.size).map { Node.create(size) })
            }
        }

        private fun IntSize.realPopulation(populationFactor: Float): Int {
            return (width * height / 10_000 * populationFactor).roundToInt()
        }
    }
}
