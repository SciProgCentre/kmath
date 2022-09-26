/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import space.kscience.kmath.chains.BlockingDoubleChain
import space.kscience.kmath.chains.map
import space.kscience.kmath.random.RandomGenerator

/**
 * Sampling from a Gaussian distribution with given mean and standard deviation.
 *
 * Based on Commons RNG implementation.
 * See [https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/GaussianSampler.html].
 *
 * @property mean the mean of the distribution.
 * @property standardDeviation the variance of the distribution.
 */
public class GaussianSampler(
    public val mean: Double,
    public val standardDeviation: Double,
    private val normalized: NormalizedGaussianSampler = BoxMullerSampler,
) : BlockingDoubleSampler {

    init {
        require(standardDeviation > 0.0) { "standard deviation is not strictly positive: $standardDeviation" }
    }

    override fun sample(generator: RandomGenerator): BlockingDoubleChain = normalized
        .sample(generator)
        .map { standardDeviation * it + mean }

    override fun toString(): String = "N($mean, $standardDeviation)"

    public companion object
}
