/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.chains.StatefulChain
import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.stat.Sampler
import space.kscience.kmath.structures.Float64

/**
 * [Metropolis–Hastings algorithm](https://en.wikipedia.org/wiki/Metropolis-Hastings_algorithm) for sampling
 * target distribution [targetPdf].
 *
 * @param stepSampler a sampler for proposal point (offset to previous point)
 */
public class MetropolisHastingsSampler<T>(
    public val algebra: Group<T>,
    public val startPoint: suspend (RandomGenerator) ->T,
    public val stepSampler: Sampler<T>,
    public val targetPdf: suspend (T) -> Float64,
) : Sampler<T> {

    //TODO consider API for conditional step probability

    override fun sample(generator: RandomGenerator): Chain<T> = StatefulChain<Chain<T>, T>(
        state = stepSampler.sample(generator),
        seed = { startPoint(generator) },
        forkState = Chain<T>::fork
    ) { previousPoint: T ->
        val proposalPoint = with(algebra) { previousPoint + next() }
        val ratio = targetPdf(proposalPoint) / targetPdf(previousPoint)
        if (ratio >= 1.0) {
            proposalPoint
        } else {
            val acceptanceProbability = generator.nextDouble()
            if (acceptanceProbability <= ratio) {
                proposalPoint
            } else {
                previousPoint
            }
        }
    }


    public companion object {

        /**
         * A Metropolis-Hastings sampler for univariate [Float64] values
         */
        public fun univariate(
            startPoint: Float64,
            stepSampler: Sampler<Float64>,
            targetPdf: suspend (Float64) -> Float64,
        ): MetropolisHastingsSampler<Float64> = MetropolisHastingsSampler(
            algebra = Float64.algebra,
            startPoint = {startPoint},
            stepSampler = stepSampler,
            targetPdf = targetPdf
        )

        /**
         * A Metropolis-Hastings sampler for univariate [Float64] values with normal step distribution
         */
        public fun univariateNormal(
            startPoint: Float64,
            stepSigma: Float64,
            targetPdf: suspend (Float64) -> Float64,
        ): MetropolisHastingsSampler<Float64> = univariate(startPoint, GaussianSampler(0.0, stepSigma), targetPdf)
    }
}