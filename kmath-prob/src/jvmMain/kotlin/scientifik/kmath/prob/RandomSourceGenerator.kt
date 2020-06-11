package scientifik.kmath.prob

import org.apache.commons.rng.UniformRandomProvider
import org.apache.commons.rng.simple.RandomSource

class RandomSourceGenerator(val source: RandomSource, seed: Long?) : RandomGenerator {
    internal val random: UniformRandomProvider = seed?.let {
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

inline class RandomGeneratorProvider(val generator: RandomGenerator) : UniformRandomProvider {
    override fun nextBoolean(): Boolean = generator.nextBoolean()

    override fun nextFloat(): Float = generator.nextDouble().toFloat()

    override fun nextBytes(bytes: ByteArray) {
        generator.fillBytes(bytes)
    }

    override fun nextBytes(bytes: ByteArray, start: Int, len: Int) {
        generator.fillBytes(bytes, start, start + len)
    }

    override fun nextInt(): Int = generator.nextInt()

    override fun nextInt(n: Int): Int = generator.nextInt(n)

    override fun nextDouble(): Double = generator.nextDouble()

    override fun nextLong(): Long = generator.nextLong()

    override fun nextLong(n: Long): Long = generator.nextLong(n)
}

/**
 * Represent this [RandomGenerator] as commons-rng [UniformRandomProvider] preserving and mirroring its current state.
 * Getting new value from one of those changes the state of another.
 */
fun RandomGenerator.asUniformRandomProvider(): UniformRandomProvider = if (this is RandomSourceGenerator) {
    random
} else {
    RandomGeneratorProvider(this)
}

fun RandomGenerator.Companion.fromSource(source: RandomSource, seed: Long? = null): RandomSourceGenerator =
    RandomSourceGenerator(source, seed)

fun RandomGenerator.Companion.mersenneTwister(seed: Long? = null): RandomSourceGenerator =
    fromSource(RandomSource.MT, seed)
