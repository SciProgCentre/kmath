/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.operations.bufferAlgebra
import space.kscience.kmath.structures.asBuffer
import kotlin.test.Test


class DTWTest {

    @Test
    fun someData() : Unit {
        with(Double.algebra.bufferAlgebra.seriesAlgebra()) {
            val firstSequence: DoubleArray = doubleArrayOf(0.0, 2.0, 3.0, 1.0, 3.0, 0.1, 0.0, 1.0)
            val secondSequence: DoubleArray = doubleArrayOf(1.0, 0.0, 3.0, 0.0, 0.0, 3.0, 2.0, 0.0, 2.0)

            val seriesOne: Series<Double> = firstSequence.asBuffer().moveTo(0)
            val seriesTwo: Series<Double> = secondSequence.asBuffer().moveTo(0)

            val result = DoubleFieldOpsND.dynamicTimeWarping(seriesOne, seriesTwo)
            println("Total penalty coefficient: ${result.totalCost}")
            print("Alignment: ")
            println(result.alignMatrix)
            for ((i , j) in result.alignMatrix.indices) {
                if (result.alignMatrix[i, j] > 0.0) {
                    print("[$i, $j] ")
                }
            }
        }
    }
}


