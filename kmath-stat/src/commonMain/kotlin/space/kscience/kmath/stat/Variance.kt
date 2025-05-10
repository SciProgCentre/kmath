/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.Float32Field
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.Int32Field
import space.kscience.kmath.operations.Int32Ring
import space.kscience.kmath.operations.Int64Field
import space.kscience.kmath.operations.Int64Ring
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.Float32
import space.kscience.kmath.structures.Float64
import space.kscience.kmath.structures.Int32
import space.kscience.kmath.structures.Int64

/**
 * Variance of the available values.
 *
 * By default, the unbiased "sample variance" definitional formula is used:
 *
 * The variance is computed as:
 * - Unbiased: σ² = Σ (xᵢ - μ)² / (n - 1)
 * - Biased:   σ² = Σ (xᵢ - μ)² / n
 * where μ is the sample mean and `n` is the number of sample observations.
 *
 *
 * The `isBiasCorrected` property determines whether the "population" (biased) or "sample" (unbiased)
 * value is returned.
 */
public class Variance<T>(
    private val field: Field<T>,
    private val isBiasCorrected: Boolean = true
) : BlockingStatistic<T, T> {

    /**
     * Computes the variance of the elements in the given buffer using Welford's online algorithm.
     * This method operates in a single pass and supports both biased and unbiased (Bessel's correction) variance.
     *
     * @param data The input buffer containing the elements to compute the variance for.
     * @return The computed variance as a value of type [T].
     * @throws IllegalArgumentException If the buffer size is less than 2 (variance requires at least 2 elements).
     *
     * Algorithm Details:
     * - Uses Welford's method for numerical stability in a single pass.
     * - For unbiased variance (Bessel's correction), divides by (n-1).
     * - For biased variance, divides by n.
     *
     * Math Background:
     * The variance is computed as:
     * - Unbiased: σ² = Σ (xᵢ - μ)² / (n - 1)
     * - Biased:   σ² = Σ (xᵢ - μ)² / n
     * where μ is the sample mean.
     *
     * The implementation avoids catastrophic cancellation by using the recurrence relation:
     * M₂ = M₂ + (xᵢ - meanₙ₋₁) * (xᵢ - meanₙ)
     * where meanₙ is the updated mean after including xᵢ.
     */
    override fun evaluateBlocking(data: Buffer<T>): T = with(field) {
        if (data.size < 2) return zero // Variance requires at least 2 elements

        //  Recursively updates mean and variance in a single pass.
        var mean = data[0]
        var m2 = zero
        var delta: T

        for (i in 1 until data.size) {
            delta = data[i] - mean
            mean += delta / (i + 1)
            m2 += delta * (data[i] - mean)
        }

        if (isBiasCorrected) {
            return m2 / (data.size - 1)
        } else {
            return m2 / (data.size)
        }
    }

    /**
     * Calculates the variance of sample data given a precomputed mean.
     * The method use "corrected two-pass algorithm" from Chan, Golub, Levesque, "Algorithms for Computing the Sample Variance",
     * American Statistician, vol. 37, no. 3 (1983) pp. 242-247 for numerical stability.
     *
     * @param data The input data buffer.
     * @param mean Precomputed mean of the data.
     * @return Variance of the data (biased or unbiased based on [isBiasCorrected]).
     */
    public fun evaluate(data: Buffer<T>, mean: T): T = with(field) {
        if (data.size < 2) return zero // Variance requires at least 2 elements

        var sumOfSquaredDeviations = zero
        var sumOfDeviations = zero

        for (i in 0 until data.size) {
            val deviation = data[i] - mean
            sumOfDeviations += deviation
            sumOfSquaredDeviations += deviation * deviation
        }

        return if (isBiasCorrected) {
            (sumOfSquaredDeviations - (sumOfDeviations * sumOfDeviations) / data.size) / (data.size - 1)
        } else {
            (sumOfSquaredDeviations - (sumOfDeviations * sumOfDeviations)) / data.size
        }
    }

    public companion object {
        public fun evaluate(buffer: Buffer<Float64>): Float64 = Float64Field.variance.evaluateBlocking(buffer)
        public fun evaluate(buffer: Buffer<Float32>): Float32 = Float32Field.variance.evaluateBlocking(buffer)
    }

}

public val Float64Field.variance: Variance<Float64> get() = Variance(Float64Field)
public val Float32Field.variance: Variance<Float32> get() = Variance(Float32Field)
