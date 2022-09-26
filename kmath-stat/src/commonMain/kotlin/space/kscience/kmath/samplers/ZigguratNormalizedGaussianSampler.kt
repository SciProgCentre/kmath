/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import space.kscience.kmath.chains.BlockingDoubleChain
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.structures.DoubleBuffer
import kotlin.math.*

/**
 * [Marsaglia and Tsang "Ziggurat"](https://en.wikipedia.org/wiki/Ziggurat_algorithm) method for sampling from a
 * Gaussian distribution with mean 0 and standard deviation 1. The algorithm is explained in this paper and this
 * implementation has been adapted from the C code provided therein.
 *
 * Based on Commons RNG implementation.
 * See [https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/ZigguratNormalizedGaussianSampler.html].
 */
public object ZigguratNormalizedGaussianSampler : NormalizedGaussianSampler {

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

    private fun gauss(x: Double): Double = exp(-0.5 * x * x)

    private fun sampleOne(generator: RandomGenerator): Double {
        val j = generator.nextLong()
        val i = (j and LAST.toLong()).toInt()
        return if (abs(j) < K[i]) j * W[i] else fix(generator, j, i)
    }

    override fun sample(generator: RandomGenerator): BlockingDoubleChain = object : BlockingDoubleChain {
        override fun nextBufferBlocking(size: Int): DoubleBuffer = DoubleBuffer(size) { sampleOne(generator) }

        override suspend fun fork(): BlockingDoubleChain = sample(generator.fork())
    }


    private fun fix(generator: RandomGenerator, hz: Long, iz: Int): Double {
        var x = hz * W[iz]

        return when {
            iz == 0 -> {
                var y: Double

                do {
                    y = -ln(generator.nextDouble())
                    x = -ln(generator.nextDouble()) * ONE_OVER_R
                } while (y + y < x * x)

                val out = R + x
                if (hz > 0) out else -out
            }

            F[iz] + generator.nextDouble() * (F[iz - 1] - F[iz]) < gauss(x) -> x
            else -> sampleOne(generator)
        }
    }

}
