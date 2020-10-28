package kscience.kmath.stat

import kscience.kmath.chains.Chain
import kscience.kmath.chains.ConstantChain
import kscience.kmath.chains.map
import kscience.kmath.chains.zip
import kscience.kmath.operations.Space
import kscience.kmath.operations.invoke

public class BasicSampler<T : Any>(public val chainBuilder: (RandomGenerator) -> Chain<T>) : Sampler<T> {
    public override fun sample(generator: RandomGenerator): Chain<T> = chainBuilder(generator)
}

public class ConstantSampler<T : Any>(public val value: T) : Sampler<T> {
    public override fun sample(generator: RandomGenerator): Chain<T> = ConstantChain(value)
}

/**
 * A space for samplers. Allows to perform simple operations on distributions
 */
public class SamplerSpace<T : Any>(public val space: Space<T>) : Space<Sampler<T>> {
    public override val zero: Sampler<T> = ConstantSampler(space.zero)

    public override fun add(a: Sampler<T>, b: Sampler<T>): Sampler<T> = BasicSampler { generator ->
        a.sample(generator).zip(b.sample(generator)) { aValue, bValue -> space { aValue + bValue } }
    }

    public override fun multiply(a: Sampler<T>, k: Number): Sampler<T> = BasicSampler { generator ->
        a.sample(generator).map { space { it * k.toDouble() } }
    }
}
