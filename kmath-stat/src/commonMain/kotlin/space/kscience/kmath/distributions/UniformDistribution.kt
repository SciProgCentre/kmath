/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.distributions

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.chains.SimpleChain
import space.kscience.kmath.random.RandomGenerator

public class UniformDistribution(public val range: ClosedFloatingPointRange<Double>) : Distribution1D<Double> {
    private val length: Double = range.endInclusive - range.start

    override fun probability(arg: Double): Double = if (arg in range) 1.0 / length else 0.0

    override fun sample(generator: RandomGenerator): Chain<Double> =
        SimpleChain { range.start + generator.nextDouble() * length }

    override fun cumulative(arg: Double): Double = when {
        arg < range.start -> 0.0
        arg >= range.endInclusive -> 1.0
        else -> (arg - range.start) / length
    }
}

public fun Distribution.Companion.uniform(range: ClosedFloatingPointRange<Double>): UniformDistribution =
    UniformDistribution(range)
