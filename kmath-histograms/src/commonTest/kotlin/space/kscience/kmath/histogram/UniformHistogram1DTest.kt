/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.histogram

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import space.kscience.kmath.distributions.NormalDistribution
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.stat.RandomGenerator
import space.kscience.kmath.stat.nextBuffer
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class, UnstableKMathAPI::class)
internal class UniformHistogram1DTest {
    @Test
    fun normal() = runTest {
        val generator = RandomGenerator.default(123)
        val distribution = NormalDistribution(0.0, 1.0)
        with(Histogram.uniform1D(DoubleField, 0.1)) {
            val h1 = produce(distribution.nextBuffer(generator, 10000))

            val h2 = produce(distribution.nextBuffer(generator, 50000))

            val h3 = h1 + h2

            assertEquals(60000, h3.bins.sumOf { it.binValue }.toInt())
        }
    }
}