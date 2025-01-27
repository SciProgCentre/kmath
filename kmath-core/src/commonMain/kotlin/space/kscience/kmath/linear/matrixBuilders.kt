/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.Ring


/**
 * Create a vector from elements
 */
public fun <T> LinearSpace<T, Ring<T>>.vector(vararg elements: T): Point<T> =
    buildVector(elements.size) { elements[it] }

/**
 * Create a single row matrix
 */
public inline fun <T, A : Ring<T>> LinearSpace<T, A>.row(
    size: Int,
    crossinline builder: A.(Int) -> T,
): Matrix<T> = buildMatrix(1, size) { _, j -> builder(j) }

/**
 * Create a single row matrix from elements
 */
public fun <T> LinearSpace<T, Ring<T>>.row(vararg elements: T): Matrix<T> = row(elements.size) { elements[it] }

/**
 * Create a single column matrix
 */
public inline fun <T, A : Ring<T>> LinearSpace<T, A>.column(
    size: Int,
    crossinline builder: A.(Int) -> T,
): Matrix<T> = buildMatrix(size, 1) { i, _ -> builder(i) }

/**
 * Create a single column matrix from elements
 */
public fun <T> LinearSpace<T, Ring<T>>.column(vararg elements: T): Matrix<T> = column(elements.size) { elements[it] }

/**
 * Stack vertically several matrices with the same number of columns.
 *
 * Resulting matrix number of rows is the sum of rows in all [matrices]
 */
@PerformancePitfall
@UnstableKMathAPI
public fun <T> LinearSpace<T, Ring<T>>.vstack(vararg matrices: Matrix<T>): Matrix<T> {
    require(matrices.isNotEmpty()) { "No matrices" }
    val colNum = matrices.first().colNum
    require(matrices.all { it.colNum == colNum }) { "All matrices must have the same number of columns: $colNum" }

    val rows = matrices.flatMap { it.rows }

    return buildMatrix(matrices.sumOf { it.rowNum }, colNum) { row, column->
        rows[row][column]
    }
}

/**
 * Stack horizontally several matrices with the same number of rows.
 *
 * Resulting matrix number of co is the sum of rows in all [matrices]
 */
@PerformancePitfall
@UnstableKMathAPI
public fun <T> LinearSpace<T, Ring<T>>.hstack(vararg matrices: Matrix<T>): Matrix<T> {
    require(matrices.isNotEmpty()) { "No matrices" }
    val rowNum = matrices.first().rowNum
    require(matrices.all { it.rowNum == rowNum }) { "All matrices must have the same number of rows: $rowNum" }

    val columns = matrices.flatMap { it.columns }

    return buildMatrix(rowNum, matrices.sumOf { it.colNum }) { row, column->
        columns[column][row]
    }
}


/**
 * Fill the matrix with given elements. The number of elements must be the same as the number of elements in the matrix.
 *
 * This method is used for small matrices and test purposes.
 */
@UnstableKMathAPI
public fun <T> MutableMatrix<T>.fill(vararg elements: T): MutableMatrix<T> {
    require(rowNum * colNum == elements.size) { "The number of elements ${elements.size} is not equal $rowNum * $colNum" }
    for (i in 0 until rowNum) {
        for (j in 0 until colNum) {
            set(i, j, elements[i * rowNum + j])
        }
    }
    return this
}
