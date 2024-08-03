/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers
import space.kscience.kmath.distributions.NormalDistribution
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.random.DefaultGenerator
import space.kscience.kmath.stat.invoke
import space.kscience.kmath.stat.mean
import kotlin.math.exp
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals

class TestMetropolisHastingsSampler {

    @Test
    fun samplingNormalTest() {
        fun normalDist1(arg : Double) = NormalDistribution(0.5, 1.0).probability(arg)
        var sampler = MetropolisHastingsSampler(::normalDist1, proposalStd = 1.0)
        var sampledValues = sampler.sample(DefaultGenerator()).nextBufferBlocking(1_000_000)

         assertEquals(0.5, Float64Field.mean(sampledValues), 1e-2)

        fun normalDist2(arg : Double) = NormalDistribution(68.13, 1.0).probability(arg)
        sampler = MetropolisHastingsSampler(::normalDist2, initialState = 63.0, proposalStd = 1.0)
        sampledValues = sampler.sample(DefaultGenerator()).nextBufferBlocking(1_000_000)

        assertEquals(68.13, Float64Field.mean(sampledValues), 1e-2)
    }

    @Test
    fun samplingExponentialTest() {
        fun expDist(arg : Double, param : Double) : Double {
            if (arg < 0.0) { return 0.0 }
            return param * exp(-param * arg)
        }

        fun expDist1(arg : Double) = expDist(arg, 0.5)
        var sampler = MetropolisHastingsSampler(::expDist1, initialState = 2.0, proposalStd = 1.0)
        var sampledValues = sampler.sample(DefaultGenerator()).nextBufferBlocking(1_000_000)

        assertEquals(2.0, Float64Field.mean(sampledValues), 1e-2)

        fun expDist2(arg : Double) = expDist(arg, 2.0)
        sampler = MetropolisHastingsSampler(::expDist2, initialState = 9.0, proposalStd = 1.0)
        sampledValues = sampler.sample(DefaultGenerator()).nextBufferBlocking(1_000_000)

        assertEquals(0.5, Float64Field.mean(sampledValues), 1e-2)

    }

    @Test
    fun samplingRayleighTest() {
        fun rayleighDist(arg : Double, sigma : Double) : Double {
            if (arg < 0.0) { return 0.0 }

            val expArg = (arg / sigma).pow(2)
            return arg * exp(-expArg / 2.0) / sigma.pow(2)
        }

        fun rayleighDist1(arg : Double) = rayleighDist(arg, 1.0)
        var sampler = MetropolisHastingsSampler(::rayleighDist1, initialState = 2.0, proposalStd = 1.0)
        var sampledValues = sampler.sample(DefaultGenerator()).nextBufferBlocking(1_000_000)

        assertEquals(1.25, Float64Field.mean(sampledValues), 1e-2)

        fun rayleighDist2(arg : Double) = rayleighDist(arg, 2.0)
        sampler = MetropolisHastingsSampler(::rayleighDist2, proposalStd = 1.0)
        sampledValues = sampler.sample(DefaultGenerator()).nextBufferBlocking(10_000_000)

        assertEquals(2.5, Float64Field.mean(sampledValues), 1e-2)
    }
}