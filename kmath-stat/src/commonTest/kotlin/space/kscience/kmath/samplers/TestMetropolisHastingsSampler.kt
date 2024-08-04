/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import kotlinx.coroutines.test.runTest
import space.kscience.kmath.chains.discard
import space.kscience.kmath.chains.nextBuffer
import space.kscience.kmath.distributions.NormalDistribution
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.stat.invoke
import space.kscience.kmath.stat.mean
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class TestMetropolisHastingsSampler {

    data class TestSetup(val mean: Double, val startPoint: Double, val sigma: Double = 0.5)

    private val sample = 1e6.toInt()
    private val burnIn = sample / 5

    @Test
    fun samplingNormalTest() = runTest {
        val generator = RandomGenerator.default(1)

        listOf(
            TestSetup(0.5, 0.0),
            TestSetup(68.13, 60.0),
        ).forEach {
            val distribution = NormalDistribution(it.mean, 1.0)
            val sampler = MetropolisHastingsSampler
                .univariateNormal(it.startPoint, it.sigma, distribution::probability)
            val sampledValues = sampler.sample(generator).discard(burnIn).nextBuffer(sample)

            assertEquals(it.mean, Float64Field.mean(sampledValues), 1e-2)
        }
    }

    @Test
    fun samplingExponentialTest() = runTest {
        val generator = RandomGenerator.default(1)

        fun expDist(lambda: Double, arg: Double): Double = if (arg <= 0.0) 0.0 else lambda * exp(-arg * lambda)

        listOf(
            TestSetup(0.5, 2.0),
            TestSetup(2.0, 1.0)
        ).forEach { setup ->
            val sampler = MetropolisHastingsSampler.univariateNormal(setup.startPoint, setup.sigma) {
                expDist(setup.mean, it)
            }
            val sampledValues = sampler.sample(generator).discard(burnIn).nextBuffer(sample)

            assertEquals(1.0 / setup.mean, Float64Field.mean(sampledValues), 1e-2)
        }
    }

    @Test
    fun samplingRayleighTest() = runTest {
        val generator = RandomGenerator.default(1)

        fun rayleighDist(sigma: Double, arg: Double): Double = if (arg < 0.0) {
            0.0
        } else {
            arg * exp(-(arg / sigma).pow(2) / 2.0) / sigma.pow(2)
        }

        listOf(
            TestSetup(0.5, 1.0),
            TestSetup(2.0, 1.0)
        ).forEach { setup ->
            val sampler = MetropolisHastingsSampler.univariateNormal(setup.startPoint, setup.sigma) {
                rayleighDist(setup.mean, it)
            }
            val sampledValues = sampler.sample(generator).discard(burnIn).nextBuffer(sample)

            assertEquals(setup.mean * sqrt(PI / 2), Float64Field.mean(sampledValues), 1e-2)
        }
    }
}