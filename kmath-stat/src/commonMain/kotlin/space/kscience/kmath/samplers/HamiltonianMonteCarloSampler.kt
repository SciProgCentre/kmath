/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.chains.StatefulChain
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.samplers.GaussianSampler
import space.kscience.kmath.stat.Sampler
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.Float64
import space.kscience.kmath.structures.MutableBuffer
import kotlin.math.ln

/**
 * Hamiltonian Monte Carlo sampler for continuous multivariate distributions.
 *
 * Uses leapfrog integration of Hamiltonian dynamics with a diagonal (identity) mass matrix.
 *
 * @param dimension the dimensionality of the target distribution.
 * @param startPoint a function providing the initial position given a [RandomGenerator].
 * @param targetLogPdf the log-probability density function (up to an additive constant).
 * @param gradientLogPdf the gradient of [targetLogPdf] with respect to the parameters.
 * @param stepSize the leapfrog step size (epsilon).
 * @param leapfrogSteps the number of leapfrog steps (L).
 */
public class HamiltonianMonteCarloSampler(
    public val dimension: Int,
    public val startPoint: suspend (RandomGenerator) -> Buffer<Float64>,
    public val targetLogPdf: suspend (Buffer<Float64>) -> Float64,
    public val gradientLogPdf: suspend (Buffer<Float64>) -> Buffer<Float64>,
    public val stepSize: Float64,
    public val leapfrogSteps: Int,
) : Sampler<Buffer<Float64>> {

    private val momentumSampler: Sampler<Float64> = GaussianSampler(0.0, 1.0)

    override fun sample(generator: RandomGenerator): Chain<Buffer<Float64>> {
        val momentumChain = momentumSampler.sample(generator)

        return StatefulChain<Chain<Float64>, Buffer<Float64>>(
            state = momentumChain,
            seed = { startPoint(generator) },
            forkState = Chain<Float64>::fork
        ) { previousPoint: Buffer<Float64> ->
            // Sample fresh momentum ~ N(0, I)
            val momentum = MutableBuffer.double(dimension) { next() }

            // Copy to proposed state
            val proposedPos = MutableBuffer.double(dimension) { previousPoint[it] }
            val proposedMom = MutableBuffer.double(dimension) { momentum[it] }

            // Potential energy gradient: ∇U = -∇logπ
            var grad = gradientLogPdf(proposedPos)
            for (i in 0 until dimension) {
                proposedMom[i] += 0.5 * stepSize * (-grad[i])
            }

            // Full leapfrog steps
            repeat(leapfrogSteps - 1) {
                for (i in 0 until dimension) {
                    proposedPos[i] += stepSize * proposedMom[i]
                }
                grad = gradientLogPdf(proposedPos)
                for (i in 0 until dimension) {
                    proposedMom[i] += stepSize * (-grad[i])
                }
            }

            // Final position step
            for (i in 0 until dimension) {
                proposedPos[i] += stepSize * proposedMom[i]
            }

            // Final half-step for momentum
            grad = gradientLogPdf(proposedPos)
            for (i in 0 until dimension) {
                proposedMom[i] += 0.5 * stepSize * (-grad[i])
            }

            // Metropolis acceptance criterion
            val currentLogP = targetLogPdf(previousPoint)
            val proposedLogP = targetLogPdf(proposedPos)

            val currentKinetic = 0.5 * (0 until dimension).sumOf { momentum[it] * momentum[it] }
            val proposedKinetic = 0.5 * (0 until dimension).sumOf { proposedMom[it] * proposedMom[it] }

            val logAcceptance = (proposedLogP - currentLogP) + (currentKinetic - proposedKinetic)

            if (logAcceptance >= 0.0 || ln(generator.nextDouble()) < logAcceptance) {
                proposedPos
            } else {
                previousPoint
            }
        }
    }

    public companion object {
        /**
         * A univariate HMC sampler for [Float64] values.
         */
        public fun univariate(
            startPoint: Float64,
            targetLogPdf: suspend (Float64) -> Float64,
            gradientLogPdf: suspend (Float64) -> Float64,
            stepSize: Float64,
            leapfrogSteps: Int,
        ): HamiltonianMonteCarloSampler = HamiltonianMonteCarloSampler(
            dimension = 1,
            startPoint = { MutableBuffer.double(1) { startPoint } },
            targetLogPdf = { targetLogPdf(it[0]) },
            gradientLogPdf = { buffer -> MutableBuffer.double(1) { gradientLogPdf(buffer[0]) } },
            stepSize = stepSize,
            leapfrogSteps = leapfrogSteps
        )
    }
}
