/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.misc.binomialCoefficient
import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.Ring

/**
 * Apply integer differencing operation to the buffer.
 *
 * The result of differencing is not a derivative in the function sense since it does not divide by step.
 */
public fun <T, A : Field<T>> SeriesAlgebra<T, A, *, *>.differentiate(
    series: Series<T>,
    diffOrder: Int = 1,
): Series<T> {
    return when (diffOrder) {
        0 -> series
        1 -> series.difference(1)
        else -> {
            val buffers = (0 until diffOrder).map { offset -> series.moveBy(offset) }
            val coefficients = (0 until diffOrder).map { i ->
                (if (i % 2 == 0) 1.0 else -1.0) * IntRing.binomialCoefficient(diffOrder, i)
            }

            with(elementAlgebra) {
                bufferFactory(series.size) { i ->
                    (0 until diffOrder).fold(zero) { acc, order ->
                        acc + (buffers[order][i] * coefficients[order])
                    }
                }.asSeries(series.position)
            }

        }
    }
}

/**
 * Accumulate elements in a buffer into a new buffer.
 */
public fun <T, A : Ring<T>> SeriesAlgebra<T, A, *, *>.integrate(
    series: Series<T>,
    initialValue: T,
): Series<T> = with(elementAlgebra) {

    var accumulator: T = initialValue
    val result = bufferFactory(series.size + 1) { i ->
        accumulator.also {
            if (i < series.size) {
                accumulator += series.origin[i]
            }
        }
    }

    return result.asSeries(series.position)
}