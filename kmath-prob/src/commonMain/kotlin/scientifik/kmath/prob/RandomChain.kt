package scientifik.kmath.prob

import kotlinx.atomicfu.atomic
import scientifik.kmath.chains.Chain

/**
 * A possibly stateful chain producing random values.
 * TODO make random chain properly fork generator
 */
class RandomChain<out R>(val generator: RandomGenerator, private val gen: suspend RandomGenerator.() -> R) : Chain<R> {
    private val atomicValue = atomic<R?>(null)
    override fun peek(): R? = atomicValue.value

    override suspend fun next(): R = generator.gen().also { atomicValue.lazySet(it) }

    override fun fork(): Chain<R> = RandomChain(generator, gen)
}