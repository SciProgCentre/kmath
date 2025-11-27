/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.attributes.Attributes
import space.kscience.attributes.SafeType
import space.kscience.attributes.WithType
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.BufferAccessor2D
import space.kscience.kmath.structures.MutableBufferFactory

/**
 * A builder for matrix with fixed size
 */
@UnstableKMathAPI
public class MatrixBuilder<T, out A : Ring<T>>(
    public val linearSpace: LinearSpace<T, A>,
    public val rowNum: Int,
    public val colNum: Int,
) : WithType<T> {

    override val type: SafeType<T> get() = linearSpace.type

}

@UnstableKMathAPI
public fun <T, A : Ring<T>> MatrixBuilder<T, A>.sparse(): SparseMatrix<T> =
    SparseMatrix(rowNum, colNum, linearSpace.elementAlgebra.zero)

@UnstableKMathAPI
public fun <T, A : Ring<T>> MatrixBuilder<T, A>.fill(vararg elements: T): Matrix<T> {
    require(rowNum * colNum == elements.size) { "The number of elements ${elements.size} is not equal $rowNum * $colNum" }
    return linearSpace.buildMatrix(rowNum, colNum) { i, j -> elements[i * colNum + j] }
}

/**
 * Create a matrix builder with given number of rows and columns
 */
@UnstableKMathAPI
public fun <T, A : Ring<T>> LinearSpace<T, A>.MatrixBuilder(rows: Int, columns: Int): MatrixBuilder<T, A> =
    MatrixBuilder(this, rows, columns)

/**
 * Naive implementation of a symmetric matrix builder, that adds a [Symmetric] tag.
 * The resulting matrix contains full `size^2` number of elements,
 * but caches elements during calls to save [builder] calls.
 * Always called in the upper triangle region meaning that `i <= j`
 */
@UnstableKMathAPI
public fun <T, A : Ring<T>> MatrixBuilder<T, A>.symmetric(
    builder: A.(i: Int, j: Int) -> T,
): Matrix<T> {
    require(colNum == rowNum) { "In order to build symmetric matrix, number of rows $rowNum should be equal to number of columns $colNum" }
    return with(BufferAccessor2D<T?>(rowNum, rowNum, MutableBufferFactory(type))) {
        val cache = HashMap<IntArray, T>()
        linearSpace.buildMatrix(this@symmetric.rowNum, this@symmetric.rowNum) { i, j ->
            val index = intArrayOf(i, j)
            val cached = cache[index]
            if (cached == null) {
                val value = if (i <= j) builder(i, j) else builder(j, i)
                cache[index] = value
                cache[index] = value
                value
            } else {
                cached
            }
        }.withAttribute(Symmetric, true)
    }
}

/**
 * Create a diagonal matrix with given factory.
 */
@UnstableKMathAPI
public fun <T, A : Ring<T>> MatrixBuilder<T, A>.diagonal(
    builder: A.(Int) -> T
): Matrix<T> = with(linearSpace.elementAlgebra) {
    require(colNum == rowNum) { "In order to build symmetric matrix, number of rows $rowNum should be equal to number of columns $colNum" }
    return VirtualMatrix(rowNum, colNum, attributes = Attributes(IsDiagonal, true)) { i, j ->
        check(i in 0 until rowNum) { "$i out of bounds: 0..<$rowNum" }
        check(j in 0 until colNum) { "$j out of bounds: 0..<$colNum" }
        if (i == j) {
            builder(i)
        } else {
            zero
        }
    }
}

/**
 * Create a diagonal matrix from elements
 */
@UnstableKMathAPI
public fun <T> MatrixBuilder<T, Ring<T>>.diagonal(vararg elements: T): Matrix<T> {
    require(colNum == rowNum) { "In order to build symmetric matrix, number of rows $rowNum should be equal to number of columns $colNum" }
    return return VirtualMatrix(rowNum, colNum, attributes = Attributes(IsDiagonal,true)) { i, j ->
        check(i in 0 until rowNum) { "$i out of bounds: 0..<$rowNum" }
        check(j in 0 until colNum) { "$j out of bounds: 0..<$colNum" }
        if (i == j) {
            elements[i]
        } else {
            linearSpace.elementAlgebra.zero
        }
    }
}

/**
 * Create a lazily evaluated virtual matrix with a given size
 */
@UnstableKMathAPI
public fun <T : Any> MatrixBuilder<T, *>.virtual(
    attributes: Attributes = Attributes.EMPTY,
    generator: (i: Int, j: Int) -> T,
): VirtualMatrix<T> = VirtualMatrix(rowNum, colNum, attributes, generator)

