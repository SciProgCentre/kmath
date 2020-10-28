package kscience.kmath.stat

import kscience.kmath.chains.Chain

/**
 * A possibly stateful chain producing random values.
 */
public class RandomChain<out R>(
    public val generator: RandomGenerator,
    private val gen: suspend RandomGenerator.() -> R
) : Chain<R> {
    override suspend fun next(): R = generator.gen()

    override fun fork(): Chain<R> = RandomChain(generator.fork(), gen)
}

public fun <R> RandomGenerator.chain(gen: suspend RandomGenerator.() -> R): RandomChain<R> = RandomChain(this, gen)
