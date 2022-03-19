/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.histogram

import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertTrue

class TreeHistogramTest {

    @Test
    fun normalFill() {
        val histogram = TreeHistogramSpace.uniform(0.1) {
            repeat(100_000) {
                putValue(Random.nextDouble())
            }
        }

        assertTrue { histogram.bins.count() > 10 }
    }
}