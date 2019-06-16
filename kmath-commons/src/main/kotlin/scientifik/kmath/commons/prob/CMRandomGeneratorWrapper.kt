package scientifik.kmath.commons.prob

import org.apache.commons.math3.random.JDKRandomGenerator
import scientifik.kmath.prob.RandomGenerator
import org.apache.commons.math3.random.RandomGenerator as CMRandom

class CMRandomGeneratorWrapper(seed: Long?, val builder: (Long?) -> CMRandom) : RandomGenerator {
    val generator = builder(seed)

    override fun nextDouble(): Double = generator.nextDouble()

    override fun nextInt(): Int = generator.nextInt()

    override fun nextLong(): Long = generator.nextLong()

    override fun nextBlock(size: Int): ByteArray = ByteArray(size).apply { generator.nextBytes(this) }

    override fun fork(): RandomGenerator = CMRandomGeneratorWrapper(nextLong(), builder)
}

fun RandomGenerator.asCMGenerator(): CMRandom =
    (this as? CMRandomGeneratorWrapper)?.generator ?: TODO("Implement reverse CM wrapper")


fun RandomGenerator.Companion.jdk(seed: Long? = null): RandomGenerator =
    CMRandomGeneratorWrapper(seed) { JDKRandomGenerator() }