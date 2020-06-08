package scientifik.kmath.prob

import org.apache.commons.rng.simple.RandomSource

class RandomSourceGenerator(private val source: RandomSource, seed: Long?) :
    RandomGenerator {
    private val random = seed?.let {
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

fun RandomGenerator.Companion.fromSource(source: RandomSource, seed: Long? = null): RandomSourceGenerator =
    RandomSourceGenerator(source, seed)

fun RandomGenerator.Companion.mersenneTwister(seed: Long? = null): RandomSourceGenerator =
    fromSource(RandomSource.MT, seed)
