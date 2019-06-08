package scientifik.kmath.commons.prob

import org.apache.commons.math3.random.JDKRandomGenerator
import scientifik.kmath.prob.RandomGenerator
import org.apache.commons.math3.random.RandomGenerator as CMRandom

inline class CMRandomGeneratorWrapper(val generator: CMRandom) : RandomGenerator {
    override fun nextDouble(): Double = generator.nextDouble()

    override fun nextInt(): Int = generator.nextInt()

    override fun nextLong(): Long = generator.nextLong()

    override fun nextBlock(size: Int): ByteArray = ByteArray(size).apply { generator.nextBytes(this) }
}

fun CMRandom.asKmathGenerator(): RandomGenerator = CMRandomGeneratorWrapper(this)

fun RandomGenerator.asCMGenerator(): CMRandom =
    (this as? CMRandomGeneratorWrapper)?.generator ?: TODO("Implement reverse CM wrapper")

val RandomGenerator.Companion.default: RandomGenerator by lazy { JDKRandomGenerator().asKmathGenerator() }

fun RandomGenerator.Companion.jdk(seed: Int? = null): RandomGenerator = if (seed == null) {
    JDKRandomGenerator()
} else {
    JDKRandomGenerator(seed)
}.asKmathGenerator()