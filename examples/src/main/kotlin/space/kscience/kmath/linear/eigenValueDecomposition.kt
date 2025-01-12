/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.commons.linear.CMLinearSpace
import space.kscience.kmath.ejml.EjmlLinearSpaceDDRM
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.structures.Float64
import kotlin.random.Random

@OptIn(PerformancePitfall::class)
fun main() {
    val dim = 46

    val random = Random(123)

    val u = Float64.algebra.linearSpace.buildMatrix(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }

    listOf(CMLinearSpace, EjmlLinearSpaceDDRM).forEach { algebra ->
        with(algebra) {
            //create a symmetric matrix
            val matrix = buildMatrix(dim, dim) { row, col ->
                if (row >= col) u[row, col] else u[col, row]
            }
            val eigen = matrix.getOrComputeAttribute(EIG) ?: error("Failed to compute eigenvalue decomposition")
            check(
                StructureND.contentEquals(
                    matrix,
                    eigen.v dot eigen.d dot eigen.v.transposed(),
                    1e-4
                )
            ) { "$algebra decomposition failed" }
            println("$algebra eigenvalue decomposition complete and checked" )
        }
    }

}