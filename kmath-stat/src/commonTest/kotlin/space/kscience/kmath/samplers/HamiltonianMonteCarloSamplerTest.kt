/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import kotlinx.coroutines.runBlocking
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.structures.MutableBuffer
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class HamiltonianMonteCarloSamplerTest {

    @Test
    fun testUnivariateStandardNormal() = runBlocking {
        val sampler = HamiltonianMonteCarloSampler.univariate(
            startPoint = 2.0,
            targetLogPdf = { -0.5 * it * it },
            gradientLogPdf = { -it },
            stepSize = 0.1,
            leapfrogSteps = 20
        )

        val generator = RandomGenerator.default(42)
        val chain = sampler.sample(generator)

        // Burn-in
        repeat(1_000) { chain.next() }

        val samples = List(5_000) { chain.next()[0] }
        val mean = samples.average()
        val variance = samples.map { (it - mean) * (it - mean) }.average()

        assertTrue(abs(mean) < 0.15, "Mean should be close to 0, was $mean")
        assertTrue(abs(variance - 1.0) < 0.15, "Variance should be close to 1, was $variance")
    }

    @Test
    fun testBivariateIsotropicNormal() = runBlocking {
        val sampler = HamiltonianMonteCarloSampler(
            dimension = 2,
            startPoint = { MutableBuffer.double(2) { 2.0 } },
            targetLogPdf = { buf -> -0.5 * (buf[0] * buf[0] + buf[1] * buf[1]) },
            gradientLogPdf = { buf -> MutableBuffer.double(2) { -buf[it] } },
            stepSize = 0.15,
            leapfrogSteps = 15
        )

        val generator = RandomGenerator.default(42)
        val chain = sampler.sample(generator)

        // Burn-in
        repeat(2_000) { chain.next() }

        val samples = List(10_000) { chain.next() }
        val meanX = samples.map { it[0] }.average()
        val meanY = samples.map { it[1] }.average()
        val varX = samples.map { (it[0] - meanX) * (it[0] - meanX) }.average()
        val varY = samples.map { (it[1] - meanY) * (it[1] - meanY) }.average()

        assertTrue(abs(meanX) < 0.15, "Mean X should be close to 0, was $meanX")
        assertTrue(abs(meanY) < 0.15, "Mean Y should be close to 0, was $meanY")
        assertTrue(abs(varX - 1.0) < 0.15, "Var X should be close to 1, was $varX")
        assertTrue(abs(varY - 1.0) < 0.15, "Var Y should be close to 1, was $varY")
    }

    @Test
    fun testCorrelatedBivariateNormal() = runBlocking {
        // Target: N(0, Σ) with Σ = [[1, 0.5], [0.5, 1]]
        // Σ^-1 = (1/0.75) * [[1, -0.5], [-0.5, 1]]
        val inv11 = 4.0 / 3.0
        val inv12 = -2.0 / 3.0

        val sampler = HamiltonianMonteCarloSampler(
            dimension = 2,
            startPoint = { MutableBuffer.double(2) { 2.0 } },
            targetLogPdf = { buf ->
                val x = buf[0]
                val y = buf[1]
                -0.5 * (inv11 * x * x + 2 * inv12 * x * y + inv11 * y * y)
            },
            gradientLogPdf = { buf ->
                val x = buf[0]
                val y = buf[1]
                MutableBuffer.double(2) {
                    when (it) {
                        0 -> -(inv11 * x + inv12 * y)
                        else -> -(inv12 * x + inv11 * y)
                    }
                }
            },
            stepSize = 0.12,
            leapfrogSteps = 20
        )

        val generator = RandomGenerator.default(42)
        val chain = sampler.sample(generator)

        // Burn-in
        repeat(3_000) { chain.next() }

        val samples = List(50_000) { chain.next() }
        val meanX = samples.map { it[0] }.average()
        val meanY = samples.map { it[1] }.average()

        // Empirical covariance
        val covXX = samples.map { (it[0] - meanX) * (it[0] - meanX) }.average()
        val covYY = samples.map { (it[1] - meanY) * (it[1] - meanY) }.average()
        val covXY = samples.map { (it[0] - meanX) * (it[1] - meanY) }.average()

        assertTrue(abs(meanX) < 0.15, "Mean X should be close to 0, was $meanX")
        assertTrue(abs(meanY) < 0.15, "Mean Y should be close to 0, was $meanY")
        assertTrue(abs(covXX - 1.0) < 0.15, "Cov XX should be close to 1, was $covXX")
        assertTrue(abs(covYY - 1.0) < 0.15, "Cov YY should be close to 1, was $covYY")
        assertTrue(abs(covXY - 0.5) < 0.15, "Cov XY should be close to 0.5, was $covXY")
    }
}
