/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(UnstableKMathAPI::class)

package space.kscience.kmath.expressions

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.structures.getOrNull

public class NamedMatrix<T>(public val values: Matrix<T>, public val indexer: SymbolIndexer) : Matrix<T> by values {
    public operator fun get(i: Symbol, j: Symbol): T = get(indexer.indexOf(i), indexer.indexOf(j))

    public companion object {

        @OptIn(PerformancePitfall::class)
        public fun toStringWithSymbols(values: Matrix<*>, indexer: SymbolIndexer): String = buildString {
            appendLine(indexer.symbols.joinToString(separator = "\t", prefix = "\t\t"))
            indexer.symbols.forEach { i ->
                append(i.identity + "\t")
                values.rows.getOrNull(indexer.indexOf(i))?.let { row ->
                    indexer.symbols.forEach { j ->
                        append(row.getOrNull(indexer.indexOf(j)).toString())
                        append("\t")
                    }
                    appendLine()
                }
            }
        }
    }
}

public fun <T> Matrix<T>.named(indexer: SymbolIndexer): NamedMatrix<T> = NamedMatrix(this, indexer)

public fun <T> Matrix<T>.named(symbols: List<Symbol>): NamedMatrix<T> = named(SimpleSymbolIndexer(symbols))