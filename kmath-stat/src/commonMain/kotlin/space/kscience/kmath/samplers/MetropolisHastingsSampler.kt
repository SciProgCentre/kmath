/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import space.kscience.kmath.chains.BlockingDoubleChain
import space.kscience.kmath.distributions.Distribution1D
import space.kscience.kmath.distributions.NormalDistribution
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.structures.Float64Buffer
import kotlin.math.*

/**
 * [Metropolis–Hastings algorithm](https://en.wikipedia.org/wiki/Metropolis%E2%80%93Hastings_algorithm) for sampling
 * target distribution [targetDist].
 *
 *      params:
 *          - targetDist: function close to the density of the sampled distribution;
 *          - initialState: initial value of the chain of sampled values.
 */
public class MetropolisHastingsSampler(
    public val targetDist: (arg : Double) -> Double,
    public val initialState : Double = 0.0,
) : BlockingDoubleSampler {
    override fun sample(generator: RandomGenerator): BlockingDoubleChain = object : BlockingDoubleChain {
        var currentState = initialState
        fun proposalDist(arg : Double) = NormalDistribution(arg, 0.01)

        override fun nextBufferBlocking(size: Int): Float64Buffer {
            val acceptanceProb = generator.nextDoubleBuffer(size)

            return Float64Buffer(size) {index ->
                val newState = proposalDist(currentState).sample(RandomGenerator.default(0)).nextBufferBlocking(5).get(4)
                val firstComp = targetDist(newState) / targetDist(currentState)
                val secondComp = proposalDist(newState).probability(currentState) / proposalDist(currentState).probability(newState)
                val acceptanceRatio = min(1.0, firstComp * secondComp)

                currentState = if (acceptanceProb[index] <= acceptanceRatio) newState else currentState
                currentState
            }
        }

        override suspend fun fork(): BlockingDoubleChain = BoxMullerSampler.sample(generator.fork())
    }

}