/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import kotlin.random.Random
import kotlin.time.measureTime

fun main(): Unit = with(Float64LinearSpace) {
    val random = Random(1224)
    val dim = 500

    //creating invertible matrix
    val u = buildMatrix(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }
    val l = buildMatrix(dim, dim) { i, j -> if (i >= j) random.nextDouble() else 0.0 }
    val matrix = l dot u

    val time = measureTime {
        repeat(20) {
            lupSolver().inverse(matrix)
        }
    }

    println(time)
}