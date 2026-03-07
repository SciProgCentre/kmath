/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.bufferAlgebra
import space.kscience.kmath.structures.Float64
import space.kscience.kmath.structures.Float64Buffer
import space.kscience.kmath.structures.toDoubleArray
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class TestSeries {


    val seriesAlgebra = Float64Field.bufferAlgebra.seriesAlgebra()

    @Test
    fun zip() = with(seriesAlgebra) {
        val s1 = series(100) { sin(2 * PI * it / 100) + 1.0 }

        val s2 = s1.slice(20..50).moveTo(40)

        val s3: Series<Float64> = s1.zip(s2) { l, r -> l + r } //s1 + s2

        assertEquals(s3[40], s1[40] + s1.get(index = 20))
    }


    /**
     * Test the slice functionality of SeriesAlgebra.
     */
    @Test
    fun `test slice within valid range`() = with(seriesAlgebra) {

        val originalSeries = Float64Buffer(10) { it.toDouble() }.asSeries(5)
        val slicedSeries = originalSeries.slice(6..8)

        assertEquals(3, slicedSeries.size)
        assertEquals(6, slicedSeries.position)
        assertEquals(1.0, slicedSeries[6])
        assertEquals(2.0, slicedSeries[7])
        assertEquals(3.0, slicedSeries[8])
    }

    @Test
    fun `test slice with full range`() = with(seriesAlgebra) {

        val originalSeries = Float64Buffer(10) { it.toDouble() }.asSeries(0)
        val slicedSeries = originalSeries.slice(0..9)

        assertEquals(10, slicedSeries.size)
        assertEquals(0, slicedSeries.position)
        for (i in 0..9) {
            assertEquals(i.toDouble(), slicedSeries[i])
        }
    }

    @Test
    fun `test slice with overlap at the beginning`() = with(seriesAlgebra) {

        val originalSeries = Float64Buffer(10) { it.toDouble() }.asSeries(5)
        val slicedSeries = originalSeries.slice(4..6)

        assertEquals(2, slicedSeries.size)
        assertEquals(5, slicedSeries.position)
        assertEquals(0.0, slicedSeries[5])
        assertEquals(1.0, slicedSeries[6])
    }

    @Test
    fun `test slice with overlap at the end`() = with(seriesAlgebra) {

        val originalSeries = Float64Buffer(10) { it.toDouble() }.asSeries(5)
        val slicedSeries = originalSeries.slice(12..15)

        assertEquals(3, slicedSeries.size)
        assertEquals(12, slicedSeries.position)
        assertEquals(7.0, slicedSeries[12])
        assertEquals(8.0, slicedSeries[13])
        assertEquals(9.0, slicedSeries[14])
    }

    @Test
    fun testSliceOutOfRange() = with(seriesAlgebra) {
        val originalSeries = Float64Buffer(10) { it.toDouble() }.asSeries(5)
        val slicedSeries = originalSeries.slice(15..20)
        assertEquals(0, slicedSeries.size)
    }


    @Test
    fun `test asBuffer with range inside series`() = with(seriesAlgebra) {
        val buffer = Float64Buffer(5) { (it + 1).toDouble() } // [1.0, 2.0, 3.0, 4.0, 5.0]
        val series = Series(buffer, position = 2) // Indices: [2, 3, 4, 5, 6]

        val outputBuffer = series.asBuffer(range = 3..5)


        assertEquals(3, outputBuffer.size)
        assertEquals(2.0, outputBuffer[0])
        assertEquals(3.0, outputBuffer[1])
        assertEquals(4.0, outputBuffer[2])
    }

    @Test
    fun `test asBuffer with range completely outside series`() = with(seriesAlgebra) {
        val buffer = Float64Buffer(5) { (it + 1).toDouble() } // [1.0, 2.0, 3.0, 4.0, 5.0]
        val series = Series(buffer, position = 2) // Indices: [2, 3, 4, 5, 6]

        val outputBuffer = series.asBuffer(missingValue = 0.0, range = 7..9)


        assertEquals(3, outputBuffer.size)
        assertEquals(0.0, outputBuffer[0])
        assertEquals(0.0, outputBuffer[1])
        assertEquals(0.0, outputBuffer[2])
    }

    @Test
    fun `test asBuffer with range partially overlapping series`() = with(seriesAlgebra) {
        val buffer = Float64Buffer(5) { (it + 1).toDouble() } // [1.0, 2.0, 3.0, 4.0, 5.0]
        val series = Series(buffer, position = 2) // Indices: [2, 3, 4, 5, 6]

        val outputBuffer = series.asBuffer(missingValue = 0.0, range = 5..8)


        assertEquals(4, outputBuffer.size)
        assertEquals(4.0, outputBuffer[0]) // Value from original series
        assertEquals(5.0, outputBuffer[1]) // Value from original series
        assertEquals(0.0, outputBuffer[2]) // Missing value
        assertEquals(0.0, outputBuffer[3]) // Missing value
    }

    @Test
    fun `test asBuffer with range equal to series boundaries`() = with(seriesAlgebra) {
        val buffer = Float64Buffer(5) { (it + 1).toDouble() } // [1.0, 2.0, 3.0, 4.0, 5.0]
        val series = Series(buffer, position = 2) // Indices: [2, 3, 4, 5, 6]

        val outputBuffer = series.asBuffer()

        assertEquals(7, outputBuffer.size)
        assertContentEquals(doubleArrayOf(0.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0), outputBuffer.toDoubleArray())
    }

    @Test
    fun `test asBuffer with larger range and default missing value`() = with(seriesAlgebra) {
        val buffer = Float64Buffer(3) { (it + 1).toDouble() } // [1.0, 2.0, 3.0]
        val series = Series(buffer, position = 2) // Indices: [2, 3, 4]

        val outputBuffer = series.asBuffer(missingValue = -1.0, range = 0..6)

        assertEquals(7, outputBuffer.size)
        assertEquals(-1.0, outputBuffer[0]) // Missing (before series)
        assertEquals(-1.0, outputBuffer[1]) // Missing (before series)
        assertEquals(1.0, outputBuffer[2]) // Original series (2..4)
        assertEquals(2.0, outputBuffer[3])
        assertEquals(3.0, outputBuffer[4])
        assertEquals(-1.0, outputBuffer[5]) // Missing (after series)
        assertEquals(-1.0, outputBuffer[6]) // Missing (after series)
    }
}