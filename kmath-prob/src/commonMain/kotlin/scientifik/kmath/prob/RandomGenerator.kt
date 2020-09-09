package scientifik.kmath.prob

import kotlin.random.Random

/**
 * A basic generator
 */
public interface RandomGenerator {
    public fun nextBoolean(): Boolean
    public fun nextDouble(): Double
    public fun nextInt(): Int
    public fun nextInt(until: Int): Int
    public fun nextLong(): Long
    public fun nextLong(until: Long): Long
    public fun fillBytes(array: ByteArray, fromIndex: Int = 0, toIndex: Int = array.size)
    public fun nextBytes(size: Int): ByteArray = ByteArray(size).also { fillBytes(it) }

    /**
     * Create a new generator which is independent from current generator (operations on new generator do not affect this one
     * and vise versa). The statistical properties of new generator should be the same as for this one.
     * For pseudo-random generator, the fork is keeping the same sequence of numbers for given call order for each run.
     *
     * The thread safety of this operation is not guaranteed since it could affect the state of the generator.
     */
    public fun fork(): RandomGenerator

    public companion object {
        public val default: DefaultGenerator by lazy { DefaultGenerator() }

        public fun default(seed: Long): DefaultGenerator = DefaultGenerator(Random(seed))
    }
}

public inline class DefaultGenerator(public val random: Random = Random) : RandomGenerator {
    public override fun nextBoolean(): Boolean = random.nextBoolean()
    public override fun nextDouble(): Double = random.nextDouble()
    public override fun nextInt(): Int = random.nextInt()
    public override fun nextInt(until: Int): Int = random.nextInt(until)
    public override fun nextLong(): Long = random.nextLong()
    public override fun nextLong(until: Long): Long = random.nextLong(until)

    public override fun fillBytes(array: ByteArray, fromIndex: Int, toIndex: Int) {
        random.nextBytes(array, fromIndex, toIndex)
    }

    public override fun nextBytes(size: Int): ByteArray = random.nextBytes(size)
    public override fun fork(): RandomGenerator = RandomGenerator.default(random.nextLong())
}
