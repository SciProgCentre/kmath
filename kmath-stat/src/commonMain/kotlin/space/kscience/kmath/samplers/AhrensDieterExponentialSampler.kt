/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import space.kscience.kmath.chains.BlockingDoubleChain
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.stat.Sampler
import space.kscience.kmath.structures.DoubleBuffer
import kotlin.math.ln
import kotlin.math.pow

/**
 * Sampling from an [exponential distribution](http://mathworld.wolfram.com/ExponentialDistribution.html).
 *
 * Based on Commons RNG implementation.
 * See [https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/AhrensDieterExponentialSampler.html].
 */
public class AhrensDieterExponentialSampler(public val mean: Double) : Sampler<Double> {

    init {
        require(mean > 0) { "mean is not strictly positive: $mean" }
    }

    override fun sample(generator: RandomGenerator): BlockingDoubleChain = object : BlockingDoubleChain {
        override fun nextBlocking(): Double {
            // Step 1:
            var a = 0.0
            var u = generator.nextDouble()

            // Step 2 and 3:
            while (u < 0.5) {
                a += EXPONENTIAL_SA_QI[0]
                u *= 2.0
            }

            // Step 4 (now u >= 0.5):
            u += u - 1
            // Step 5:
            if (u <= EXPONENTIAL_SA_QI[0]) return mean * (a + u)
            // Step 6:
            var i = 0 // Should be 1, be we iterate before it in while using 0.
            var u2 = generator.nextDouble()
            var umin = u2

            // Step 7 and 8:
            do {
                ++i
                u2 = generator.nextDouble()
                if (u2 < umin) umin = u2
                // Step 8:
            } while (u > EXPONENTIAL_SA_QI[i]) // Ensured to exit since EXPONENTIAL_SA_QI[MAX] = 1.

            return mean * (a + umin * EXPONENTIAL_SA_QI[0])
        }

        override fun nextBufferBlocking(size: Int): DoubleBuffer = DoubleBuffer(size) { nextBlocking() }

        override suspend fun fork(): BlockingDoubleChain = sample(generator.fork())
    }

    public companion object {
        private val EXPONENTIAL_SA_QI by lazy {
            val ln2 = ln(2.0)
            var qi = 0.0

            DoubleArray(16) { i ->
                qi += ln2.pow(i + 1.0) / InternalUtils.factorial(i + 1)
                qi
            }
        }
    }

}

