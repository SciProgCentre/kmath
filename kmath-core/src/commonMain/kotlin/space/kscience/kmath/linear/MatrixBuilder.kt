/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.BufferAccessor2D
import space.kscience.kmath.structures.MutableBuffer

public class MatrixBuilder<T : Any, out A : Ring<T>>(
    public val linearSpace: LinearSpace<T, A>,
    public val rows: Int,
    public val columns: Int,
) {
    public operator fun invoke(vararg elements: T): Matrix<T> {
        require(rows * columns == elements.size) { "The number of elements ${elements.size} is not equal $rows * $columns" }
        return linearSpace.buildMatrix(rows, columns) { i, j -> elements[i * columns + j] }
    }

    //TODO add specific matrix builder functions like diagonal, etc
}

/**
 * Create a matrix builder with given number of rows and columns
 */
@UnstableKMathAPI
public fun <T : Any, A : Ring<T>> LinearSpace<T, A>.matrix(rows: Int, columns: Int): MatrixBuilder<T, A> =
    MatrixBuilder(this, rows, columns)

@UnstableKMathAPI
public fun <T : Any> LinearSpace<T, Ring<T>>.vector(vararg elements: T): Point<T> {
    return buildVector(elements.size) { elements[it] }
}

public inline fun <T : Any> LinearSpace<T, Ring<T>>.row(
    size: Int,
    crossinline builder: (Int) -> T,
): Matrix<T> = buildMatrix(1, size) { _, j -> builder(j) }

public fun <T : Any> LinearSpace<T, Ring<T>>.row(vararg values: T): Matrix<T> = row(values.size, values::get)

public inline fun <T : Any> LinearSpace<T, Ring<T>>.column(
    size: Int,
    crossinline builder: (Int) -> T,
): Matrix<T> = buildMatrix(size, 1) { i, _ -> builder(i) }

public fun <T : Any> LinearSpace<T, Ring<T>>.column(vararg values: T): Matrix<T> = column(values.size, values::get)

public object SymmetricMatrixFeature : MatrixFeature

/**
 * Naive implementation of a symmetric matrix builder, that adds a [SymmetricMatrixFeature] tag. The resulting matrix contains
 * full `size^2` number of elements, but caches elements during calls to save [builder] calls. [builder] is always called in the
 * upper triangle region meaning that `i <= j`
 */
public fun <T : Any, A : Ring<T>> MatrixBuilder<T, A>.symmetric(
    builder: (i: Int, j: Int) -> T,
): Matrix<T> {
    require(columns == rows) { "In order to build symmetric matrix, number of rows $rows should be equal to number of columns $columns" }
    return with(BufferAccessor2D<T?>(rows, rows, MutableBuffer.Companion::boxing)) {
        val cache = factory(rows * rows) { null }
        linearSpace.buildMatrix(rows, rows) { i, j ->
            val cached = cache[i, j]
            if (cached == null) {
                val value = if (i <= j) builder(i, j) else builder(j, i)
                cache[i, j] = value
                cache[j, i] = value
                value
            } else {
                cached
            }
        }.withFeature(SymmetricMatrixFeature)
    }
}