package scientifik.kmath.operations

import scientifik.kmath.structures.NDElement
import scientifik.kmath.structures.NDField
import scientifik.kmath.structures.complex

fun main() {
    val element = NDElement.complex(2, 2) { index: IntArray ->
        Complex(index[0].toDouble() - index[1].toDouble(), index[0].toDouble() + index[1].toDouble())
    }

    val compute = (NDField.complex(8)) {
        val a = produce { (it) -> i * it - it.toDouble() }
        val b = 3
        val c = Complex(1.0, 1.0)

        (a pow b) + c
    }
}
