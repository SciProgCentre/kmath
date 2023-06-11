/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.distributions

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.operations.DoubleField.pow
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.samplers.GaussianSampler
import space.kscience.kmath.samplers.InternalErf
import space.kscience.kmath.samplers.NormalizedGaussianSampler
import space.kscience.kmath.samplers.ZigguratNormalizedGaussianSampler
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

    public companion object {
        private val SQRT2 = sqrt(2.0)

        /**
         * Zelen & Severo approximation for the standard normal CDF.
         * The error upper boundary by 7.5 * 10e-8.
         */
        public fun zSNormalCDF(x: Double): Double {
            val t = 1 / (1 + 0.2316419 * abs(x))
            val sum = 0.319381530 * t -
                    0.356563782 * t.pow(2) +
                    1.781477937 * t.pow(3) -
                    1.821255978 * t.pow(4) +
                    1.330274429 * t.pow(5)
            val temp = sum * exp(-abs(x).pow(2) / 2) / (2 * PI).pow(0.5)
            return if (x >= 0) 1 - temp else temp
        }
    }
}

public fun NormalDistribution(
    mean: Double,
    standardDeviation: Double,
    normalized: NormalizedGaussianSampler = ZigguratNormalizedGaussianSampler,
): NormalDistribution = NormalDistribution(GaussianSampler(mean, standardDeviation, normalized))
