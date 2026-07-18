/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.operations.bufferAlgebra
import space.kscience.kmath.structures.asBuffer
import space.kscience.kmath.structures.toDoubleBuffer
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals


class DTWTest {

    @Test
    fun someData() {
        val firstSequence: DoubleArray = doubleArrayOf(0.0, 2.0, 3.0, 1.0, 2.0, 3.0, 1.0, 1.0)
        val secondSequence: DoubleArray = doubleArrayOf(0.0, 2.0, 3.0, 1.0, 2.0, 3.0, 1.0, 1.0)

        val seriesOne = firstSequence.asBuffer()
        val seriesTwo = secondSequence.asBuffer()

        val result = DoubleFieldOpsND.dynamicTimeWarping(seriesOne, seriesTwo)
        assertEquals(result.totalCost, 0.0)
    }

    @Test
    fun pathTest() = with(Double.algebra.bufferAlgebra.seriesAlgebra()) {
        val s1 = series(10) { DoubleField.sin(2 * PI * it / 100)}.toDoubleBuffer()
        val s2 = series(20) {sin(PI * it / 100)}.toDoubleBuffer()
        val s3 = series(20) {sin(PI * it / 100  + 2 * PI)}.toDoubleBuffer()

        val resS1S2 = DoubleFieldOpsND.dynamicTimeWarping(s1, s2).alignMatrix
        var pathLengthS1S2 = 0
        for ((i,j) in resS1S2.indices) {
            if (resS1S2[i, j] > 0.0) {
                ++pathLengthS1S2
            }
        }

        val resS1S3 = DoubleFieldOpsND.dynamicTimeWarping(s1, s3).alignMatrix
        var pathLengthS1S3 = 0
        for ((i,j) in resS1S3.indices) {
            if (resS1S2[i, j] > 0.0) {
                ++pathLengthS1S3
            }
        }
        assertEquals(pathLengthS1S3, pathLengthS1S2)
    }

}
