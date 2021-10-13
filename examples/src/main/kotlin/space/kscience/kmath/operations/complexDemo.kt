/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.complex.Complex
import space.kscience.kmath.complex.algebra
import space.kscience.kmath.complex.bufferAlgebra
import space.kscience.kmath.complex.ndAlgebra
import space.kscience.kmath.nd.BufferND
import space.kscience.kmath.nd.StructureND

fun main() = Complex.algebra {
    val complex = 2 + 2 * i
    println(complex * 8 - 5 * i)

    //flat buffer
    val buffer = bufferAlgebra(8).run {
        buffer { Complex(it, -it) }.map { Complex(it.im, it.re) }
    }
    println(buffer)


    // 2d element
    val element: BufferND<Complex> = ndAlgebra(2, 2).produce { (i, j) ->
        Complex(i - j, i + j)
    }
    println(element)

    // 1d element operation
    val result: StructureND<Complex> = ndAlgebra(8).run {
        val a = produce { (it) -> i * it - it.toDouble() }
        val b = 3
        val c = Complex(1.0, 1.0)

        (a pow b) + c
    }
    println(result)
}
