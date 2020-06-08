package scientifik.kmath.prob

import scientifik.kmath.commons.rng.UniformRandomProvider


inline class RandomGeneratorProvider(val generator: RandomGenerator) :
    UniformRandomProvider {
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
