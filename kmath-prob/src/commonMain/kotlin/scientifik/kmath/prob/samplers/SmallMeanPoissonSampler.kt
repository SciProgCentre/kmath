package scientifik.kmath.prob.samplers

import scientifik.kmath.chains.Chain
import scientifik.kmath.prob.RandomGenerator
import scientifik.kmath.prob.Sampler
import scientifik.kmath.prob.chain
import kotlin.math.ceil
import kotlin.math.exp

/**
 * Based on commons-rng implementation.
 *
 * See https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/SmallMeanPoissonSampler.html
 */
class SmallMeanPoissonSampler private constructor(mean: Double) : Sampler<Int> {
    private val p0: Double = exp(-mean)

    private val limit: Int = (if (p0 > 0)
        ceil(1000 * mean)
    else
        throw IllegalArgumentException("No p(x=0) probability for mean: $mean")).toInt()

    override fun sample(generator: RandomGenerator): Chain<Int> = generator.chain {
        var n = 0
        var r = 1.0

        while (n < limit) {
            r *= nextDouble()
            if (r >= p0) n++ else break
        }

        n
    }

    override fun toString(): String = "Small Mean Poisson deviate"

    companion object {
        fun of(mean: Double): SmallMeanPoissonSampler {
            require(mean > 0) { "mean is not strictly positive: $mean" }
            return SmallMeanPoissonSampler(mean)
        }
    }
}
