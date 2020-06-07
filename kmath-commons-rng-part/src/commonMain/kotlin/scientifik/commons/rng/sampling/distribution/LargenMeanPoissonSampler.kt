package scientifik.commons.rng.sampling.distribution

import scientifik.commons.rng.UniformRandomProvider
import kotlin.math.*

class LargeMeanPoissonSampler : SharedStateDiscreteSampler {
    private val rng: UniformRandomProvider
    private val exponential: SharedStateContinuousSampler
    private val gaussian: SharedStateContinuousSampler
    private val factorialLog: InternalUtils.FactorialLog
    private val lambda: Double
    private val logLambda: Double
    private val logLambdaFactorial: Double
    private val delta: Double
    private val halfDelta: Double
    private val twolpd: Double
    private val p1: Double
    private val p2: Double
    private val c1: Double
    private val smallMeanPoissonSampler: SharedStateDiscreteSampler

    constructor(
        rng: UniformRandomProvider,
        mean: Double
    ) {
        require(mean >= 1) { "mean is not >= 1: $mean" }
        // The algorithm is not valid if Math.floor(mean) is not an integer.
        require(mean <= MAX_MEAN) { "mean $mean > $MAX_MEAN" }
        this.rng = rng
        gaussian = ZigguratNormalizedGaussianSampler(rng)
        exponential = AhrensDieterExponentialSampler.of(rng, 1.0)
        // Plain constructor uses the uncached function.
        factorialLog = NO_CACHE_FACTORIAL_LOG!!
        // Cache values used in the algorithm
        lambda = floor(mean)
        logLambda = ln(lambda)
        logLambdaFactorial = getFactorialLog(lambda.toInt())
        delta = sqrt(lambda * ln(32 * lambda / PI + 1))
        halfDelta = delta / 2
        twolpd = 2 * lambda + delta
        c1 = 1 / (8 * lambda)
        val a1: Double = sqrt(PI * twolpd) * exp(c1)
        val a2: Double = twolpd / delta * exp(-delta * (1 + delta) / twolpd)
        val aSum = a1 + a2 + 1
        p1 = a1 / aSum
        p2 = a2 / aSum

        // The algorithm requires a Poisson sample from the remaining lambda fraction.
        val lambdaFractional = mean - lambda
        smallMeanPoissonSampler =
            if (lambdaFractional < Double.MIN_VALUE) NO_SMALL_MEAN_POISSON_SAMPLER else  // Not used.
                KempSmallMeanPoissonSampler.of(rng, lambdaFractional)
    }

    internal constructor(
        rng: UniformRandomProvider,
        state: LargeMeanPoissonSamplerState,
        lambdaFractional: Double
    ) {
        require(!(lambdaFractional < 0 || lambdaFractional >= 1)) { "lambdaFractional must be in the range 0 (inclusive) to 1 (exclusive): $lambdaFractional" }
        this.rng = rng
        gaussian = ZigguratNormalizedGaussianSampler(rng)
        exponential = AhrensDieterExponentialSampler.of(rng, 1.0)
        // Plain constructor uses the uncached function.
        factorialLog = NO_CACHE_FACTORIAL_LOG!!
        // Use the state to initialise the algorithm
        lambda = state.lambdaRaw
        logLambda = state.logLambda
        logLambdaFactorial = state.logLambdaFactorial
        delta = state.delta
        halfDelta = state.halfDelta
        twolpd = state.twolpd
        p1 = state.p1
        p2 = state.p2
        c1 = state.c1

        // The algorithm requires a Poisson sample from the remaining lambda fraction.
        smallMeanPoissonSampler =
            if (lambdaFractional < Double.MIN_VALUE)
                NO_SMALL_MEAN_POISSON_SAMPLER
            else  // Not used.
                KempSmallMeanPoissonSampler.of(rng, lambdaFractional)
    }

    /**
     * @param rng Generator of uniformly distributed random numbers.
     * @param source Source to copy.
     */
    private constructor(
        rng: UniformRandomProvider,
        source: LargeMeanPoissonSampler
    ) {
        this.rng = rng
        gaussian = source.gaussian.withUniformRandomProvider(rng)!!
        exponential = source.exponential.withUniformRandomProvider(rng)!!
        // Reuse the cache
        factorialLog = source.factorialLog
        lambda = source.lambda
        logLambda = source.logLambda
        logLambdaFactorial = source.logLambdaFactorial
        delta = source.delta
        halfDelta = source.halfDelta
        twolpd = source.twolpd
        p1 = source.p1
        p2 = source.p2
        c1 = source.c1

        // Share the state of the small sampler
        smallMeanPoissonSampler = source.smallMeanPoissonSampler.withUniformRandomProvider(rng)!!
    }

    /** {@inheritDoc}  */
    override fun sample(): Int {
        // This will never be null. It may be a no-op delegate that returns zero.
        val y2: Int = smallMeanPoissonSampler.sample()
        var x: Double
        var y: Double
        var v: Double
        var a: Int
        var t: Double
        var qr: Double
        var qa: Double
        while (true) {
            // Step 1:
            val u = rng.nextDouble()

            if (u <= p1) {
                // Step 2:
                val n = gaussian.sample()
                x = n * sqrt(lambda + halfDelta) - 0.5
                if (x > delta || x < -lambda) continue
                y = if (x < 0) floor(x) else ceil(x)
                val e = exponential.sample()
                v = -e - 0.5 * n * n + c1
            } else {
                // Step 3:
                if (u > p1 + p2) {
                    y = lambda
                    break
                }

                x = delta + twolpd / delta * exponential.sample()
                y = ceil(x)
                v = -exponential.sample() - delta * (x + 1) / twolpd
            }
            // The Squeeze Principle
            // Step 4.1:
            a = if (x < 0) 1 else 0
            t = y * (y + 1) / (2 * lambda)

            // Step 4.2
            if (v < -t && a == 0) {
                y += lambda
                break
            }

            // Step 4.3:
            qr = t * ((2 * y + 1) / (6 * lambda) - 1)
            qa = qr - t * t / (3 * (lambda + a * (y + 1)))

            // Step 4.4:
            if (v < qa) {
                y += lambda
                break
            }

            // Step 4.5:
            if (v > qr) continue

            // Step 4.6:
            if (v < y * logLambda - getFactorialLog((y + lambda).toInt()) + logLambdaFactorial) {
                y += lambda
                break
            }
        }

        return min(y2 + y.toLong(), Int.MAX_VALUE.toLong()).toInt()
    }


    private fun getFactorialLog(n: Int): Double = factorialLog.value(n)

    override fun toString(): String = "Large Mean Poisson deviate [$rng]"

    override fun withUniformRandomProvider(rng: UniformRandomProvider): SharedStateDiscreteSampler =
        LargeMeanPoissonSampler(rng, this)

    val state: LargeMeanPoissonSamplerState
        get() = LargeMeanPoissonSamplerState(
            lambda, logLambda, logLambdaFactorial,
            delta, halfDelta, twolpd, p1, p2, c1
        )

    class LargeMeanPoissonSamplerState(
        val lambdaRaw: Double,
        val logLambda: Double,
        val logLambdaFactorial: Double,
        val delta: Double,
        val halfDelta: Double,
        val twolpd: Double,
        val p1: Double,
        val p2: Double,
        val c1: Double
    ) {
        fun getLambda(): Int = lambdaRaw.toInt()
    }

    companion object {
        private const val MAX_MEAN = 0.5 * Int.MAX_VALUE
        private var NO_CACHE_FACTORIAL_LOG: InternalUtils.FactorialLog? = null

        private val NO_SMALL_MEAN_POISSON_SAMPLER: SharedStateDiscreteSampler =
            object : SharedStateDiscreteSampler {
                override fun withUniformRandomProvider(rng: UniformRandomProvider): SharedStateDiscreteSampler =
// No requirement for RNG
                    this

                override fun sample(): Int =// No Poisson sample
                    0
            }

        fun of(
            rng: UniformRandomProvider,
            mean: Double
        ): SharedStateDiscreteSampler = LargeMeanPoissonSampler(rng, mean)

        init {
            // Create without a cache.
            NO_CACHE_FACTORIAL_LOG =
                InternalUtils.FactorialLog.create()
        }
    }
}