/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("UnusedReceiverParameter")

package space.kscience.kmath.linear

import space.kscience.attributes.Attributes
import space.kscience.attributes.PolymorphicAttribute
import space.kscience.attributes.safeTypeOf
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.*

public interface LupDecomposition<T> {
    public val pivot: IntBuffer
    public val l: Matrix<T>
    public val u: Matrix<T>
}

/**
 * Create a pivot matrix from pivot vector using provided [LinearSpace]
 */
public fun <T> LupDecomposition<T>.pivotMatrix(linearSpace: LinearSpace<T, Ring<T>>): Matrix<T> =
    VirtualMatrix(linearSpace.type, l.rowNum, l.colNum) { row, column ->
        if (column == pivot[row]) linearSpace.elementAlgebra.one else linearSpace.elementAlgebra.zero
    }

/**
 * Matrices with this feature support LU factorization with partial pivoting: *[p] &middot; a = [l] &middot; [u]* where
 * *a* is the owning matrix.
 *
 * @param T the type of matrices' items.
 * @param lu combined L and U matrix
 */
public class GenericLupDecomposition<T>(
    public val elementAlgebra: Field<T>,
    private val lu: Matrix<T>,
    override val pivot: IntBuffer,
    private val even: Boolean,
) : LupDecomposition<T> {


    override val l: Matrix<T>
        get() = VirtualMatrix(lu.type, lu.rowNum, lu.colNum, attributes = Attributes(LowerTriangular)) { i, j ->
            when {
                j < i -> lu[i, j]
                j == i -> elementAlgebra.one
                else -> elementAlgebra.zero
            }
        }

    override val u: Matrix<T>
        get() = VirtualMatrix(lu.type, lu.rowNum, lu.colNum, attributes = Attributes(UpperTriangular)) { i, j ->
            if (j >= i) lu[i, j] else elementAlgebra.zero
        }

    public val determinant: T by lazy {
        elementAlgebra { (0 until l.shape[0]).fold(if (even) one else -one) { value, i -> value * lu[i, i] } }
    }

}

public class LupDecompositionAttribute<T> :
    PolymorphicAttribute<LupDecomposition<T>>(safeTypeOf()),
    MatrixAttribute<LupDecomposition<T>>

public val <T> MatrixScope<T>.LUP: LupDecompositionAttribute<T>
    get() = LupDecompositionAttribute()

@PublishedApi
internal fun <T : Comparable<T>> LinearSpace<T, Ring<T>>.abs(value: T): T =
    if (value > elementAlgebra.zero) value else elementAlgebra { -value }

/**
 * Create a lup decomposition of generic matrix.
 */
public fun <T : Comparable<T>> LinearSpace<T, Field<T>>.lup(
    matrix: Matrix<T>,
    checkSingular: (T) -> Boolean,
): GenericLupDecomposition<T> = elementAlgebra {
    require(matrix.rowNum == matrix.colNum) { "LU decomposition supports only square matrices" }
    val m = matrix.colNum
    val pivot = IntArray(matrix.rowNum)

    val strides = RowStrides(ShapeND(matrix.rowNum, matrix.colNum))

    val lu: MutableStructure2D<T> = MutableBufferND(
        strides,
        bufferAlgebra.buffer(strides.linearSize) { offset ->
            matrix[strides.index(offset)]
        }
    ).as2D()


    // Initialize the permutation array and parity
    for (row in 0 until m) pivot[row] = row
    var even = true

    // Initialize the permutation array and parity
    for (row in 0 until m) pivot[row] = row

    // Loop over columns
    for (col in 0 until m) {
        // upper
        for (row in 0 until col) {
            var sum = lu[row, col]
            for (i in 0 until row) sum -= lu[row, i] * lu[i, col]
            lu[row, col] = sum
        }

        // lower
        var max = col // permutation row
        var largest = -one

        for (row in col until m) {
            var sum = lu[row, col]
            for (i in 0 until col) sum -= lu[row, i] * lu[i, col]
            lu[row, col] = sum

            // maintain the best permutation choice
            if (abs(sum) > largest) {
                largest = abs(sum)
                max = row
            }
        }

        // Singularity check
        check(!checkSingular(abs(lu[max, col]))) { "The matrix is singular" }

        // Pivot if necessary
        if (max != col) {
            for (i in 0 until m) {
                val tmp = lu[max, i]
                lu[max, i] = lu[col, i]
                lu[col, i] = tmp
            }

            val temp = pivot[max]
            pivot[max] = pivot[col]
            pivot[col] = temp
            even = !even
        }

        // Divide the lower elements by the "winning" diagonal elt.
        val luDiag = lu[col, col]
        for (row in col + 1 until m) lu[row, col] /= luDiag
    }


    return GenericLupDecomposition(elementAlgebra, lu, pivot.asBuffer(), even)

}


public fun LinearSpace<Double, Float64Field>.lup(
    matrix: Matrix<Double>,
    singularityThreshold: Double = 1e-11,
): GenericLupDecomposition<Double> = lup(matrix) { it < singularityThreshold }

internal fun <T> LinearSpace<T, Field<T>>.solve(
    lup: LupDecomposition<T>,
    matrix: Matrix<T>,
): Matrix<T> = elementAlgebra {
    require(matrix.rowNum == lup.l.rowNum) { "Matrix dimension mismatch. Expected ${lup.l.rowNum}, but got ${matrix.colNum}" }

//    with(BufferAccessor2D(matrix.rowNum, matrix.colNum, elementAlgebra.bufferFactory)) {

    val strides = RowStrides(ShapeND(matrix.rowNum, matrix.colNum))

    // Apply permutations to b
    val bp: MutableStructure2D<T> = MutableBufferND(
        strides,
        bufferAlgebra.buffer(strides.linearSize) { offset -> zero }
    ).as2D()


    for (row in 0 until matrix.rowNum) {
        val pRow = lup.pivot[row]
        for (col in 0 until matrix.colNum) {
            bp[row, col] = matrix[pRow, col]
        }
    }

    // Solve LY = b
    for (col in 0 until matrix.colNum) {

        for (i in col + 1 until matrix.colNum) {
            val luICol = lup.l[i, col]
            for (j in 0 until matrix.colNum) {
                bp[i, j] -= bp[col, j] * luICol
            }
        }
    }

    // Solve UX = Y
    for (col in matrix.colNum - 1 downTo 0) {
        val luDiag = lup.u[col, col]
        for (j in 0 until matrix.colNum) {
            bp[col, j] /= luDiag
        }

        for (i in 0 until col) {
            val luICol = lup.u[i, col]
            for (j in 0 until matrix.colNum) {
                bp[i, j] -= bp[col, j] * luICol
            }
        }
    }

    return buildMatrix(matrix.rowNum, matrix.colNum) { i, j -> bp[i, j] }

}


/**
 * Produce a generic solver based on LUP decomposition
 */
@OptIn(UnstableKMathAPI::class)
public fun <T : Comparable<T>> LinearSpace<T, Field<T>>.lupSolver(
    singularityCheck: (T) -> Boolean,
): LinearSolver<T> = object : LinearSolver<T> {
    override fun solve(a: Matrix<T>, b: Matrix<T>): Matrix<T> {
        // Use existing decomposition if it is provided by matrix or linear space itself
        val decomposition = a.getOrComputeAttribute(LUP) ?: lup(a, singularityCheck)
        return solve(decomposition, b)
    }

    override fun inverse(matrix: Matrix<T>): Matrix<T> = solve(matrix, one(matrix.rowNum, matrix.colNum))
}

public fun LinearSpace<Double, Float64Field>.lupSolver(singularityThreshold: Double = 1e-11): LinearSolver<Double> =
    lupSolver { it < singularityThreshold }
