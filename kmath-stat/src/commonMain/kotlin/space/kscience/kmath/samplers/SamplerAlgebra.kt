/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.chains.ConstantChain
import space.kscience.kmath.chains.map
import space.kscience.kmath.chains.zip
import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.stat.Sampler

/**
 * Implements [Sampler] by sampling only certain [value].
 *
 * @property value the value to sample.
 */
public class ConstantSampler<out T : Any>(public val value: T) : Sampler<T> {
    override fun sample(generator: RandomGenerator): Chain<T> = ConstantChain(value)
}

/**
 * Implements [Sampler] by delegating sampling to value of [chainBuilder].
 *
 * @property chainBuilder the provider of [Chain].
 */
public class BasicSampler<out T : Any>(public val chainBuilder: (RandomGenerator) -> Chain<T>) : Sampler<T> {
    override fun sample(generator: RandomGenerator): Chain<T> = chainBuilder(generator)
}

/**
 * A space of samplers. Allows performing simple operations on distributions.
 *
 * @property algebra the space to provide addition and scalar multiplication for [T].
 */
public class SamplerSpace<T : Any, out S>(public val algebra: S) : Group<Sampler<T>>,
    ScaleOperations<Sampler<T>> where S : Group<T>, S : ScaleOperations<T> {

    override val zero: Sampler<T> = ConstantSampler(algebra.zero)

    override fun add(left: Sampler<T>, right: Sampler<T>): Sampler<T> = BasicSampler { generator ->
        left.sample(generator).zip(right.sample(generator)) { aValue, bValue -> algebra { aValue + bValue } }
    }

    override fun scale(a: Sampler<T>, value: Double): Sampler<T> = BasicSampler { generator ->
        a.sample(generator).map { a ->
            algebra { a * value }
        }
    }

    override fun Sampler<T>.unaryMinus(): Sampler<T> = scale(this, -1.0)
}
