package scientifik.kmath.prob

import scientifik.kmath.chains.BlockingIntChain
import scientifik.kmath.chains.BlockingRealChain
import scientifik.kmath.chains.Chain

/**
 * A possibly stateful chain producing random values.
 */
class RandomChain<out R>(val generator: RandomGenerator, private val gen: suspend RandomGenerator.() -> R) : Chain<R> {
    override suspend fun next(): R = generator.gen()

    override fun fork(): Chain<R> = RandomChain(generator.fork(), gen)
}

fun <R> RandomGenerator.chain(gen: suspend RandomGenerator.() -> R): RandomChain<R> = RandomChain(this, gen)

fun RandomChain<Double>.blocking(): BlockingRealChain = let {
    object : BlockingRealChain() {
        override suspend fun next(): Double = it.next()
        override fun fork(): Chain<Double> = it.fork()
    }
}

fun RandomChain<Int>.blocking(): BlockingIntChain = let {
    object : BlockingIntChain() {
        override suspend fun next(): Int = it.next()
        override fun fork(): Chain<Int> = it.fork()
    }
}
