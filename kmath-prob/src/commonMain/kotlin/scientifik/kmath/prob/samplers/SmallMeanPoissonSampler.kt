package scientifik.kmath.prob.samplers

import scientifik.kmath.chains.Chain
import scientifik.kmath.chains.SimpleChain
import scientifik.kmath.prob.RandomGenerator
import scientifik.kmath.prob.Sampler
import scientifik.kmath.prob.chain
import kotlin.math.ceil
import kotlin.math.exp

class SmallMeanPoissonSampler(mean: Double) : Sampler<Int> {
    private val p0: Double
    private val limit: Int

    init {
        require(mean > 0) { "mean is not strictly positive: $mean" }
        p0 = exp(-mean)

        limit = (if (p0 > 0)
            ceil(1000 * mean)
        else
            throw IllegalArgumentException("No p(x=0) probability for mean: $mean")).toInt()
    }

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
        fun of(mean: Double): SmallMeanPoissonSampler =
            SmallMeanPoissonSampler(mean)
    }
}
