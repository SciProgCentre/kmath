/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import space.kscience.kmath.chains.BlockingDoubleChain
import space.kscience.kmath.stat.RandomGenerator
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.nextDoubleBuffer
import kotlin.math.*

/**
 * [Box-Muller algorithm](https://en.wikipedia.org/wiki/Box%E2%80%93Muller_transform) for sampling from a Gaussian
 * distribution.
 *
 * Based on Commons RNG implementation.
 * See [https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/BoxMullerNormalizedGaussianSampler.html].
 */

public object BoxMullerSampler : NormalizedGaussianSampler {
    override fun sample(generator: RandomGenerator): BlockingDoubleChain = object : BlockingDoubleChain {
        var state = Double.NaN

        override fun nextBufferBlocking(size: Int): DoubleBuffer {
            val xs = nextDoubleBuffer(size)
            val ys = nextDoubleBuffer(size)

            return DoubleBuffer(size) { index ->
                if (state.isNaN()) {
                    // Generate a pair of Gaussian numbers.
                    val x = xs[index]
                    val y = ys[index]
                    val alpha = 2 * PI * x
                    val r = sqrt(-2 * ln(y))

                    // Keep second element of the pair for next invocation.
                    state = r * sin(alpha)

                    // Return the first element of the generated pair.
                    r * cos(alpha)
                } else {
                    // Use the second element of the pair (generated at the
                    // previous invocation).
                    state.also {
                        // Both elements of the pair have been used.
                        state = Double.NaN
                    }
                }
            }
        }


        override suspend fun fork(): BlockingDoubleChain = sample(generator.fork())
    }

}
