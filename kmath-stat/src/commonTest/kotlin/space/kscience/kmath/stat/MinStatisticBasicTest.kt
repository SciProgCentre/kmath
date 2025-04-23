/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.structures.Float64Buffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.runTest
import space.kscience.kmath.operations.Int32Ring
import space.kscience.kmath.operations.Int64Ring
import space.kscience.kmath.structures.Int32Buffer
import space.kscience.kmath.structures.Int64Buffer

internal class MinStatisticBasicTest {

    // Int32 Tests
    @Test
    fun singleBlockingInt32Min() = runTest {
        val res = Int32Ring.min(Int32Buffer(1, 2, 3))
        assertEquals(1, res)
    }

    @Test
    fun int32MinWithNegativeValues() = runTest {
        val res = Int32Ring.min(Int32Buffer(-1, -5, -3))
        assertEquals(-5, res)
    }

    @Test
    fun int32SingleElement() = runTest {
        val res = Int32Ring.min(Int32Buffer(42))
        assertEquals(42, res)
    }

    // Int64 Tests
    @Test
    fun singleBlockingInt64Min() = runTest {
        val res = Int64Ring.min(Int64Buffer(1L, 2L, 3L))
        assertEquals(1L, res)
    }

    // Float64 Tests
    @Test
    fun singleBlockingFloat64Min() = runTest {
        val res = Float64Field.min(Float64Buffer(1.0, 2.5, 3.1))
        assertEquals(1.0, res)
    }

    @Test
    fun float64MinWithNaN() = runTest {
        val res = Float64Field.min(Float64Buffer(Double.NEGATIVE_INFINITY, Double.MIN_VALUE, Double.NaN))
        assertEquals(Double.NEGATIVE_INFINITY, res)
    }

    @Test
    fun float64MinWithSpecialValues() = runTest {
        val res = Float64Field.min(Float64Buffer(Double.NEGATIVE_INFINITY, Double.MIN_VALUE, 0.0))
        assertEquals(Double.NEGATIVE_INFINITY, res)
    }

    // Edge Cases
    @Test
    fun emptyBufferThrowsException() = runTest {
        assertFailsWith<IllegalArgumentException> {
            Float64Field.min(Float64Buffer())
        }
    }

    @Test
    fun allEqualValuesReturnsSame() = runTest {
        val res = Int32Ring.min(Int32Buffer(5, 5, 5))
        assertEquals(5, res)
    }

    @Test
    fun extremeValueAtBufferStart() = runTest {
        val res = Float64Field.min(Float64Buffer(Double.MIN_VALUE, 1.0, 2.0))
        assertEquals(Double.MIN_VALUE, res)
    }
}