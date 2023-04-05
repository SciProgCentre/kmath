/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import space.kscience.kmath.chains.BlockingDoubleChain
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.structures.DoubleBuffer
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * [Marsaglia polar method](https://en.wikipedia.org/wiki/Marsaglia_polar_method) for sampling from a Gaussian
 * distribution with mean 0 and standard deviation 1. This is a variation of the algorithm implemented in
 * [BoxMullerNormalizedGaussianSampler].
 *
 * Based on Commons RNG implementation.
 * See [https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/MarsagliaNormalizedGaussianSampler.html]
 */
public object MarsagliaNormalizedGaussianSampler : NormalizedGaussianSampler {

    override fun sample(generator: RandomGenerator): BlockingDoubleChain = object : BlockingDoubleChain {
        var nextGaussian = Double.NaN

        override fun nextBlocking(): Double {
            return if (nextGaussian.isNaN()) {
                val alpha: Double
                var x: Double

                // Rejection scheme for selecting a pair that lies within the unit circle.
                while (true) {
                    // Generate a pair of numbers within [-1 , 1).
                    x = 2.0 * generator.nextDouble() - 1.0
                    val y = 2.0 * generator.nextDouble() - 1.0
                    val r2 = x * x + y * y

                    if (r2 < 1 && r2 > 0) {
                        // Pair (x, y) is within unit circle.
                        alpha = sqrt(-2 * ln(r2) / r2)
                        // Keep second element of the pair for next invocation.
                        nextGaussian = alpha * y
                        // Return the first element of the generated pair.
                        break
                    }
                    // Pair is not within the unit circle: Generate another one.
                }

                // Return the first element of the generated pair.
                alpha * x
            } else {
                // Use the second element of the pair (generated at the
                // previous invocation).
                val r = nextGaussian
                // Both elements of the pair have been used.
                nextGaussian = Double.NaN
                r
            }
        }

        override fun nextBufferBlocking(size: Int): DoubleBuffer = DoubleBuffer(size) { nextBlocking() }

        override suspend fun fork(): BlockingDoubleChain = sample(generator.fork())
    }
}
