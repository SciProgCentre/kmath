/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.structures.asBuffer
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

internal class StandardDeviationBlockingTest{
    private val doubleField = DoubleField
    private val tolerance = 1e-10

    @Test
    fun testEmptyData() {
        val stdDev = StandardDeviation(doubleField)
        val data = doubleArrayOf().asBuffer()
        assertEquals(0.0, stdDev.evaluateBlocking(data))
    }

    @Test
    fun testSingleElement() {
        val stdDev = StandardDeviation(doubleField)
        val data = doubleArrayOf(5.0).asBuffer()
        assertEquals(0.0, stdDev.evaluateBlocking(data))
    }

    @Test
    fun testTwoElementsUnbiased() {
        val stdDev = StandardDeviation(doubleField, isBiasCorrected = true)
        val data = doubleArrayOf(1.0, 3.0).asBuffer()
        // (1-2)² + (3-2)² = 2 / (2-1) = 2
        assertEquals(sqrt(2.0), stdDev.evaluateBlocking(data))
    }

    @Test
    fun testTwoElementsBiased() {
        val stdDev = StandardDeviation(doubleField, isBiasCorrected = false)
        val data = doubleArrayOf(1.0, 3.0).asBuffer()
        // (1-2)² + (3-2)² = 2 / 2 = 1
        assertEquals(1.0, stdDev.evaluateBlocking(data))
    }

    @Test
    fun testMultipleElementsUnbiased() {
        val stdDev = StandardDeviation(doubleField)
        val data = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0).asBuffer()
        // Mean = 3.0
        // Variance = (4+1+0+1+4)/4 = 2.5
        assertEquals(sqrt(2.5), stdDev.evaluateBlocking(data))
    }

    @Test
    fun testMultipleElementsBiased() {
        val stdDev = StandardDeviation(doubleField, isBiasCorrected = false)
        val data = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0).asBuffer()
        // Mean = 3.0
        // Variance = (4+1+0+1+4)/5 = 2.0
        assertEquals(sqrt(2.0), stdDev.evaluateBlocking(data))
    }

    @Test
    fun testLargeNumbersUnbiased() {
        val stdDev = StandardDeviation(doubleField)
        val data = doubleArrayOf(1e9, 1e9 + 2, 1e9 + 4).asBuffer()
        // Mean = 1e9 + 2
        // Variance = (4 + 0 + 4)/2 = 4
        assertEquals(2.0, stdDev.evaluateBlocking(data))
    }

    @Test
    fun testLargeNumbersBiased() {
        val stdDev = StandardDeviation(doubleField, isBiasCorrected = false)
        val data = doubleArrayOf(1e9, 1e9 + 2, 1e9 + 4).asBuffer()
        // Mean = 1e9 + 2
        // Variance = (4 + 0 + 4)/3 ≈ 2.666...
        assertEquals(sqrt(8.0 / 3.0), stdDev.evaluateBlocking(data), tolerance)
    }


    @Test
    fun testConstantValues() {
        val stdDev = StandardDeviation(doubleField)
        val data = doubleArrayOf(5.0, 5.0, 5.0, 5.0).asBuffer()
        assertEquals(0.0, stdDev.evaluateBlocking(data))
    }

    @Test
    fun testPrecisionWithSmallVariations() {
        val stdDev = StandardDeviation(doubleField)
        val data = doubleArrayOf(
            1.0000000001,
            1.0000000002,
            1.0000000003,
            1.0000000004,
            1.0000000005
        ).asBuffer()
        // Expected standardDeviation calculation
        val mean = 1.0000000003
        val expected = (
                (1.0000000001 - mean).pow(2) +
                        (1.0000000002 - mean).pow(2) +
                        (1.0000000003 - mean).pow(2) +
                        (1.0000000004 - mean).pow(2) +
                        (1.0000000005 - mean).pow(2)
                ) / 4
        assertEquals(sqrt(expected), stdDev.evaluateBlocking(data), 1e-20)
    }
}