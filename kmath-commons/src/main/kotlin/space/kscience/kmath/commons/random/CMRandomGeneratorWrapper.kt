package space.kscience.kmath.commons.random

import space.kscience.kmath.stat.RandomGenerator

public class CMRandomGeneratorWrapper(
    public val factory: (IntArray) -> RandomGenerator,
) : org.apache.commons.math3.random.RandomGenerator {
    private var generator: RandomGenerator = factory(intArrayOf())

    public override fun nextBoolean(): Boolean = generator.nextBoolean()
    public override fun nextFloat(): Float = generator.nextDouble().toFloat()

    public override fun setSeed(seed: Int) {
        generator = factory(intArrayOf(seed))
    }

    public override fun setSeed(seed: IntArray) {
        generator = factory(seed)
    }

    public override fun setSeed(seed: Long) {
        setSeed(seed.toInt())
    }

    public override fun nextBytes(bytes: ByteArray) {
        generator.fillBytes(bytes)
    }

    public override fun nextInt(): Int = generator.nextInt()
    public override fun nextInt(n: Int): Int = generator.nextInt(n)
    public override fun nextGaussian(): Double = TODO()
    public override fun nextDouble(): Double = generator.nextDouble()
    public override fun nextLong(): Long = generator.nextLong()
}
