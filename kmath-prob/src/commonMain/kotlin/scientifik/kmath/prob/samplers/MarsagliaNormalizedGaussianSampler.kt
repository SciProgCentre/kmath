package scientifik.kmath.prob.samplers

import scientifik.kmath.chains.Chain
import scientifik.kmath.prob.RandomGenerator
import scientifik.kmath.prob.Sampler
import scientifik.kmath.prob.chain
import kotlin.math.ln
import kotlin.math.sqrt

class MarsagliaNormalizedGaussianSampler : NormalizedGaussianSampler, Sampler<Double> {
    private var nextGaussian = Double.NaN

    override fun sample(generator: RandomGenerator): Chain<Double> = generator.chain {
        if (nextGaussian.isNaN()) {
            val alpha: Double
            var x: Double

            // Rejection scheme for selecting a pair that lies within the unit circle.
            while (true) {
                // Generate a pair of numbers within [-1 , 1).
                x = 2.0 * generator.nextDouble() - 1.0
                val y = 2.0 * generator.nextDouble() - 1.0
                val r2 = x * x + y * y

                if (r2 < 1 && r2 > 0) {
                    // Pair (x, y) is within unit circle.
                    alpha = sqrt(-2 * ln(r2) / r2)

                    // Keep second element of the pair for next invocation.
                    nextGaussian = alpha * y

                    // Return the first element of the generated pair.
                    break
                }
                // Pair is not within the unit circle: Generate another one.
            }

            // Return the first element of the generated pair.
            alpha * x
        } else {
            // Use the second element of the pair (generated at the
            // previous invocation).
            val r = nextGaussian

            // Both elements of the pair have been used.
            nextGaussian = Double.NaN
            r
        }
    }

    override fun toString(): String = "Box-Muller (with rejection) normalized Gaussian deviate"
}
