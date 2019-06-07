package scientifik.kmath.prob

import org.apache.commons.rng.UniformRandomProvider

inline class CommonsRandomProviderWrapper(val provider: UniformRandomProvider) : RandomGenerator {
    override fun nextDouble(): Double = provider.nextDouble()

    override fun nextInt(): Int = provider.nextInt()

    override fun nextLong(): Long = provider.nextLong()

    override fun nextBlock(size: Int): ByteArray = ByteArray(size).also { provider.nextBytes(it) }
}

fun UniformRandomProvider.asGenerator(): RandomGenerator = CommonsRandomProviderWrapper(this)

fun RandomGenerator.asProvider(): UniformRandomProvider =
    (this as? CommonsRandomProviderWrapper)?.provider ?: TODO("implement reverse wrapper")