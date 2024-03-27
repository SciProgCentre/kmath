/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.real

import space.kscience.kmath.linear.asMatrix
import space.kscience.kmath.linear.linearSpace
import space.kscience.kmath.linear.transposed
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.structures.Float64Buffer
import kotlin.test.Test
import kotlin.test.assertEquals

internal class DoubleVectorTest {
    @Test
    fun testSum() {
        val vector1 = Float64Buffer(5) { it.toDouble() }
        val vector2 = Float64Buffer(5) { 5 - it.toDouble() }
        val sum = vector1 + vector2
        assertEquals(5.0, sum[2])
    }

    @Test
    fun testVectorToMatrix() {
        val vector = Float64Buffer(5) { it.toDouble() }
        val matrix = vector.asMatrix()
        assertEquals(4.0, matrix[4, 0])
    }

    @Test
    fun testDot() = Double.algebra.linearSpace.run {
        val vector1 = Float64Buffer(5) { it.toDouble() }
        val vector2 = Float64Buffer(5) { 5 - it.toDouble() }
        val matrix1 = vector1.asMatrix()
        val matrix2 = vector2.asMatrix().transposed()
        val product = matrix1 dot matrix2
        assertEquals(5.0, product[1, 0])
        assertEquals(6.0, product[2, 2])
    }
}
