/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.operations.algebra
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
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

    val time = measureTime {
        with(Double.algebra.linearSpace) {
            repeat(10) {
                matrix1 dot matrix2
            }
        }
    }

    println(time)

}