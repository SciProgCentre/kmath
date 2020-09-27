package kscience.kmath.prob

import kscience.kmath.chains.BlockingIntChain
import kscience.kmath.chains.BlockingRealChain
import kscience.kmath.chains.Chain

/**
 * A possibly stateful chain producing random values.
 *
 * @property generator the underlying [RandomGenerator] instance.
 */
public class RandomChain<out R>(
    public val generator: RandomGenerator,
    private val gen: suspend RandomGenerator.() -> R
) : Chain<R> {
    override suspend fun next(): R = generator.gen()
    override fun fork(): Chain<R> = RandomChain(generator.fork(), gen)
}

public fun <R> RandomGenerator.chain(gen: suspend RandomGenerator.() -> R): RandomChain<R> = RandomChain(this, gen)
public fun Chain<Double>.blocking(): BlockingRealChain = object : Chain<Double> by this, BlockingRealChain {}
public fun Chain<Int>.blocking(): BlockingIntChain = object : Chain<Int> by this, BlockingIntChain {}
