package scientifik.kmath.commons.rng.sampling.distribution

import scientifik.kmath.commons.rng.UniformRandomProvider
import kotlin.math.*

class BoxMullerNormalizedGaussianSampler(
    private val rng: UniformRandomProvider
) :
    NormalizedGaussianSampler,
    SharedStateContinuousSampler {
    private var nextGaussian: Double = Double.NaN

    override fun sample(): Double {
        val random: Double

        if (nextGaussian.isNaN()) {
            // Generate a pair of Gaussian numbers.
            val x = rng.nextDouble()
            val y = rng.nextDouble()
            val alpha = 2 * PI * x
            val r = sqrt(-2 * ln(y))

            // Return the first element of the generated pair.
            random = r * cos(alpha)

            // Keep second element of the pair for next invocation.
            nextGaussian = r * sin(alpha)
        } else {
            // Use the second element of the pair (generated at the
            // previous invocation).
            random = nextGaussian

            // Both elements of the pair have been used.
            nextGaussian = Double.NaN
        }

        return random
    }

    override fun toString(): String = "Box-Muller normalized Gaussian deviate [$rng]"

    override fun withUniformRandomProvider(rng: UniformRandomProvider): SharedStateContinuousSampler =
        BoxMullerNormalizedGaussianSampler(rng)

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <S> of(rng: UniformRandomProvider): S where S : NormalizedGaussianSampler?, S : SharedStateContinuousSampler? =
            BoxMullerNormalizedGaussianSampler(rng) as S
    }
}
