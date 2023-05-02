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
import kotlin.math.max

/**
 * The buffer of X values.
 */
@UnstableKMathAPI
public interface XYColumnarData<out T, out X : T, out Y : T> : ColumnarData<T> {
    /**
     * The buffer of X values
     */
    public val x: Buffer<X>

    /**
     * The buffer of Y values.
     */
    public val y: Buffer<Y>

    override fun get(symbol: Symbol): Buffer<T>? = when (symbol) {
        Symbol.x -> x
        Symbol.y -> y
        else -> null
    }

    public companion object{
        @UnstableKMathAPI
        public fun <T, X : T, Y : T> of(x: Buffer<X>, y: Buffer<Y>): XYColumnarData<T, X, Y> {
            require(x.size == y.size) { "Buffer size mismatch. x buffer size is ${x.size}, y buffer size is ${y.size}" }
            return object : XYColumnarData<T, X, Y> {
                override val size: Int = x.size
                override val x: Buffer<X> = x
                override val y: Buffer<Y> = y
            }
        }
    }
}


/**
 * Represent a [ColumnarData] as an [XYColumnarData]. The presence or respective columns is checked on creation.
 */
@UnstableKMathAPI
public fun <T> ColumnarData<T>.asXYData(
    xSymbol: Symbol,
    ySymbol: Symbol,
): XYColumnarData<T, T, T> = object : XYColumnarData<T, T, T> {
    init {
        requireNotNull(this@asXYData[xSymbol]){"The column with name $xSymbol is not present in $this"}
        requireNotNull(this@asXYData[ySymbol]){"The column with name $ySymbol is not present in $this"}
    }
    override val size: Int get() = this@asXYData.size
    override val x: Buffer<T> get() = this@asXYData[xSymbol]!!
    override val y: Buffer<T> get() = this@asXYData[ySymbol]!!
    override fun get(symbol: Symbol): Buffer<T>? = when (symbol) {
        Symbol.x -> x
        Symbol.y -> y
        else -> this@asXYData.get(symbol)
    }
}

/**
 * A zero-copy method to represent a [Structure2D] as a two-column x-y data.
 * There could more than two columns in the structure.
 */
@OptIn(PerformancePitfall::class)
@UnstableKMathAPI
public fun <T> Structure2D<T>.asXYData(xIndex: Int = 0, yIndex: Int = 1): XYColumnarData<T, T, T> {
    require(shape[1] >= max(xIndex, yIndex)) { "Column index out of bounds" }
    return object : XYColumnarData<T, T, T> {
        override val size: Int get() = this@asXYData.shape[0]
        override val x: Buffer<T> get() = columns[xIndex]
        override val y: Buffer<T> get() = columns[yIndex]
    }
}
