package scientifik.kmath.prob

import kotlin.random.Random

/**
 * A basic generator
 */
interface RandomGenerator {
    fun nextDouble(): Double
    fun nextInt(): Int
    fun nextLong(): Long
    fun nextBlock(size: Int): ByteArray

    /**
     * Create a new generator which is independent from current generator (operations on new generator do not affect this one
     * and vise versa). The statistical properties of new generator should be the same as for this one.
     * For pseudo-random generator, the fork is keeping the same sequence of numbers for given call order for each run.
     *
     * The thread safety of this operation is not guaranteed since it could affect the state of the generator.
     */
    fun fork(): RandomGenerator

    companion object {
        val default by lazy { DefaultGenerator(Random.nextLong()) }
    }
}

class DefaultGenerator(seed: Long?) : RandomGenerator {
    private val random = seed?.let { Random(it) } ?: Random

    override fun nextDouble(): Double = random.nextDouble()

    override fun nextInt(): Int = random.nextInt()

    override fun nextLong(): Long = random.nextLong()

    override fun nextBlock(size: Int): ByteArray = random.nextBytes(size)

    override fun fork(): RandomGenerator = DefaultGenerator(nextLong())

}