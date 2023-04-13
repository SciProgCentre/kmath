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
    fun volatileData() {
        with(Double.algebra.bufferAlgebra.seriesAlgebra()) {
            val volatileData = series(10) { sin(PI * it + PI/2) + 1.0}
            val resultHomo = varianceRatioTest(volatileData, 2, homoscedastic = true)
            assertEquals(0.0, resultHomo.varianceRatio, 1e-6)
            // homoscedastic zScore
            assertEquals(-3.162277, resultHomo.zScore, 1e-6)
            val resultHetero = varianceRatioTest(volatileData, 2, homoscedastic = false)
            // heteroscedastic zScore
            assertEquals(-3.535533, resultHetero.zScore, 1e-6)
        }
    }

    @Test
    fun negativeData() {
        with(Double.algebra.bufferAlgebra.seriesAlgebra()) {
            val volatileData = series(10) { sin(PI * it)}
            val resultHomo = varianceRatioTest(volatileData, 2, homoscedastic = true)
            assertEquals(1.142857, resultHomo.varianceRatio, 1e-6)
            // homoscedastic zScore
            assertEquals(0.451753, resultHomo.zScore, 1e-6)
            val resultHetero = varianceRatioTest(volatileData, 2, homoscedastic = false)
            // heteroscedastic zScore
            assertEquals(2.462591, resultHetero.zScore, 1e-6)
        }
    }

//    @Test
//    fun zeroVolatility() {
//        with(Double.algebra.bufferAlgebra.seriesAlgebra()) {
//            val volatileData = series(10) { 1.0 }
//            val result = varianceRatioTest(volatileData, 2, homoscedastic = true)
//        }
//    }
}