package scientifik.kmath.prob

import org.apache.commons.rng.simple.RandomSource
import scientifik.kmath.commons.rng.UniformRandomProvider

class RandomSourceGenerator(val source: RandomSource, seed: Long?) :
    RandomGenerator {
    internal val random = seed?.let {
        RandomSource.create(source, seed)
    } ?: RandomSource.create(source)

    override fun nextBoolean(): Boolean = random.nextBoolean()
    override fun nextDouble(): Double = random.nextDouble()
    override fun nextInt(): Int = random.nextInt()
    override fun nextInt(until: Int): Int = random.nextInt(until)
    override fun nextLong(): Long = random.nextLong()
    override fun nextLong(until: Long): Long = random.nextLong(until)

    override fun fillBytes(array: ByteArray, fromIndex: Int, toIndex: Int) {
        require(toIndex > fromIndex)
        random.nextBytes(array, fromIndex, toIndex - fromIndex)
    }

    override fun fork(): RandomGenerator = RandomSourceGenerator(source, nextLong())
}

/**
 * Represent this [RandomGenerator] as commons-rng [UniformRandomProvider] preserving and mirroring its current state.
 * Getting new value from one of those changes the state of another.
 */
fun RandomGenerator.asUniformRandomProvider(): UniformRandomProvider = if (this is RandomSourceGenerator) {
    object : UniformRandomProvider {
        override fun nextBytes(bytes: ByteArray) = random.nextBytes(bytes)
        override fun nextBytes(bytes: ByteArray, start: Int, len: Int) = random.nextBytes(bytes, start, len)
        override fun nextInt(): Int = random.nextInt()
        override fun nextInt(n: Int): Int = random.nextInt(n)
        override fun nextLong(): Long = random.nextLong()
        override fun nextLong(n: Long): Long = random.nextLong(n)
        override fun nextBoolean(): Boolean = random.nextBoolean()
        override fun nextFloat(): Float = random.nextFloat()
        override fun nextDouble(): Double = random.nextDouble()
    }
} else RandomGeneratorProvider(this)

fun RandomGenerator.Companion.fromSource(source: RandomSource, seed: Long? = null): RandomSourceGenerator =
    RandomSourceGenerator(source, seed)

fun RandomGenerator.Companion.mersenneTwister(seed: Long? = null): RandomSourceGenerator =
    fromSource(RandomSource.MT, seed)
