package space.kscience.kmath.operations

import space.kscience.kmath.complex.Complex
import space.kscience.kmath.complex.complex
import space.kscience.kmath.nd.AlgebraND

fun main() {
    // 2d element
    val element = AlgebraND.complex(2, 2).produce { (i, j) ->
        Complex(i.toDouble() - j.toDouble(), i.toDouble() + j.toDouble())
    }
    println(element)

    // 1d element operation
    val result = with(AlgebraND.complex(8)) {
        val a = produce { (it) -> i * it - it.toDouble() }
        val b = 3
        val c = Complex(1.0, 1.0)

        (a pow b) + c
    }

    println(result)
}
