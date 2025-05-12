/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.structures.Float64Buffer
import kotlin.test.Test
import kotlin.test.assertEquals

class MeanStatisticBasicTest {
    companion object {
        val float64Sample = RandomGenerator.default(123).nextDoubleBuffer(100)
    }

    @Test
    fun meanFloat64() {
        assertEquals(0.488, Float64Field.mean(float64Sample), 0.0002)
    }

    @Test
    fun numericalStability(){
        val data = doubleArrayOf(1.0e4, 1.0, 1.0, 1.0, 1.0);
        assertEquals(2000.8, Float64Field.mean.evaluateBlocking(Float64Buffer(data)));
    }

    @Test
    fun compositeWithDifferentlySizedChunks() = runTest {
        val flow: Flow<Float64Buffer> = flow {
            emit(Float64Buffer(doubleArrayOf(1.0, 2.0)))
            emit(Float64Buffer(doubleArrayOf(1.0, 2.0, 4.0)))
        }
        val average = Float64Field.mean
            .flow(flow)
            .take(100)
            .last()
        // (1+2+1+2+4)/5 = 10/5 = 2
        assertEquals(2.0, average, 1e-6)
    }

}