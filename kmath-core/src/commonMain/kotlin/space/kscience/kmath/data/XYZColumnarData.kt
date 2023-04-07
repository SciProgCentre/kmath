/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.data

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.structures.Buffer

/**
 * A [ColumnarData] with guaranteed [x], [y] and [z] columns designated by corresponding symbols.
 * Inherits [XYColumnarData].
 */
@UnstableKMathAPI
public interface XYZColumnarData<out T, out X : T, out Y : T, out Z : T> : XYColumnarData<T, X, Y> {
    public val z: Buffer<Z>

    override fun get(symbol: Symbol): Buffer<T>? = when (symbol) {
        Symbol.x -> x
        Symbol.y -> y
        Symbol.z -> z
        else -> null
    }
}
