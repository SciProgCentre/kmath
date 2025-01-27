/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.attributes.Attributes
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.Ring

/**
 * Mutable sparse matrix that stores values only for non-zero cells ([DOK format](https://en.wikipedia.org/wiki/Sparse_matrix#Dictionary_of_keys_(DOK))).
 *
 * [SparseMatrix] is ineffective, but does not depend on particular [LinearSpace]
 *
 * Using this class is almost always a [PerformancePitfall]. It should be used only for special cases like manual matrix building.
 */
@UnstableKMathAPI
public class SparseMatrix<T>(
    override val rowNum: Int,
    override val colNum: Int,
    private val zero: T,
    cells: Map<Pair<Int, Int>, T> = emptyMap(),
    override val attributes: Attributes = Attributes.EMPTY,
) : MutableMatrix<T> {

    private val cells = cells.toMutableMap()

    override fun get(i: Int, j: Int): T {
        if (i !in 0 until rowNum) throw IndexOutOfBoundsException("Row index $i out of row range 0..$rowNum")
        if (j !in 0 until colNum) throw IndexOutOfBoundsException("Column index $j out of column range 0..$colNum")
        return cells[i to j] ?: zero
    }

    override fun set(i: Int, j: Int, value: T) {
        require(i in 0 until rowNum) { "Row index $i is out of bounds: 0..<$rowNum" }
        require(j in 0 until colNum) { "Row index $j is out of bounds: 0..<$colNum" }
        val coordinates = i to j
        if (cells[coordinates] != null || value != zero) {
            cells[coordinates] = value
        }
    }

    override fun set(index: IntArray, value: T) {
        require(index.size == 2) { "Index array must contain two elements." }
        set(index[0], index[1], value)
    }
}

/**
 * Create and optionally fill DOK [SparseMatrix]. Those matrices must be converted to dense or effective sparse form
 * after creation for effective use.
 */
@UnstableKMathAPI
public fun <T> LinearSpace<T, Ring<T>>.sparse(
    rows: Int,
    columns: Int,
    attributes: Attributes = Attributes.EMPTY,
    builder: SparseMatrix<T>.() -> Unit = {}
): SparseMatrix<T> = SparseMatrix(rows, columns, elementAlgebra.zero, attributes = attributes).apply(builder)