/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.complex.Complex
import space.kscience.kmath.complex.algebra
import space.kscience.kmath.complex.ndAlgebra
import space.kscience.kmath.nd.BufferND
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.structureND

fun main() = Complex.algebra {
    val complex = 2 + 2 * i
    println(complex * 8 - 5 * i)

    //flat buffer
    val buffer = with(bufferAlgebra) {
        buffer(8) { Complex(it, -it) }.map { Complex(it.im, it.re) }
    }
    println(buffer)

    // 2d element
    val element: BufferND<Complex> = ndAlgebra.structureND(2, 2) { (i, j) ->
        Complex(i - j, i + j)
    }
    println(element)

    // 1d element operation
    val result: StructureND<Complex> = ndAlgebra {
        val a = structureND(8) { (it) -> i * it - it.toDouble() }
        val b = 3
        val c = Complex(1.0, 1.0)

        (a pow b) + c
    }
    println(result)
}
