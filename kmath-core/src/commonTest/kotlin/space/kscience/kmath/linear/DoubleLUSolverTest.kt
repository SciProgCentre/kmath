/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.algebra
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(PerformancePitfall::class)
fun <T : Any> assertMatrixEquals(expected: StructureND<T>, actual: StructureND<T>) {
    assertTrue { StructureND.contentEquals(expected, actual) }
}

@UnstableKMathAPI
class DoubleLUSolverTest {

    @Test
    fun testInvertOne() = Double.algebra.linearSpace.run{
        val matrix = one(2, 2)
        val inverted = lupSolver().inverse(matrix)
        assertMatrixEquals(matrix, inverted)
    }

    @Test
    fun testDecomposition() = Double.algebra.linearSpace.run {
        val matrix = matrix(2, 2)(
            3.0, 1.0,
            2.0, 3.0
        )

        val lup = lup(matrix)

        //Check determinant
        assertEquals(7.0, lup.determinant)

        assertMatrixEquals(lup.p dot matrix, lup.l dot lup.u)
    }

    @Test
    fun testInvert()  = Double.algebra.linearSpace.run{
        val matrix = matrix(2, 2)(
            3.0, 1.0,
            1.0, 3.0
        )

        val inverted = lupSolver().inverse(matrix)

        val expected = matrix(2, 2)(
            0.375, -0.125,
            -0.125, 0.375
        )

        assertMatrixEquals(expected, inverted)
    }
}
