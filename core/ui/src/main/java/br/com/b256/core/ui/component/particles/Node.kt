package br.com.b256.core.ui.component.particles

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

internal data class Node(
    val position: Offset,
    val vector: Offset
) {
    companion object {
        /**
         * Calcule a distância deste [Node] até outro.
         */
        infix fun Node.distanceTo(another: Node): Float {
            return (position - another.position).getDistance()
        }

        /**
         * Calcule onde esse ponto estará na próxima iteração.
         *
         * @param borders Tamanho da tela onde os pontos saltam.
         * @param durationMillis Quanto tempo vai passar até a próxima iteração.
         * @param dotRadius O raio deste ponto quando ele é desenhado.
         * @param speedCoefficient Embora haja um vetor que indique movimento, este
         * parâmetro é usado para acelerar ou desacelerar a animação à vontade.
         */
        fun Node.next(
            borders: IntSize,
            durationMillis: Long,
            dotRadius: Float,
            speedCoefficient: Float
        ): Node {
            val speed = vector * speedCoefficient

            return Node(
                position = position + Offset(
                    x = speed.x / 1000f * durationMillis,
                    y = speed.y / 1000f * durationMillis,
                ),
                vector = vector
            ).let { (position, vector) ->
                val borderBottom = borders.height - dotRadius
                val borderRight = borders.width - dotRadius
                Node(
                    position = Offset(
                        x = when {
                            position.x < dotRadius -> dotRadius - (position.x - dotRadius)
                            position.x > borderRight -> borderRight - (position.x - borderRight)
                            else -> position.x
                        },
                        y = when {
                            position.y < dotRadius -> dotRadius - (position.y - dotRadius)
                            position.y > borderBottom -> borderBottom - (position.y - borderBottom)
                            else -> position.y
                        }
                    ),
                    vector = Offset(
                        x = when {
                            position.x < dotRadius -> -vector.x
                            position.x > borderRight -> -vector.x
                            else -> vector.x
                        },
                        y = when {
                            position.y < dotRadius -> -vector.y
                            position.y > borderBottom -> -vector.y
                            else -> vector.y
                        }
                    )
                )
            }
        }

        /**
         * Crie uma instância de ponto aleatória pertencente a @param borders.
         */
        fun create(borders: IntSize): Node {
            return Node(
                position = Offset(
                    (0..borders.width).random().toFloat(),
                    (0..borders.height).random().toFloat()
                ),
                vector = Offset(
                    // Primeiro, randomize a direção. Segundo, randomize a amplitude do vetor de velocidade.
                    listOf(-1f, 1f).random() * ((borders.width.toFloat() / 100f).toInt()..(borders.width.toFloat() / 10f).toInt()).random()
                        .toFloat(),
                    listOf(-1f, 1f).random() * ((borders.height.toFloat() / 100f).toInt()..(borders.height.toFloat() / 10f).toInt()).random()
                        .toFloat()
                )
            )
        }
    }
}
