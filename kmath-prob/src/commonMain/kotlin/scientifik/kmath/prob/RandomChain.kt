package scientifik.kmath.prob

import scientifik.kmath.chains.Chain

/**
 * A possibly stateful chain producing random values.
 */
class RandomChain<out R>(val generator: RandomGenerator, private val gen: suspend RandomGenerator.() -> R) : Chain<R> {
    override suspend fun next(): R = generator.gen()

    override fun fork(): Chain<R> = RandomChain(generator.fork(), gen)
}

fun <R> RandomGenerator.chain(gen: suspend RandomGenerator.() -> R): RandomChain<R> = RandomChain(this, gen)
