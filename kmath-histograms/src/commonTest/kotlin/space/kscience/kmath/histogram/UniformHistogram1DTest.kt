/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.histogram

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.distributions.NormalDistribution
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.stat.nextBuffer
import kotlin.native.concurrent.ThreadLocal
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class, UnstableKMathAPI::class)
internal class UniformHistogram1DTest {

    @Test
    fun normal() = runTest {
        val distribution = NormalDistribution(0.0, 1.0)
        with(Histogram.uniform1D(DoubleField, 0.1)) {
            val h1 = produce(distribution.nextBuffer(generator, 10000))

            val h2 = produce(distribution.nextBuffer(generator, 50000))

            val h3 = h1 + h2

            assertEquals(60000, h3.bins.sumOf { it.binValue }.toInt())
        }
    }

    @Test
    fun rebinDown() = runTest {
        val h1 = Histogram.uniform1D(DoubleField, 0.01).produce(generator.nextDoubleBuffer(10000))
        val h2 = Histogram.uniform1D(DoubleField, 0.03).produceFrom(h1)

        assertEquals(10000, h2.bins.sumOf { it.binValue }.toInt())
    }

    @Test
    fun rebinUp() = runTest {
        val h1 = Histogram.uniform1D(DoubleField, 0.03).produce(generator.nextDoubleBuffer(10000))
        val h2 = Histogram.uniform1D(DoubleField, 0.01).produceFrom(h1)

        assertEquals(10000, h2.bins.sumOf { it.binValue }.toInt())
    }

    @ThreadLocal
    companion object {
        private val generator = RandomGenerator.default(123)
    }
}