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
 * [Metropolis–Hastings algorithm](https://en.wikipedia.org/wiki/Metropolis-Hastings_algorithm) for sampling
 * target distribution [targetDist].
 *
 * The normal distribution is used as the proposal function.
 *
 *      params:
 *          - targetDist: function close to the density of the sampled distribution;
 *          - initialState: initial value of the chain of sampled values;
 *          - proposalStd: standard deviation of the proposal function.
 */
public class MetropolisHastingsSampler(
    public val targetDist: (arg : Double) -> Double,
    public val initialState : Double = 0.0,
    public val proposalStd : Double = 1.0,
) : BlockingDoubleSampler {
    override fun sample(generator: RandomGenerator): BlockingDoubleChain = object : BlockingDoubleChain {
        var currentState = initialState
        fun proposalDist(arg : Double) = NormalDistribution(arg, proposalStd)

        override fun nextBufferBlocking(size: Int): Float64Buffer {
            val acceptanceProb = generator.nextDoubleBuffer(size)

            return Float64Buffer(size) {index ->
                val newState = proposalDist(currentState).sample(generator).nextBufferBlocking(1).get(0)
                val acceptanceRatio = min(1.0, targetDist(newState) / targetDist(currentState))

                currentState = if (acceptanceProb[index] <= acceptanceRatio) newState else currentState
                currentState
            }
        }

        override suspend fun fork(): BlockingDoubleChain = sample(generator.fork())
    }

}