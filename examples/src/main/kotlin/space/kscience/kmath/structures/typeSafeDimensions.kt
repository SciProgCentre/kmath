/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.kmath.dimensions.D2
import space.kscience.kmath.dimensions.D3
import space.kscience.kmath.dimensions.DMatrixContext
import space.kscience.kmath.dimensions.Dimension

private fun DMatrixContext<Double, *>.simple() {
    val m1 = produce<D2, D3> { i, j -> (i + j).toDouble() }
    val m2 = produce<D3, D2> { i, j -> (i + j).toDouble() }

    //Dimension-safe addition
    m1.transpose() + m2
}

private object D5 : Dimension {
    override val dim: Int = 5
}

private fun DMatrixContext<Double, *>.custom() {
    val m1 = produce<D2, D5> { i, j -> (i + j).toDouble() }
    val m2 = produce<D5, D2> { i, j -> (i - j).toDouble() }
    val m3 = produce<D2, D2> { i, j -> (i - j).toDouble() }
    (m1 dot m2) + m3
}

fun main(): Unit = with(DMatrixContext.real) {
    simple()
    custom()
}
