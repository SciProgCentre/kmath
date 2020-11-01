package kscience.kmath.stat

import kscience.kmath.chains.Chain
import kscience.kmath.chains.ConstantChain
import kscience.kmath.chains.map
import kscience.kmath.chains.zip
import kscience.kmath.operations.Space
import kscience.kmath.operations.invoke

/**
 * Implements [Sampler] by sampling only certain [value].
 *
 * @property value the value to sample.
 */
public class ConstantSampler<T : Any>(public val value: T) : Sampler<T> {
    public override fun sample(generator: RandomGenerator): Chain<T> = ConstantChain(value)
}

/**
 * A space of samplers. Allows to perform simple operations on distributions.
 *
 * @property space the space to provide addition and scalar multiplication for [T].
 */
public class SamplerSpace<T : Any>(public val space: Space<T>) : Space<Sampler<T>> {
    public override val zero: Sampler<T> = ConstantSampler(space.zero)

    public override fun add(a: Sampler<T>, b: Sampler<T>): Sampler<T> = Sampler { generator ->
        a.sample(generator).zip(b.sample(generator)) { aValue, bValue -> space { aValue + bValue } }
    }

    public override fun multiply(a: Sampler<T>, k: Number): Sampler<T> = Sampler { generator ->
        a.sample(generator).map { space { it * k.toDouble() } }
    }
}
