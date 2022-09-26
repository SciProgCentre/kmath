/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.samplers.GaussianSampler

internal class CommonsDistributionsTest {
    @Test
    fun testNormalDistributionSuspend() = runBlocking {
        val distribution = GaussianSampler(7.0, 2.0)
        val generator = RandomGenerator.default(1)
        val sample = distribution.sample(generator).nextBuffer(1000)
        Assertions.assertEquals(7.0, Mean.evaluate(sample), 0.2)
    }

    @Test
    fun testNormalDistributionBlocking() {
        val distribution = GaussianSampler(7.0, 2.0)
        val generator = RandomGenerator.default(1)
        val sample = distribution.sample(generator).nextBufferBlocking(1000)
        Assertions.assertEquals(7.0, Mean.evaluate(sample), 0.2)
    }
}
