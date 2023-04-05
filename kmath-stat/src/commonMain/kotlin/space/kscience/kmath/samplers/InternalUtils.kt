/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import kotlin.math.ln
import kotlin.math.min

internal object InternalUtils {
    private val FACTORIALS = longArrayOf(
        1L, 1L, 2L,
        6L, 24L, 120L,
        720L, 5040L, 40320L,
        362880L, 3628800L, 39916800L,
        479001600L, 6227020800L, 87178291200L,
        1307674368000L, 20922789888000L, 355687428096000L,
        6402373705728000L, 121645100408832000L, 2432902008176640000L
    )

    private const val BEGIN_LOG_FACTORIALS = 2

    fun factorial(n: Int): Long = FACTORIALS[n]

    fun validateProbabilities(probabilities: DoubleArray?): Double {
        require(!(probabilities == null || probabilities.isEmpty())) { "Probabilities must not be empty." }

        val sumProb = probabilities.sumOf { prob ->
            require(!(prob < 0 || prob.isInfinite() || prob.isNaN())) { "Invalid probability: $prob" }
            prob
        }

        require(!(sumProb.isInfinite() || sumProb <= 0)) { "Invalid sum of probabilities: $sumProb" }
        return sumProb
    }

    class FactorialLog private constructor(numValues: Int, cache: DoubleArray?) {
        private val logFactorials: DoubleArray = DoubleArray(numValues)

        init {
            val endCopy: Int

            if (cache != null && cache.size > BEGIN_LOG_FACTORIALS) {
                // Copy available values.
                endCopy = min(cache.size, numValues)

                cache.copyInto(
                    logFactorials,
                    BEGIN_LOG_FACTORIALS,
                    BEGIN_LOG_FACTORIALS,
                    endCopy,
                )
            } else
            // All values to be computed
                endCopy = BEGIN_LOG_FACTORIALS

            // Compute remaining values.
            (endCopy until numValues).forEach { i ->
                if (i < FACTORIALS.size)
                    logFactorials[i] = ln(FACTORIALS[i].toDouble())
                else
                    logFactorials[i] = logFactorials[i - 1] + ln(i.toDouble())
            }
        }

        fun value(n: Int): Double {
            if (n < logFactorials.size) return logFactorials[n]
            return if (n < FACTORIALS.size) ln(FACTORIALS[n].toDouble()) else InternalGamma.logGamma(n + 1.0)
        }

        companion object {
            fun create(): FactorialLog = FactorialLog(0, null)
        }
    }
}