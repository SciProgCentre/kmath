/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.algebra

@OptIn(PerformancePitfall::class)
fun main(): Unit = with(Double.algebra.ndAlgebra) {
    val structure: MutableStructure2D<Float64> = mutableStructureND(ShapeND(2, 2)) { (i, j) ->
        i.toDouble() + j.toDouble()
    }.as2D()

    structure[0, 1] = -2.0

    val structure2 = mutableStructureND(2, 2) { (i, j) -> i.toDouble() + j.toDouble() }.as2D()

    structure2[0, 1] = 2.0


    println(structure + structure2)
}