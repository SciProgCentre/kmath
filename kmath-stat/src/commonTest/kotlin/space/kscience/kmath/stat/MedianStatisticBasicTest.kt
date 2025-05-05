/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.structures.slice
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(UnstableKMathAPI::class)
class MedianStatisticBasicTest {
    companion object {
        val float64Sample = RandomGenerator.default(123).nextDoubleBuffer(100)
    }

    @Test
    fun medianFloat64() {
        assertEquals(0.508, Float64Field.median(float64Sample), 0.0005)
        assertEquals(0.5055, Float64Field.median(float64Sample.slice { 0..<last }), 0.0005)
    }

}