/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import kotlinx.coroutines.test.runTest
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class GeometricMeanTest {

    // Float64 tests
    @Test
    fun singleBlockingFloat64GeometricMean() = runTest {
        val res = Float64Field.geometricMean.evaluateBlocking(Float64Buffer(1.0, 8.0, 27.0))
        assertEquals(6.0, res)
    }

    @Test
    fun float64GeometricMeanWithSingleElement() = runTest {
        val res = Float64Field.geometricMean.evaluateBlocking(Float64Buffer(5.0))
        assertEquals(5.0, res, 1e-1)
    }

    @Test
    fun float64GeometricMeanWithNegativeNumbers() = runTest {
        assertFailsWith<ArithmeticException> {
            Float64Field.geometricMean.evaluateBlocking(Float64Buffer(1.0, -2.0, 3.0))
        }
    }

    @Test
    fun float64GeometricMeanWithZero() = runTest {
        val res = Float64Field.geometricMean.evaluateBlocking(Float64Buffer(1.0, 0.0, 3.0))
        assertEquals(0.0, res)
    }

    @Test
    fun float64GeometricMeanWithEmptyBuffer() = runTest {
        assertFailsWith<IllegalArgumentException> {
            Float64Field.geometricMean.evaluateBlocking(Float64Buffer())
        }
    }

    @Test
    fun float64GeometricMeanWithLargeNumbers() = runTest {
        val res = Float64Field.geometricMean.evaluateBlocking(Float64Buffer(1e6, 1e6, 1e6))
        assertEquals(1e6, res,1e-1)
    }

    // Float32 tests
    @Test
    fun singleBlockingFloat32GeometricMean() = runTest {
        val res = Float32Field.geometricMean.evaluateBlocking(Float32Buffer(1f, 8f, 27f))
        assertEquals(6f, res)
    }

    @Test
    fun float32GeometricMeanWithSingleElement() = runTest {
        val res = Float32Field.geometricMean.evaluateBlocking(Float32Buffer(5f))
        assertEquals(5f, res)
    }

    @Test
    fun float32GeometricMeanWithNegativeNumbers() = runTest {
        assertFailsWith<ArithmeticException> {
            Float32Field.geometricMean.evaluateBlocking(Float32Buffer(1f, -2f, 3f))
        }
    }

    @Test
    fun float32GeometricMeanWithZero() = runTest {
        val res = Float32Field.geometricMean.evaluateBlocking(Float32Buffer(1f, 0f, 3f))
        assertEquals(0f, res)
    }

    @Test
    fun float32GeometricMeanWithEmptyBuffer() = runTest {
        assertFailsWith<IllegalArgumentException> {
            Float32Field.geometricMean.evaluateBlocking(Float32Buffer())
        }
    }

    @Test
    fun float32GeometricMeanWithPrecision() = runTest {
        val res = Float32Field.geometricMean.evaluateBlocking(Float32Buffer(1f, 2f, 4f))
        assertEquals(2f, res) // 2^3 = 8, 8^(1/3) = 2
    }

    // Extension property tests
    @Test
    fun float64ExtensionPropertyWorks() = runTest {
        val res = Float64Field.geometricMean(Float64Buffer(1.0, 4.0, 16.0))
        assertEquals(4.0, res,1e-1)
    }

    @Test
    fun float32ExtensionPropertyWorks() = runTest {
        val res = Float32Field.geometricMean(Float32Buffer(1f, 4f, 16f))
        assertEquals(4f, res, 1e-1f)
    }
}