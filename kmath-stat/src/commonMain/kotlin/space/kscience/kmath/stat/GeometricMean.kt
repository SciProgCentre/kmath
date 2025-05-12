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
import space.kscience.kmath.structures.indices

/**
 * Geometric mean. Nth root of product of values.
 */
public class GeometricMean<T : Comparable<T>>(
    private val field: ExtendedField<T>
) : BlockingStatistic<T, T>, ComposableStatistic<T, Pair<T, Int>, T> {

    private fun logsum(data: Buffer<T>): T = with(field) {
        require(data.size > 0) { "Data must not be empty" }
        var res = zero
        for (i in data.indices) {
            if (data[i] < zero) throw ArithmeticException("Geometric mean is not defined for negative numbers. Found: " + data[i])
            res += ln(data[i])
        }
        res
    }

    override fun evaluateBlocking(data: Buffer<T>): T = with(field) {
        exp(logsum(data) / data.size)
    }

    override suspend fun computeIntermediate(data: Buffer<T>): Pair<T, Int> = logsum(data) to data.size

    override suspend fun composeIntermediate(first: Pair<T, Int>, second: Pair<T, Int>): Pair<T, Int> = with(field) {
        first.first + second.first to first.second + second.second
    }

    override suspend fun toResult(intermediate: Pair<T, Int>): T =
        with(field) { exp(intermediate.first / intermediate.second) }

    override suspend fun evaluate(data: Buffer<T>): T = super<ComposableStatistic>.evaluate(data)
}

public val Float32Field.geometricMean: GeometricMean<Float32> get() = GeometricMean(Float32Field)
public val Float64Field.geometricMean: GeometricMean<Float64> get() = GeometricMean(Float64Field)
