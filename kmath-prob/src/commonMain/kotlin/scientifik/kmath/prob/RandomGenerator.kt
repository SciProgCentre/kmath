package scientifik.kmath.prob

import kotlin.random.Random

/**
 * A basic generator
 */
interface RandomGenerator {
    fun nextBoolean(): Boolean
    fun nextDouble(): Double
    fun nextInt(): Int
    fun nextInt(until: Int): Int
    fun nextLong(): Long
    fun nextLong(until: Long): Long
    fun fillBytes(array: ByteArray, fromIndex: Int = 0, toIndex: Int = array.size)
    fun nextBytes(size: Int): ByteArray = ByteArray(size).also { fillBytes(it) }

    /**
     * Create a new generator which is independent from current generator (operations on new generator do not affect this one
     * and vise versa). The statistical properties of new generator should be the same as for this one.
     * For pseudo-random generator, the fork is keeping the same sequence of numbers for given call order for each run.
     *
     * The thread safety of this operation is not guaranteed since it could affect the state of the generator.
     */
    fun fork(): RandomGenerator

    companion object {
        val default by lazy { DefaultGenerator() }
        fun default(seed: Long) = DefaultGenerator(Random(seed))
    }
}

inline class DefaultGenerator(private val random: Random = Random) : RandomGenerator {
    override fun nextBoolean(): Boolean = random.nextBoolean()
    override fun nextDouble(): Double = random.nextDouble()
    override fun nextInt(): Int = random.nextInt()
    override fun nextInt(until: Int): Int = random.nextInt(until)
    override fun nextLong(): Long = random.nextLong()
    override fun nextLong(until: Long): Long = random.nextLong(until)

    override fun fillBytes(array: ByteArray, fromIndex: Int, toIndex: Int) {
        random.nextBytes(array, fromIndex, toIndex)
    }

    override fun nextBytes(size: Int): ByteArray = random.nextBytes(size)
    override fun fork(): RandomGenerator = RandomGenerator.default(random.nextLong())
}
