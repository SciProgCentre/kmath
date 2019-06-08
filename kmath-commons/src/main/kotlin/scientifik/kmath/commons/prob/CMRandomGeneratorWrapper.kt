package scientifik.kmath.commons.prob

import org.apache.commons.math3.random.RandomGenerator

inline class CMRandomGeneratorWrapper(val generator: RandomGenerator) : scientifik.kmath.prob.RandomGenerator {
    override fun nextDouble(): Double = generator.nextDouble()

    override fun nextInt(): Int = generator.nextInt()

    override fun nextLong(): Long = generator.nextLong()

    override fun nextBlock(size: Int): ByteArray = ByteArray(size).apply { generator.nextBytes(this) }
}

fun RandomGenerator.asKmathGenerator() = CMRandomGeneratorWrapper(this)

fun scientifik.kmath.prob.RandomGenerator.asCMGenerator() =
    (this as? CMRandomGeneratorWrapper)?.generator ?: TODO("Implement reverse CM wrapper")