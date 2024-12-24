/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(UnstableKMathAPI::class)

package space.kscience.kmath.named

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MutableBuffer

public class NamedBuffer<T>(public val values: Buffer<T>, public val indexer: SymbolIndexer): Buffer<T> by values{
    public operator fun get(symbol: Symbol): T = values[indexer.indexOf(symbol)]
}

public class NamedMutableBuffer<T>(public val values: MutableBuffer<T>, public val indexer: SymbolIndexer): MutableBuffer<T> by values{
    public operator fun get(symbol: Symbol): T = values[indexer.indexOf(symbol)]
    public operator fun set(symbol: Symbol, value: T) { values[indexer.indexOf(symbol)] = value }
}