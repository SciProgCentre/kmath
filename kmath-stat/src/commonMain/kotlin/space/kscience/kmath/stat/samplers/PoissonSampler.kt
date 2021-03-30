package space.kscience.kmath.stat.samplers

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.stat.RandomGenerator
import space.kscience.kmath.stat.Sampler

/**
 * Sampler for the Poisson distribution.
 * - For small means, a Poisson process is simulated using uniform deviates, as described in
 *   Knuth (1969). Seminumerical Algorithms. The Art of Computer Programming, Volume 2. Chapter 3.4.1.F.3
 *   Important integer-valued distributions: The Poisson distribution. Addison Wesley.
 * The Poisson process (and hence, the returned value) is bounded by 1000 * mean.
 * - For large means, we use the rejection algorithm described in
 *   Devroye, Luc. (1981). The Computer Generation of Poisson Random Variables Computing vol. 26 pp. 197-207.
 *
 * Based on Commons RNG implementation.
 * See [https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/PoissonSampler.html].
 */
public class PoissonSampler private constructor(mean: Double) : Sampler<Int> {
    private val poissonSamplerDelegate: Sampler<Int> = of(mean)
    public override fun sample(generator: RandomGenerator): Chain<Int> = poissonSamplerDelegate.sample(generator)
    public override fun toString(): String = poissonSamplerDelegate.toString()

    public companion object {
        private const val PIVOT = 40.0

        public fun of(mean: Double): Sampler<Int> =// Each sampler should check the input arguments.
            if (mean < PIVOT) SmallMeanPoissonSampler.of(mean) else LargeMeanPoissonSampler.of(mean)
    }
}
