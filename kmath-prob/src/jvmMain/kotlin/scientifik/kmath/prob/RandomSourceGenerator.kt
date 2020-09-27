package scientifik.kmath.prob

import kscience.kmath.prob.RandomGenerator
import org.apache.commons.rng.simple.RandomSource

public class RandomSourceGenerator(private val source: RandomSource, seed: Long?) :
    RandomGenerator {
    private val random = seed?.let {
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

    override fun fork(): RandomGenerator = RandomSourceGenerator(source, nextLong())
}

public fun RandomGenerator.Companion.fromSource(source: RandomSource, seed: Long? = null): RandomSourceGenerator =
    RandomSourceGenerator(source, seed)

public fun RandomGenerator.Companion.mersenneTwister(seed: Long? = null): RandomSourceGenerator =
    fromSource(RandomSource.MT, seed)
