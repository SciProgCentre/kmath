/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.complex.Complex
import space.kscience.kmath.complex.complex
import space.kscience.kmath.nd.AlgebraND
import space.kscience.kmath.nd.double

fun main() {
    // 2d element
    val element = AlgebraND.double(2, 2).complex().produce { (i, j) ->
        Complex(i.toDouble() - j.toDouble(), i.toDouble() + j.toDouble())
    }
    println(element)

    // 1d element operation
    val result = with(AlgebraND.double(8).complex()) {
        val a = produce { (it) -> i * it - it.toDouble() }
        val b = 3
        val c = Complex(1.0, 1.0)

        (a pow b) + c
    }

    println(result)
}
