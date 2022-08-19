/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.real.*
import space.kscience.kmath.structures.DoubleBuffer

fun main() {
    val x0 = DoubleVector(0.0, 0.0, 0.0)
    val sigma = DoubleVector(1.0, 1.0, 1.0)

    val gaussian: (Point<Double>) -> Double = { x ->
        require(x.size == x0.size)
        kotlin.math.exp(-((x - x0) / sigma).square().sum())
    }

    fun ((Point<Double>) -> Double).grad(x: Point<Double>): Point<Double> {
        require(x.size == x0.size)
        return DoubleBuffer(x.size) { i ->
            val h = sigma[i] / 5
            val dVector = DoubleBuffer(x.size) { if (it == i) h else 0.0 }
            val f1 = this(x + dVector / 2)
            val f0 = this(x - dVector / 2)
            (f1 - f0) / h
        }
    }

    println(gaussian.grad(x0))

}