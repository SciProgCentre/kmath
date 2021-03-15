package space.kscience.kmath.stat

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.chains.ConstantChain
import space.kscience.kmath.chains.map
import space.kscience.kmath.chains.zip
import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.operations.invoke

public class BasicSampler<T : Any>(public val chainBuilder: (RandomGenerator) -> Chain<T>) : Sampler<T> {
    public override fun sample(generator: RandomGenerator): Chain<T> = chainBuilder(generator)
}

public class ConstantSampler<T : Any>(public val value: T) : Sampler<T> {
    public override fun sample(generator: RandomGenerator): Chain<T> = ConstantChain(value)
}

/**
 * A space for samplers. Allows to perform simple operations on distributions
 */
public class SamplerSpace<T : Any, S>(public val algebra: S) : Group<Sampler<T>>,
    ScaleOperations<Sampler<T>> where S : Group<T>, S : ScaleOperations<T> {

    public override val zero: Sampler<T> = ConstantSampler(algebra.zero)

    public override fun add(a: Sampler<T>, b: Sampler<T>): Sampler<T> = BasicSampler { generator ->
        a.sample(generator).zip(b.sample(generator)) { aValue, bValue -> algebra { aValue + bValue } }
    }

    public override fun scale(a: Sampler<T>, value: Double): Sampler<T> = BasicSampler { generator ->
        a.sample(generator).map { algebra { it * value } }
    }

    override fun Sampler<T>.unaryMinus(): Sampler<T> = scale(this, -1.0)
}
