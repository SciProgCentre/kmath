/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors

import space.kscience.kmath.linear.transpose
import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.nd.MutableStructure2D
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.tensors.core.*
import space.kscience.kmath.tensors.core.tensorAlgebra
import kotlin.math.*

fun MutableStructure2D<Double>.print() {
    val n = this.shape.component1()
    val m = this.shape.component2()
    for (i in 0 until n) {
        for (j in 0 until m) {
            val x = (this[i, j] * 100).roundToInt() / 100.0
            print("$x ")
        }
        println()
    }
    println("______________")
}

@OptIn(PerformancePitfall::class)
fun main(): Unit = Double.tensorAlgebra.withBroadcast {
    val shape = intArrayOf(5, 3)
    val buffer = doubleArrayOf(
        1.000000, 2.000000, 3.000000,
        2.000000, 3.000000, 4.000000,
        3.000000, 4.000000, 5.000000,
        4.000000, 5.000000, 6.000000,
        5.000000, 6.000000, 7.000000
    )
    val buffer2 = doubleArrayOf(
        0.000000, 0.000000, 0.000000,
        0.000000, 0.000000, 0.000000,
        0.000000, 0.000000, 0.000000
    )
    val tensor = fromArray(shape, buffer).as2D()
    val v = fromArray(intArrayOf(3, 3), buffer2).as2D()
    val w_shape = intArrayOf(3, 1)
    var w_buffer = doubleArrayOf(0.000000)
    for (i in 0 until 3 - 1) {
        w_buffer += doubleArrayOf(0.000000)
    }
    val w = BroadcastDoubleTensorAlgebra.fromArray(w_shape, w_buffer).as2D()
    tensor.print()
    var ans = Pair(w, v)
    tensor.svdGolabKahan(v, w)

    println("u")
    tensor.print()
    println("w")
    w.print()
    println("v")
    v.print()


}
