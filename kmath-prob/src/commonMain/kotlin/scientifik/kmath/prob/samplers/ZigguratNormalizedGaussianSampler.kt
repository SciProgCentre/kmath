package scientifik.kmath.prob.samplers

import scientifik.kmath.chains.Chain
import scientifik.kmath.prob.RandomGenerator
import scientifik.kmath.prob.Sampler
import scientifik.kmath.prob.chain
import kotlin.math.*

/**
 * Based on commons-rng implementation.
 *
 * See https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/ZigguratNormalizedGaussianSampler.html
 */
class ZigguratNormalizedGaussianSampler private constructor() :
    NormalizedGaussianSampler, Sampler<Double> {

    private fun sampleOne(generator: RandomGenerator): Double {
        val j = generator.nextLong()
        val i = (j and LAST.toLong()).toInt()
        return if (abs(j) < K[i]) j * W[i] else fix(generator, j, i)
    }

    override fun sample(generator: RandomGenerator): Chain<Double> = generator.chain { sampleOne(this) }

    override fun toString(): String = "Ziggurat normalized Gaussian deviate"

    private fun fix(
        generator: RandomGenerator,
        hz: Long,
        iz: Int
    ): Double {
        var x: Double
        var y: Double
        x = hz * W[iz]

        return if (iz == 0) {
            // Base strip.
            // This branch is called about 5.7624515E-4 times per sample.
            do {
                y = -ln(generator.nextDouble())
                x = -ln(generator.nextDouble()) * ONE_OVER_R
            } while (y + y < x * x)

            val out = R + x
            if (hz > 0) out else -out
        } else {
            // Wedge of other strips.
            // This branch is called about 0.027323 times per sample.
            // else
            // Try again.
            // This branch is called about 0.012362 times per sample.
            if (F[iz] + generator.nextDouble() * (F[iz - 1] - F[iz]) < gauss(
                    x
                )
            ) x else sampleOne(generator)
        }
    }

    companion object {
        private const val R: Double = 3.442619855899
        private const val ONE_OVER_R: Double = 1 / R
        private const val V: Double = 9.91256303526217e-3
        private val MAX: Double = 2.0.pow(63.0)
        private val ONE_OVER_MAX: Double = 1.0 / MAX
        private const val LEN: Int = 128
        private const val LAST: Int = LEN - 1
        private val K: LongArray = LongArray(LEN)
        private val W: DoubleArray = DoubleArray(LEN)
        private val F: DoubleArray = DoubleArray(LEN)

        init {
            // Filling the tables.
            var d = R
            var t = d
            var fd = gauss(d)
            val q = V / fd
            K[0] = (d / q * MAX).toLong()
            K[1] = 0
            W[0] = q * ONE_OVER_MAX
            W[LAST] = d * ONE_OVER_MAX
            F[0] = 1.0
            F[LAST] = fd

            (LAST - 1 downTo 1).forEach { i ->
                d = sqrt(-2 * ln(V / d + fd))
                fd = gauss(d)
                K[i + 1] = (d / t * MAX).toLong()
                t = d
                F[i] = fd
                W[i] = d * ONE_OVER_MAX
            }
        }

        fun of(): ZigguratNormalizedGaussianSampler = ZigguratNormalizedGaussianSampler()
        private fun gauss(x: Double): Double = exp(-0.5 * x * x)
    }
}
