/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.commons.linear.CMLinearSpace
import space.kscience.kmath.linear.matrix
import space.kscience.kmath.nd.Float64BufferND
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.nd.mutableStructureND
import space.kscience.kmath.nd.ndAlgebra
import space.kscience.kmath.viktor.viktorAlgebra
import kotlin.collections.component1
import kotlin.collections.component2

fun main() {
    val viktorStructure = Float64Field.viktorAlgebra.mutableStructureND(2, 2) { (i, j) ->
        if (i == j) 2.0 else 0.0
    }

    val cmMatrix: Structure2D<Double> = CMLinearSpace.matrix(2, 2)(0.0, 1.0, 0.0, 3.0)

    val res: Float64BufferND = Float64Field.ndAlgebra {
        exp(viktorStructure) + 2.0 * cmMatrix
    }

    println(res)
}