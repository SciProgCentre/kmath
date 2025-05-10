/*
 * Copyright 2018-2024 KMath contributors.
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
        val mu = 7.0; val sigma = 2.0
        val distribution = GaussianSampler(mu, sigma)
        val generator = RandomGenerator.default(1)
        val sample = distribution.sample(generator).nextBuffer(1000)
        Assertions.assertEquals(mu, Mean.evaluate(sample), 0.2)
        Assertions.assertEquals(sigma, StandardDeviation.evaluate(sample), 0.2)
        //the first quartile (Q1)
        // For a normal distribution with mean \(\mu \) and standard deviation \(\sigma \), the Q1 is approximately \(\mu -0.675\sigma \)
        Assertions.assertEquals(mu-0.675*sigma, Quantile.evaluate(p=0.25, sample), 1e-1)
        //the third quartile (Q3)
        Assertions.assertEquals(mu+0.675*sigma, Quantile.evaluate(p=0.75, sample), 1e-1)
    }

    @Test
    fun testNormalDistributionBlocking() {
        val mu = 7.0; val sigma = 2.0
        val distribution = GaussianSampler(mu, sigma)
        val generator = RandomGenerator.default(1)
        val sample = distribution.sample(generator).nextBufferBlocking(1000)
        Assertions.assertEquals(mu, Mean.evaluate(sample), 0.2)
        Assertions.assertEquals(sigma, StandardDeviation.evaluate(sample), 0.2)
        //the first quartile (Q1)
        // For a normal distribution with mean \(\mu \) and standard deviation \(\sigma \), the Q1 is approximately \(\mu -0.675\sigma \)
        Assertions.assertEquals(mu-0.675*sigma, Quantile.evaluate(p=0.25, sample), 1e-1)
        //the third quartile (Q3)
        Assertions.assertEquals(mu+0.675*sigma, Quantile.evaluate(p=0.75, sample), 1e-1)
    }
}
