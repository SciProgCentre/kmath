/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import space.kscience.kmath.chains.BlockingDoubleChain
import space.kscience.kmath.distributions.NormalDistribution
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.structures.Float64Buffer

/**
 * [Metropolis–Hastings algorithm](https://en.wikipedia.org/wiki/Metropolis%E2%80%93Hastings_algorithm) for sampling
 * target distribution [distribution].
 *
 */
public class MetropolisHastingsSampler(
    public val distribution: (x : Double) -> Double,
    public val initState : Double = 0.0,
) : BlockingDoubleSampler {
    override fun sample(generator: RandomGenerator): BlockingDoubleChain = object : BlockingDoubleChain {
        var currState = initState

        override fun nextBufferBlocking(size: Int): Float64Buffer {
            val u = generator.nextDoubleBuffer(size)

            return Float64Buffer(size) {index ->
                val proposalDist = NormalDistribution(currState, 0.01)
                val newState = proposalDist.sample(RandomGenerator.default(1)).nextBufferBlocking(1).get(0)
                val acceptanceRatio = distribution(newState) / distribution(currState)
                if (u[index] <= acceptanceRatio) {
                    currState = newState
                }
                currState
            }
        }

        override suspend fun fork(): BlockingDoubleChain = BoxMullerSampler.sample(generator.fork())
    }

}