/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.histogram

import org.junit.jupiter.api.Test
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.real.step
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TreeHistogramTest {

    @Test
    fun normalFill() {
        val random  = Random(123)
        val histogram = Histogram.custom1D(DoubleField, 0.0..1.0 step 0.1).produce {
            repeat(100_000) {
                putValue(random.nextDouble())
            }
        }

        assertTrue { histogram.bins.count() > 8}
        assertEquals(100_000, histogram.bins.sumOf { it.binValue }.toInt())
    }
}