/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.data

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.structures.Buffer


/**
 * A [ColumnarData] with additional [Symbol.yError] column for an [Symbol.y] error
 * Inherits [XYColumnarData].
 */
@UnstableKMathAPI
public interface XYErrorColumnarData<T, out X : T, out Y : T> : XYColumnarData<T, X, Y> {
    public val yErr: Buffer<Y>

    override fun get(symbol: Symbol): Buffer<T> = when (symbol) {
        Symbol.x -> x
        Symbol.y -> y
        Symbol.yError -> yErr
        else -> error("A column for symbol $symbol not found")
    }

    public companion object {
        public fun <T, X : T, Y : T> of(
            x: Buffer<X>, y: Buffer<Y>, yErr: Buffer<Y>
        ): XYErrorColumnarData<T, X, Y> {
            require(x.size == y.size) { "Buffer size mismatch. x buffer size is ${x.size}, y buffer size is ${y.size}" }
            require(y.size == yErr.size) { "Buffer size mismatch. y buffer size is ${x.size}, yErr buffer size is ${y.size}" }

            return object : XYErrorColumnarData<T, X, Y> {
                override val size: Int = x.size
                override val x: Buffer<X> = x
                override val y: Buffer<Y> = y
                override val yErr: Buffer<Y> = yErr
            }
        }
    }
}

