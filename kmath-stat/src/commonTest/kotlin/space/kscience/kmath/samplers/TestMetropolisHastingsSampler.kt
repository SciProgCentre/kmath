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
import kotlin.test.Test
import kotlin.test.assertEquals

class TestMetropolisHastingsSampler {

    @Test
    fun samplingNormalTest1() {
        fun myDist(arg : Double) = NormalDistribution(0.0, 1.0).probability(arg)
        val sampler = MetropolisHastingsSampler(::myDist)


        val sampledValues = sampler.sample(DefaultGenerator()).nextBufferBlocking(10)
        assertEquals(0.05, Float64Field.mean(sampledValues), 0.01)
    }
}