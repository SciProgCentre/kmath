/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import kotlin.random.Random
import kotlin.time.measureTime

fun main() = with(Float64ParallelLinearSpace) {
    val random = Random(12224)
    val dim = 1000

    //creating invertible matrix
    val matrix1 = buildMatrix(dim, dim) { i, j ->
        if (i <= j) random.nextDouble() else 0.0
    }
    val matrix2 = buildMatrix(dim, dim) { i, j ->
        if (i <= j) random.nextDouble() else 0.0
    }

    val time = measureTime {
        repeat(30) {
            matrix1 dot matrix2
        }
    }

    println(time)

}