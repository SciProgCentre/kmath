package kscience.kmath.stat

import org.apache.commons.rng.UniformRandomProvider
import org.apache.commons.rng.simple.RandomSource

public class RandomSourceGenerator(public val source: RandomSource, seed: Long?) : RandomGenerator {
    internal val random: UniformRandomProvider = seed?.let {
        RandomSource.create(source, seed)
    } ?: RandomSource.create(source)

    public override fun nextBoolean(): Boolean = random.nextBoolean()
    public override fun nextDouble(): Double = random.nextDouble()
    public override fun nextInt(): Int = random.nextInt()
    public override fun nextInt(until: Int): Int = random.nextInt(until)
    public override fun nextLong(): Long = random.nextLong()
    public override fun nextLong(until: Long): Long = random.nextLong(until)

    public override fun fillBytes(array: ByteArray, fromIndex: Int, toIndex: Int) {
        require(toIndex > fromIndex)
        random.nextBytes(array, fromIndex, toIndex - fromIndex)
    }

    public override fun fork(): RandomGenerator = RandomSourceGenerator(source, nextLong())
}

public inline class RandomGeneratorProvider(public val generator: RandomGenerator) : UniformRandomProvider {
    public override fun nextBoolean(): Boolean = generator.nextBoolean()
    public override fun nextFloat(): Float = generator.nextDouble().toFloat()

    public override fun nextBytes(bytes: ByteArray) {
        generator.fillBytes(bytes)
    }

    public override fun nextBytes(bytes: ByteArray, start: Int, len: Int) {
        generator.fillBytes(bytes, start, start + len)
    }

    public override fun nextInt(): Int = generator.nextInt()
    public override fun nextInt(n: Int): Int = generator.nextInt(n)
    public override fun nextDouble(): Double = generator.nextDouble()
    public override fun nextLong(): Long = generator.nextLong()
    public override fun nextLong(n: Long): Long = generator.nextLong(n)
}

/**
 * Represent this [RandomGenerator] as commons-rng [UniformRandomProvider] preserving and mirroring its current state.
 * Getting new value from one of those changes the state of another.
 */
public fun RandomGenerator.asUniformRandomProvider(): UniformRandomProvider = if (this is RandomSourceGenerator)
    random
else
    RandomGeneratorProvider(this)

public fun RandomGenerator.Companion.fromSource(source: RandomSource, seed: Long? = null): RandomSourceGenerator =
    RandomSourceGenerator(source, seed)

public fun RandomGenerator.Companion.mersenneTwister(seed: Long? = null): RandomSourceGenerator =
    fromSource(RandomSource.MT, seed)
