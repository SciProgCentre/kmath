package scientifik.kmath.commons.rng.sampling.distribution

import scientifik.kmath.commons.rng.UniformRandomProvider
import kotlin.math.ceil
import kotlin.math.exp

class SmallMeanPoissonSampler :
    SharedStateDiscreteSampler {
    private val p0: Double
    private val limit: Int
    private val rng: UniformRandomProvider

    constructor(
        rng: UniformRandomProvider,
        mean: Double
    ) {
        this.rng = rng
        require(mean > 0) { "mean is not strictly positive: $mean" }
        p0 = exp(-mean)

        limit = (if (p0 > 0) ceil(1000 * mean) else throw IllegalArgumentException("No p(x=0) probability for mean: $mean")).toInt()
            // This excludes NaN values for the mean
        // else
        // The returned sample is bounded by 1000 * mean
    }

    private constructor(
        rng: UniformRandomProvider,
        source: SmallMeanPoissonSampler
    ) {
        this.rng = rng
        p0 = source.p0
        limit = source.limit
    }

    override fun sample(): Int {
        var n = 0
        var r = 1.0

        while (n < limit) {
            r *= rng.nextDouble()
            if (r >= p0) n++ else break
        }

        return n
    }

    override fun toString(): String = "Small Mean Poisson deviate [$rng]"

    override fun withUniformRandomProvider(rng: UniformRandomProvider): SharedStateDiscreteSampler =
        SmallMeanPoissonSampler(rng, this)

    companion object {
        fun of(
            rng: UniformRandomProvider,
            mean: Double
        ): SharedStateDiscreteSampler =
            SmallMeanPoissonSampler(rng, mean)
    }
}