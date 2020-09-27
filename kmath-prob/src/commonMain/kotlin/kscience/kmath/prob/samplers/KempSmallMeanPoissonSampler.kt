package scientifik.kmath.prob.samplers

import scientifik.kmath.chains.Chain
import scientifik.kmath.prob.RandomGenerator
import scientifik.kmath.prob.Sampler
import scientifik.kmath.prob.chain
import kotlin.math.exp

/**
 * Sampler for the Poisson distribution.
 * - Kemp, A, W, (1981) Efficient Generation of Logarithmically Distributed Pseudo-Random Variables. Journal of the Royal Statistical Society. Vol. 30, No. 3, pp. 249-253.
 * This sampler is suitable for mean < 40. For large means, LargeMeanPoissonSampler should be used instead.
 *
 * Note: The algorithm uses a recurrence relation to compute the Poisson probability and a rolling summation for the cumulative probability. When the mean is large the initial probability (Math.exp(-mean)) is zero and an exception is raised by the constructor.
 *
 * Sampling uses 1 call to UniformRandomProvider.nextDouble(). This method provides an alternative to the SmallMeanPoissonSampler for slow generators of double.
 *
 * Based on Commons RNG implementation.
 * See https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/KempSmallMeanPoissonSampler.html
 */
class KempSmallMeanPoissonSampler private constructor(
    private val p0: Double,
    private val mean: Double
) : Sampler<Int> {
    override fun sample(generator: RandomGenerator): Chain<Int> = generator.chain {
        // Note on the algorithm:
        // - X is the unknown sample deviate (the output of the algorithm)
        // - x is the current value from the distribution
        // - p is the probability of the current value x, p(X=x)
        // - u is effectively the cumulative probability that the sample X
        //   is equal or above the current value x, p(X>=x)
        // So if p(X>=x) > p(X=x) the sample must be above x, otherwise it is x
        var u = nextDouble()
        var x = 0
        var p = p0

        while (u > p) {
            u -= p
            // Compute the next probability using a recurrence relation.
            // p(x+1) = p(x) * mean / (x+1)
            p *= mean / ++x
            // The algorithm listed in Kemp (1981) does not check that the rolling probability
            // is positive. This check is added to ensure no errors when the limit of the summation
            // 1 - sum(p(x)) is above 0 due to cumulative error in floating point arithmetic.
            if (p == 0.0) return@chain x
        }

        x
    }

    override fun toString(): String = "Kemp Small Mean Poisson deviate"

    companion object {
        fun of(mean: Double): KempSmallMeanPoissonSampler {
            require(mean > 0) { "Mean is not strictly positive: $mean" }
            val p0 = exp(-mean)
            // Probability must be positive. As mean increases then p(0) decreases.
            require(p0 > 0) { "No probability for mean: $mean" }
            return KempSmallMeanPoissonSampler(p0, mean)
        }
    }
}

