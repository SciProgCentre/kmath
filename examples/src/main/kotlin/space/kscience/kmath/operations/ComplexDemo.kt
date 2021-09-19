/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.complex.Complex
import space.kscience.kmath.complex.ComplexField
import space.kscience.kmath.complex.withNd
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.autoNd

fun main() {
    // 2d element
    val element = ComplexField.autoNd(2, 2).produce { (i, j) ->
        Complex(i - j, i + j)
    }
    println(element)

    // 1d element operation
    val result: StructureND<Complex> = ComplexField.withNd(8) {
        val a = produce { (it) -> i * it - it.toDouble() }
        val b = 3
        val c = Complex(1.0, 1.0)

        (a pow b) + c
    }

    println(result)
}
