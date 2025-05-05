/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

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

}