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

internal class MaxStatisticBasicTest {

    // Int32 Tests
    @Test
    fun singleBlockingInt32Max() = runTest {
        val res = Int32Ring.max(Int32Buffer(1, 2, 3))
        assertEquals(3, res)
    }

    @Test
    fun int32MaxWithNegativeValues() = runTest {
        val res = Int32Ring.max(Int32Buffer(-1, -5, -3))
        assertEquals(-1, res)
    }

    @Test
    fun int32SingleElement() = runTest {
        val res = Int32Ring.max(Int32Buffer(42))
        assertEquals(42, res)
    }

    // Int64 Tests
    @Test
    fun singleBlockingInt64Max() = runTest {
        val res = Int64Ring.max(Int64Buffer(1L, 2L, 3L))
        assertEquals(3L, res)
    }

    @Test
    fun int64MaxWithLargeValues() = runTest {
        val res = Int64Ring.max(Int64Buffer(Long.MAX_VALUE, Long.MIN_VALUE, 0L))
        assertEquals(Long.MAX_VALUE, res)
    }

    // Float64 Tests
    @Test
    fun singleBlockingFloat64Max() = runTest {
        val res = Float64Field.max(Float64Buffer(1.0, 2.5, 3.1))
        assertEquals(3.1, res)
    }

    @Test
    fun float64MaxWithSpecialValues() = runTest {
        val res = Float64Field.max(Float64Buffer(Double.POSITIVE_INFINITY, Double.MAX_VALUE, 0.0))
        assertEquals(Double.POSITIVE_INFINITY, res)
    }

    @Test
    fun float64MaxWithNaN() = runTest {
        val res = Float64Field.max(Float64Buffer(Double.POSITIVE_INFINITY, Double.MAX_VALUE, Double.NaN))
        assertEquals(Double.NaN, res)
    }

    // Edge Cases
    @Test
    fun emptyBufferThrowsException() = runTest {
        assertFailsWith<IllegalArgumentException> {
            Float64Field.max(Float64Buffer())
        }
    }

    @Test
    fun allEqualValuesReturnsSame() = runTest {
        val res = Int32Ring.max(Int32Buffer(5, 5, 5))
        assertEquals(5, res)
    }

    @Test
    fun extremeValueAtBufferStart() = runTest {
        val res = Float64Field.max(Float64Buffer(Double.MAX_VALUE, 1.0, 2.0))
        assertEquals(Double.MAX_VALUE, res)
    }

    @Test
    fun extremeValueAtBufferEnd() = runTest {
        val res = Int64Ring.max(Int64Buffer(1L, 2L, Long.MAX_VALUE))
        assertEquals(Long.MAX_VALUE, res)
    }
}