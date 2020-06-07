package scientifik.commons.rng.sampling.distribution

import scientifik.commons.rng.UniformRandomProvider
import kotlin.math.ln
import kotlin.math.sqrt

class MarsagliaNormalizedGaussianSampler(private val rng: UniformRandomProvider) :
    NormalizedGaussianSampler,
    SharedStateContinuousSampler {
    private var nextGaussian = Double.NaN

    override fun sample(): Double {
        if (nextGaussian.isNaN()) {
            // Rejection scheme for selecting a pair that lies within the unit circle.
            while (true) {
                // Generate a pair of numbers within [-1 , 1).
                val x = 2.0 * rng.nextDouble() - 1.0
                val y = 2.0 * rng.nextDouble() - 1.0
                val r2 = x * x + y * y
                if (r2 < 1 && r2 > 0) {
                    // Pair (x, y) is within unit circle.
                    val alpha = sqrt(-2 * ln(r2) / r2)

                    // Keep second element of the pair for next invocation.
                    nextGaussian = alpha * y

                    // Return the first element of the generated pair.
                    return alpha * x
                }

                // Pair is not within the unit circle: Generate another one.
            }
        } else {
            // Use the second element of the pair (generated at the
            // previous invocation).
            val r = nextGaussian

            // Both elements of the pair have been used.
            nextGaussian = Double.NaN
            return r
        }
    }

    override fun toString(): String = "Box-Muller (with rejection) normalized Gaussian deviate [$rng]"

    override fun withUniformRandomProvider(rng: UniformRandomProvider): SharedStateContinuousSampler =
        MarsagliaNormalizedGaussianSampler(rng)

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <S> of(rng: UniformRandomProvider): S where S : NormalizedGaussianSampler?, S : SharedStateContinuousSampler? =
            MarsagliaNormalizedGaussianSampler(rng) as S
    }
}
