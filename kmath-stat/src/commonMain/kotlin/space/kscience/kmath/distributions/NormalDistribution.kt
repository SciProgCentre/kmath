/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.distributions

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.internal.InternalErf
import space.kscience.kmath.samplers.GaussianSampler
import space.kscience.kmath.samplers.NormalizedGaussianSampler
import space.kscience.kmath.samplers.ZigguratNormalizedGaussianSampler
import space.kscience.kmath.stat.RandomGenerator
import kotlin.math.*

/**
 * Implements [Distribution1D] for the normal (gaussian) distribution.
 */
public class NormalDistribution(public val sampler: GaussianSampler) : Distribution1D<Double> {

    override fun probability(arg: Double): Double {
        val x1 = (arg - sampler.mean) / sampler.standardDeviation
        return exp(-0.5 * x1 * x1 - (ln(sampler.standardDeviation) + 0.5 * ln(2 * PI)))
    }

    override fun sample(generator: RandomGenerator): Chain<Double> = sampler.sample(generator)

    override fun cumulative(arg: Double): Double {
        val dev = arg - sampler.mean

        return when {
            abs(dev) > 40 * sampler.standardDeviation -> if (dev < 0) 0.0 else 1.0
            else -> 0.5 * InternalErf.erfc(-dev / (sampler.standardDeviation * SQRT2))
        }
    }

    private companion object {
        private val SQRT2 = sqrt(2.0)
    }
}

public fun NormalDistribution(
    mean: Double,
    standardDeviation: Double,
    normalized: NormalizedGaussianSampler = ZigguratNormalizedGaussianSampler,
): NormalDistribution = NormalDistribution(GaussianSampler(mean, standardDeviation, normalized))
