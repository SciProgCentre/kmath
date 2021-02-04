package kscience.kmath.stat

import kotlin.random.Random

/**
 * An interface that is implemented by random number generator algorithms.
 */
public interface RandomGenerator {
    /**
     * Gets the next random [Boolean] value.
     */
    public fun nextBoolean(): Boolean

    /**
     * Gets the next random [Double] value uniformly distributed between 0 (inclusive) and 1 (exclusive).
     */
    public fun nextDouble(): Double

    /**
     * Gets the next random `Int` from the random number generator.
     *
     * Generates an `Int` random value uniformly distributed between [Int.MIN_VALUE] and [Int.MAX_VALUE] (inclusive).
     */
    public fun nextInt(): Int

    /**
     * Gets the next random non-negative `Int` from the random number generator less than the specified [until] bound.
     *
     * Generates an `Int` random value uniformly distributed between `0` (inclusive) and the specified [until] bound
     * (exclusive).
     */
    public fun nextInt(until: Int): Int

    /**
     * Gets the next random `Long` from the random number generator.
     *
     * Generates a `Long` random value uniformly distributed between [Long.MIN_VALUE] and [Long.MAX_VALUE] (inclusive).
     */
    public fun nextLong(): Long

    /**
     * Gets the next random non-negative `Long` from the random number generator less than the specified [until] bound.
     *
     * Generates a `Long` random value uniformly distributed between `0` (inclusive) and the specified [until] bound (exclusive).
     */
    public fun nextLong(until: Long): Long

    /**
     * Fills a subrange of the specified byte [array] starting from [fromIndex] inclusive and ending [toIndex] exclusive
     * with random bytes.
     *
     * @return [array] with the subrange filled with random bytes.
     */
    public fun fillBytes(array: ByteArray, fromIndex: Int = 0, toIndex: Int = array.size)

    /**
     * Creates a byte array of the specified [size], filled with random bytes.
     */
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
        /**
         * The [DefaultGenerator] instance.
         */
        public val default: DefaultGenerator by lazy(::DefaultGenerator)

        /**
         * Returns [DefaultGenerator] of given [seed].
         */
        public fun default(seed: Long): DefaultGenerator = DefaultGenerator(Random(seed))
    }
}

/**
 * Implements [RandomGenerator] by delegating all operations to [Random].
 */
public class DefaultGenerator(public val random: Random = Random) : RandomGenerator {
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
