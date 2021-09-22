/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.operations.algebra
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    val random = Random(12224)
    val dim = 1000

    //creating invertible matrix
    val matrix1 = Double.algebra.linearSpace.buildMatrix(dim, dim) { i, j ->
        if (i <= j) random.nextDouble() else 0.0
    }
    val matrix2 = Double.algebra.linearSpace.buildMatrix(dim, dim) { i, j ->
        if (i <= j) random.nextDouble() else 0.0
    }

    val time = measureTimeMillis {
        with(Double.algebra.linearSpace) {
            repeat(10) {
                val res = matrix1 dot matrix2
            }
        }
    }

    println(time)

}