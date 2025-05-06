/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.Float32Field
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.Float32
import space.kscience.kmath.structures.Float64

/**
 * The standard deviation.
 *
 * Standard deviation is the positive square root of the variance. This implementation use a [Variance] class.
 * The [isBiasCorrected] property determines whether to compute:
 * - The "sample standard deviation" (square root of bias-corrected sample variance) when true
 * - The "population standard deviation" (square root of non-bias-corrected population variance) when false
 *
 * @param T the type of elements in the dataset
 * @property field The [ExtendedField] used for mathematical operations
 * @property isBiasCorrected If true, applies Bessel's correction (n-1 denominator) for sample variance
 *
 * @see Variance for more information about variance calculation
 */
public class StandardDeviation<T>(
    private val field: ExtendedField<T>,
    private val isBiasCorrected: Boolean = true
) : BlockingStatistic<T, T> {
    override fun evaluateBlocking(data: Buffer<T>): T {
        return field.sqrt(Variance(field, isBiasCorrected).evaluateBlocking(data))
    }

    public companion object {
        public fun evaluate(buffer: Buffer<Float64>): Float64 = Float64Field.std.evaluateBlocking(buffer)
        public fun evaluate(buffer: Buffer<Float32>): Float32 = Float32Field.std.evaluateBlocking(buffer)
    }
}

public val Float64Field.std: StandardDeviation<Float64> get() = StandardDeviation(Float64Field)
public val Float32Field.std: StandardDeviation<Float32> get() = StandardDeviation(Float32Field)