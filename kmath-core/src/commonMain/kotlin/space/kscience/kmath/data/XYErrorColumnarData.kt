/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.data

import space.kscience.kmath.misc.Symbol
import space.kscience.kmath.misc.Symbol.Companion.z
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.misc.symbol
import space.kscience.kmath.structures.Buffer


/**
 * A [ColumnarData] with additional [Companion.yErr] column for an [Symbol.y] error
 * Inherits [XYColumnarData].
 */
@UnstableKMathAPI
public interface XYErrorColumnarData<T, out X : T, out Y : T> : XYColumnarData<T, X, Y> {
    public val yErr: Buffer<Y>

    override fun get(symbol: Symbol): Buffer<T> = when (symbol) {
        Symbol.x -> x
        Symbol.y -> y
        Companion.yErr -> yErr
        else -> error("A column for symbol $symbol not found")
    }

    public companion object{
        public val yErr: Symbol by symbol
    }
}