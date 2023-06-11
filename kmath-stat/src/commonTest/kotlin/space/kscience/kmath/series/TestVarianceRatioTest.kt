/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.operations.algebra
import space.kscience.kmath.operations.bufferAlgebra
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class TestVarianceRatioTest {

    @Test
    fun monotonicData() {
        with(Double.algebra.bufferAlgebra.seriesAlgebra()) {
            val monotonicData = series(10) { it * 1.0 }
            val resultHomo = varianceRatioTest(monotonicData, 2, homoscedastic = true)
            assertEquals(1.818181, resultHomo.varianceRatio, 1e-6)
            // homoscedastic zScore
            assertEquals(2.587318, resultHomo.zScore, 1e-6)
            assertEquals(.0096, resultHomo.pValue, 1e-4)
            val resultHetero = varianceRatioTest(monotonicData, 2, homoscedastic = false)
            // heteroscedastic zScore
            assertEquals(0.819424, resultHetero.zScore, 1e-6)
            assertEquals(.4125, resultHetero.pValue, 1e-4)
        }
    }

    @Test
    fun volatileData() {
        with(Double.algebra.bufferAlgebra.seriesAlgebra()) {
            val volatileData = series(10) { sin(PI * it + PI/2) + 1.0}
            val resultHomo = varianceRatioTest(volatileData, 2)
            assertEquals(0.0, resultHomo.varianceRatio, 1e-6)
            // homoscedastic zScore
            assertEquals(-3.162277, resultHomo.zScore, 1e-6)
            assertEquals(.0015, resultHomo.pValue, 1e-4)
            val resultHetero = varianceRatioTest(volatileData, 2, homoscedastic = false)
            // heteroscedastic zScore
            assertEquals(-1.0540925, resultHetero.zScore, 1e-6)
            assertEquals(.2918, resultHetero.pValue, 1e-4)
        }
    }

    @Test
    fun negativeData() {
        with(Double.algebra.bufferAlgebra.seriesAlgebra()) {
            val negativeData = series(10) { sin(it * 1.2)}
            val resultHomo = varianceRatioTest(negativeData, 3)
            assertEquals(1.240031, resultHomo.varianceRatio, 1e-6)
            // homoscedastic zScore
            assertEquals(0.509183, resultHomo.zScore, 1e-6)
            val resultHetero = varianceRatioTest(negativeData, 3, homoscedastic = false)
            // heteroscedastic zScore
            assertEquals(0.209202, resultHetero.zScore, 1e-6)
        }
    }

    @Test
    fun zeroVolatility() {
        with(Double.algebra.bufferAlgebra.seriesAlgebra()) {
            val zeroVolData = series(10) { 0.0 }
            val result = varianceRatioTest(zeroVolData, 4)
            assertEquals(1.0, result.varianceRatio, 1e-6)
            assertEquals(0.0, result.zScore, 1e-6)
            assertEquals(0.5, result.pValue, 1e-4)
        }
    }
}