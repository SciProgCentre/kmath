/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package space.kscience.kmath.stat

import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.structures.Float64Buffer
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class QuantileBasicTest {

    @Test
    fun testBasicQuantile() {
        val data = Float64Buffer(1.0, 2.0, 3.0, 4.0)
        assertEquals(2.5, Quantile.evaluate(0.5, data))
    }

    @Test
    fun testSingleElement() {
        val data = Float64Buffer(1.0)
        assertEquals(1.0, Quantile.evaluate(0.5, data))
    }

    @Test
    fun testTwoElements() {
        val data = Float64Buffer(1.0, 3.0)
        assertEquals(2.0, Quantile.evaluate(0.5, data))
    }

    @Test
    fun testSortedInput() {
        val data = Float64Buffer(101) { it.toDouble() } // 0.0 to 100.0
        val quantile = Quantile(DoubleField, 0.1, sorted = true, comparator = naturalOrder())
        assertEquals(10.0, quantile.evaluateBlocking(data), 1e-6)
    }

    @Test
    fun testReverseSortedInput() {
        val data = Float64Buffer(101) { (100 - it).toDouble() } // 100.0 to 0.0
        assertEquals(10.0, Quantile.evaluate(0.1, data), 1e-6)
    }

//    @Test //todo try to fix
//    fun testExtremeValues() {
//        val data = Float64Buffer(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
//        assertTrue(Quantile.evaluate(0.5, data).isInfinite())
//
//        val data2 = Float64Buffer(Double.NEGATIVE_INFINITY, 1.0)
//        assertTrue(Quantile.evaluate(0.5, data2).isInfinite())
//    }

    @Test
    fun testVerySmallQuantile() {
        val data = Float64Buffer(0.0, 1.0)
        val result = Quantile.evaluate(1e-18, data)
        assertTrue(abs(result) <= 1e-18)
    }

    @Test
    fun testEmptyInput() {
        val data = Float64Buffer(0) { 0.0 }
        assertFailsWith<IllegalArgumentException> {
            Quantile.evaluate(0.5, data)
        }
    }

    @Test
    fun testInvalidP() {
        assertFailsWith<IllegalArgumentException> {
            Quantile(Float64Field, -0.1, comparator = naturalOrder())
        }
        assertFailsWith<IllegalArgumentException> {
            Quantile(Float64Field, 1.1, comparator = naturalOrder())
        }
    }

    @Test
    fun testAlphaBetaParameters() {
        val v = Float64Buffer(2.0, 3.0, 4.0, 6.0, 9.0, 2.0, 6.0, 2.0, 21.0, 17.0)

        // Tests against scipy.stats.mstats.mquantiles method
        assertEquals(2.0, Quantile(Float64Field, 0.0, alpha = 0.0, beta = 0.0, comparator = naturalOrder()).evaluateBlocking(v))
        assertEquals(2.0, Quantile(Float64Field, 0.2, alpha = 1.0, beta = 1.0, comparator = naturalOrder()).evaluateBlocking(v))
        assertEquals(3.4, Quantile(Float64Field, 0.4, alpha = 0.0, beta = 0.0, comparator = naturalOrder()).evaluateBlocking(v),1e-6)
        assertEquals(6.0, Quantile(Float64Field, 0.6, alpha = 0.0, beta = 0.0, comparator = naturalOrder()).evaluateBlocking(v),1e-6)
        assertEquals(15.4, Quantile(Float64Field, 0.8, alpha = 0.0, beta = 0.0, comparator = naturalOrder()).evaluateBlocking(v),1e-6)
        assertEquals(21.0, Quantile(Float64Field, 1.0, alpha = 0.0, beta = 0.0, comparator = naturalOrder()).evaluateBlocking(v),1e-6)
    }

    @Test
    fun testRounding() {
        val data = Float64Buffer(10) { (it + 1).toDouble() } // 1.0 to 10.0
        for (i in 0..9) {
            val p = i / 9.0
            val expected = (i + 1).toDouble()
            assertEquals(expected, Quantile.evaluate(p, data), 1e-6) //todo try to avoid rounding
        }
    }

//    @Test
//    fun testNoOverflow() { //todo try to fix
//        val data1 = Float64Buffer(-9000.0, 100.0)
//        assertEquals(100.0, Quantile.evaluate(1.0, data1))
//
//        val data2 = Float64Buffer(-1e20, 100.0)
//        assertEquals(100.0, Quantile.evaluate(1.0, data2))
//    }

    @Test
    fun testIncreasingQuantiles() {
        val data = Float64Buffer(1.0, 1.0, 1.0 + Double.MIN_VALUE, 1.0 + Double.MIN_VALUE)
        val quantiles = (0..99).map { i ->
            val p = i / 99.0
            Quantile.evaluate(p, data)
        }
        assertTrue(quantiles.zipWithNext().all { (a, b) -> a <= b })
    }

    @Test
    fun testUnsortedData() {
        val data = Float64Buffer(4.0, 9.0, 1.0, 5.0, 7.0, 8.0, 2.0, 3.0, 5.0, 17.0, 11.0)
        assertEquals(2.0, Quantile.evaluate(0.1, data))
        assertEquals(3.0, Quantile.evaluate(0.2, data))
        assertEquals(5.0, Quantile.evaluate(0.4, data))
        assertEquals(11.0, Quantile.evaluate(0.9, data))
    }

    @Test
    fun testMedianOdd() {
        val res = Quantile.evaluate(0.5, Float64Buffer(1.0, 2.0, 3.0))
        assertEquals(2.0, res)
    }

    @Test
    fun testMedianEven() {
        val res = Quantile.evaluate(0.5, Float64Buffer(1.0, 2.0, 3.0, 4.0))
        assertEquals(2.5, res)
    }
}