package scientifik.kmath.commons.rng.sampling.distribution

import scientifik.kmath.commons.rng.UniformRandomProvider
import kotlin.math.ln
import kotlin.math.pow

class AhrensDieterExponentialSampler : SamplerBase,
    SharedStateContinuousSampler {
    private val mean: Double
    private val rng: UniformRandomProvider

    constructor(
        rng: UniformRandomProvider,
        mean: Double
    ) : super(null) {
        require(mean > 0) { "mean is not strictly positive: $mean" }
        this.rng = rng
        this.mean = mean
    }

    private constructor(
        rng: UniformRandomProvider,
        source: AhrensDieterExponentialSampler
    ) : super(null) {
        this.rng = rng
        mean = source.mean
    }

    override fun sample(): Double {
        // Step 1:
        var a = 0.0
        var u: Double = rng.nextDouble()

        // Step 2 and 3:
        while (u < 0.5) {
            a += EXPONENTIAL_SA_QI.get(
                0
            )
            u *= 2.0
        }

        // Step 4 (now u >= 0.5):
        u += u - 1

        // Step 5:
        if (u <= EXPONENTIAL_SA_QI.get(
                0
            )
        ) {
            return mean * (a + u)
        }

        // Step 6:
        var i = 0 // Should be 1, be we iterate before it in while using 0.
        var u2: Double = rng.nextDouble()
        var umin = u2

        // Step 7 and 8:
        do {
            ++i
            u2 = rng.nextDouble()
            if (u2 < umin) umin = u2
            // Step 8:
        } while (u > EXPONENTIAL_SA_QI[i]) // Ensured to exit since EXPONENTIAL_SA_QI[MAX] = 1.
        return mean * (a + umin * EXPONENTIAL_SA_QI[0])
    }

    override fun toString(): String = "Ahrens-Dieter Exponential deviate [$rng]"

    override fun withUniformRandomProvider(rng: UniformRandomProvider): SharedStateContinuousSampler =
        AhrensDieterExponentialSampler(rng, this)

    companion object {
        private val EXPONENTIAL_SA_QI = DoubleArray(16)

        fun of(
            rng: UniformRandomProvider,
            mean: Double
        ): SharedStateContinuousSampler =
            AhrensDieterExponentialSampler(
                rng,
                mean
            )

        init {
            /**
             * Filling EXPONENTIAL_SA_QI table.
             * Note that we don't want qi = 0 in the table.
             */
            val ln2 = ln(2.0)
            var qi = 0.0

            EXPONENTIAL_SA_QI.indices.forEach { i ->
                qi += ln2.pow(i + 1.0) / InternalUtils.factorial(
                    i + 1
                )
                EXPONENTIAL_SA_QI[i] = qi
            }
        }
    }
}
