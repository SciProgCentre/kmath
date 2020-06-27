package scientifik.kmath.commons.random

import scientifik.kmath.prob.RandomGenerator

class CMRandomGeneratorWrapper(val factory: (IntArray) -> RandomGenerator) :
    org.apache.commons.math3.random.RandomGenerator {
    private var generator = factory(intArrayOf())

    override fun nextBoolean(): Boolean = generator.nextBoolean()

    override fun nextFloat(): Float = generator.nextDouble().toFloat()

    override fun setSeed(seed: Int) {
        generator = factory(intArrayOf(seed))
    }

    override fun setSeed(seed: IntArray) {
        generator = factory(seed)
    }

    override fun setSeed(seed: Long) {
        setSeed(seed.toInt())
    }

    override fun nextBytes(bytes: ByteArray) {
        generator.fillBytes(bytes)
    }

    override fun nextInt(): Int = generator.nextInt()

    override fun nextInt(n: Int): Int = generator.nextInt(n)

    override fun nextGaussian(): Double = TODO()

    override fun nextDouble(): Double = generator.nextDouble()

    override fun nextLong(): Long = generator.nextLong()
}