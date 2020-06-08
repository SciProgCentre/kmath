package scientifik.kmath.prob.samplers

import scientifik.kmath.chains.Chain
import scientifik.kmath.prob.RandomGenerator
import scientifik.kmath.prob.Sampler
import scientifik.kmath.prob.chain
import kotlin.math.exp

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
            val p0: Double = exp(-mean)
            // Probability must be positive. As mean increases then p(0) decreases.
            if (p0 > 0) return KempSmallMeanPoissonSampler(p0, mean)
            throw IllegalArgumentException("No probability for mean: $mean")
        }
    }
}

