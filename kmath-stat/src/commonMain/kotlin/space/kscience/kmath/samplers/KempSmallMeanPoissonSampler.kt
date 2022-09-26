/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import space.kscience.kmath.chains.BlockingIntChain
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.stat.Sampler
import space.kscience.kmath.structures.IntBuffer
import kotlin.math.exp

/**
 * Sampler for the Poisson distribution.
 * * Kemp, A, W, (1981) Efficient Generation of Logarithmically Distributed Pseudo-Random Variables. Journal of the Royal Statistical Society. Vol. 30, No. 3, pp. 249-253.
 * This sampler is suitable for mean < 40. For large means, LargeMeanPoissonSampler should be used instead.
 *
 * Note: The algorithm uses a recurrence relation to compute the Poisson probability and a rolling summation for the cumulative probability. When the mean is large the initial probability (Math.exp(-mean)) is zero and an exception is raised by the constructor.
 *
 * Sampling uses 1 call to UniformRandomProvider.nextDouble(). This method provides an alternative to the SmallMeanPoissonSampler for slow generators of double.
 *
 * Based on Commons RNG implementation.
 * See [https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/KempSmallMeanPoissonSampler.html].
 */
public class KempSmallMeanPoissonSampler internal constructor(
    private val p0: Double,
    private val mean: Double,
) : Sampler<Int> {
    override fun sample(generator: RandomGenerator): BlockingIntChain = object : BlockingIntChain {
        override fun nextBlocking(): Int {
            //TODO move to nextBufferBlocking
            // Note on the algorithm:
            // - X is the unknown sample deviate (the output of the algorithm)
            // - x is the current value from the distribution
            // - p is the probability of the current value x, p(X=x)
            // - u is effectively the cumulative probability that the sample X
            //   is equal or above the current value x, p(X>=x)
            // So if p(X>=x) > p(X=x) the sample must be above x, otherwise it is x
            var u = generator.nextDouble()
            var x = 0
            var p = p0

            while (u > p) {
                u -= p
                // Compute the next probability using a recurrence relation.
                // p(x+1) = p(x) * mean / (x+1)
                p *= mean / ++x
                // The algorithm listed in Kemp (1981) does not check that the rolling probability
                // is positive. This check is added to ensure no errors when the limit of the summation
                // 1 - sum(p(x)) is above 0 due to cumulative error in floating point arithmetic.
                if (p == 0.0) return x
            }

            return x
        }

        override fun nextBufferBlocking(size: Int): IntBuffer = IntBuffer(size) { nextBlocking() }

        override suspend fun fork(): BlockingIntChain = sample(generator.fork())
    }

    override fun toString(): String = "Kemp Small Mean Poisson deviate"
}

public fun KempSmallMeanPoissonSampler(mean: Double): KempSmallMeanPoissonSampler {
    require(mean > 0) { "Mean is not strictly positive: $mean" }
    val p0 = exp(-mean)
    // Probability must be positive. As mean increases, p(0) decreases.
    require(p0 > 0) { "No probability for mean: $mean" }
    return KempSmallMeanPoissonSampler(p0, mean)
}
