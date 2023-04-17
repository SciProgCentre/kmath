/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.data

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.structures.Buffer

/**
 * A column-based data set with all columns of the same size (not necessary fixed in time).
 * The column could be retrieved by a [get] operation.
 */
@UnstableKMathAPI
public interface ColumnarData<out T> {
    public val size: Int

    /**
     * Provide a column by symbol or null if column with given symbol is not defined
     */
    public operator fun get(symbol: Symbol): Buffer<T>?
}

@UnstableKMathAPI
public val ColumnarData<*>.indices: IntRange get() = 0 until size

/**
 * A zero-copy method to represent a [Structure2D] as a two-column x-y data.
 * There could more than two columns in the structure.
 */
@OptIn(PerformancePitfall::class)
@UnstableKMathAPI
public fun <T> Structure2D<T>.asColumnarData(mapping: Map<Symbol, Int>): ColumnarData<T> {
    require(shape[1] >= mapping.maxOf { it.value }) { "Column index out of bounds" }
    return object : ColumnarData<T> {
        override val size: Int get() = shape[0]
        override fun get(symbol: Symbol): Buffer<T> {
            val index = mapping[symbol] ?: error("No column mapping for symbol $symbol")
            return columns[index]
        }
    }
}

