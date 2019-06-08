package scientifik.kmath.operations

import scientifik.kmath.structures.NDElement
import scientifik.kmath.structures.complex

fun main() {
    val element = NDElement.complex(2, 2) { index: IntArray ->
        Complex(index[0].toDouble() - index[1].toDouble(), index[0].toDouble() + index[1].toDouble())
    }
}