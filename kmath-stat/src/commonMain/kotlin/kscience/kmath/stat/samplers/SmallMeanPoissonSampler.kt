package kscience.kmath.stat.samplers

import kscience.kmath.chains.Chain
import kscience.kmath.stat.RandomGenerator
import kscience.kmath.stat.Sampler
import kscience.kmath.stat.chain
import kotlin.math.ceil
import kotlin.math.exp

/**
 * Sampler for the Poisson distribution.
 * - For small means, a Poisson process is simulated using uniform deviates, as described in
 *   Knuth (1969). Seminumerical Algorithms. The Art of Computer Programming, Volume 2. Chapter 3.4.1.F.3 Important
 *   integer-valued distributions: The Poisson distribution. Addison Wesley.
 * - The Poisson process (and hence, the returned value) is bounded by 1000 * mean.
 *   This sampler is suitable for mean < 40. For large means, [LargeMeanPoissonSampler] should be used instead.
 *
 * Based on Commons RNG implementation.
 *
 * See [https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/SmallMeanPoissonSampler.html].
 */
public class SmallMeanPoissonSampler private constructor(mean: Double) : Sampler<Int> {
    private val p0: Double = exp(-mean)

    private val limit: Int = (if (p0 > 0)
        ceil(1000 * mean)
    else
        throw IllegalArgumentException("No p(x=0) probability for mean: $mean")).toInt()

    public override fun sample(generator: RandomGenerator): Chain<Int> = generator.chain {
        var n = 0
        var r = 1.0

        while (n < limit) {
            r *= nextDouble()
            if (r >= p0) n++ else break
        }

        n
    }

    public override fun toString(): String = "Small Mean Poisson deviate"

    public companion object {
        public fun of(mean: Double): SmallMeanPoissonSampler {
            require(mean > 0) { "mean is not strictly positive: $mean" }
            return SmallMeanPoissonSampler(mean)
        }
    }
}
