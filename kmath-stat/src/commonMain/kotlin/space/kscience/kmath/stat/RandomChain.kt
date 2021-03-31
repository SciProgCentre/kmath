package space.kscience.kmath.stat

import space.kscience.kmath.chains.BlockingDoubleChain
import space.kscience.kmath.chains.BlockingIntChain
import space.kscience.kmath.chains.Chain

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
public fun Chain<Double>.blocking(): BlockingDoubleChain = object : Chain<Double> by this, BlockingDoubleChain {}
public fun Chain<Int>.blocking(): BlockingIntChain = object : Chain<Int> by this, BlockingIntChain {}
