/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("UnusedReceiverParameter")

package space.kscience.kmath.linear

import space.kscience.attributes.PolymorphicAttribute
import space.kscience.attributes.SafeType
import space.kscience.attributes.safeTypeOf
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.*

/**
 * Matrices with this feature support LU factorization with partial pivoting: *[p] &middot; a = [l] &middot; [u]* where
 * *a* is the owning matrix.
 *
 * @param T the type of matrices' items.
 * @param l The lower triangular matrix in this decomposition. It may have [LowerTriangular].
 * @param u The upper triangular matrix in this decomposition. It may have [UpperTriangular].
 */
public data class LupDecomposition<T>(
    public val l: Matrix<T>,
    public val u: Matrix<T>,
    public val pivot: IntBuffer,
)


public class LupDecompositionAttribute<T>(type: SafeType<LupDecomposition<T>>) :
    PolymorphicAttribute<LupDecomposition<T>>(type),
    MatrixAttribute<LupDecomposition<T>>

public val <T> MatrixOperations<T>.LUP: LupDecompositionAttribute<T>
    get() = LupDecompositionAttribute(safeTypeOf())


///**
// * Common implementation of [LupDecomposition].
// */
//private class LupDecompositionImpl<T : Any>(
//    public val elementContext: Field<T>,
//    public val lu: Matrix<T>,
//    public val pivot: IntBuffer,
//    private val even: Boolean,
//) : LupDecomposition<T> {
//    /**
//     * Returns the matrix L of the decomposition.
//     *
//     * L is a lower-triangular matrix with [Ring.one] in diagonal
//     */
//    override val l: Matrix<T> = VirtualMatrix(lu.shape[0], lu.shape[1]) { i, j ->
//        when {
//            j < i -> lu[i, j]
//            j == i -> elementContext.one
//            else -> elementContext.zero
//        }
//    }.withFeature(LowerTriangular)
//
//
//    /**
//     * Returns the matrix U of the decomposition.
//     *
//     * U is an upper-triangular matrix including the diagonal
//     */
//    override val u: Matrix<T> = VirtualMatrix(lu.shape[0], lu.shape[1]) { i, j ->
//        if (j >= i) lu[i, j] else elementContext.zero
//    }.withFeature(UpperTriangular)
//
//    /**
//     * Returns the P rows permutation matrix.
//     *
//     * P is a sparse matrix with exactly one element set to [Ring.one] in
//     * each row and each column, all other elements being set to [Ring.zero].
//     */
//    override val p: Matrix<T> = VirtualMatrix(lu.shape[0], lu.shape[1]) { i, j ->
//        if (j == pivot[i]) elementContext.one else elementContext.zero
//    }
//
//    /**
//     * Return the determinant of the matrix
//     * @return determinant of the matrix
//     */
//    override val determinant: T by lazy {
//        elementContext { (0 until lu.shape[0]).fold(if(even) one else -one) { value, i -> value * lu[i, i] } }
//    }
//
//}

@PublishedApi
internal fun <T : Comparable<T>> LinearSpace<T, Ring<T>>.abs(value: T): T =
    if (value > elementAlgebra.zero) value else elementAlgebra { -value }

/**
 * Create a lup decomposition of generic matrix.
 */
public fun <T : Comparable<T>> LinearSpace<T, Field<T>>.lup(
    matrix: Matrix<T>,
    checkSingular: (T) -> Boolean,
): LupDecomposition<T> {
    require(matrix.rowNum == matrix.colNum) { "LU decomposition supports only square matrices" }
    val m = matrix.colNum
    val pivot = IntArray(matrix.rowNum)

    //TODO just waits for multi-receivers
    BufferAccessor2D(matrix.rowNum, matrix.colNum, elementAlgebra.bufferFactory).run {
        elementAlgebra {
            val lu = create(matrix)

            // Initialize the permutation array and parity
            for (row in 0 until m) pivot[row] = row
            var even = true

            // Initialize the permutation array and parity
            for (row in 0 until m) pivot[row] = row

            // Loop over columns
            for (col in 0 until m) {
                // upper
                for (row in 0 until col) {
                    val luRow = lu.row(row)
                    var sum = luRow[col]
                    for (i in 0 until row) sum -= luRow[i] * lu[i, col]
                    luRow[col] = sum
                }

                // lower
                var max = col // permutation row
                var largest = -one

                for (row in col until m) {
                    val luRow = lu.row(row)
                    var sum = luRow[col]
                    for (i in 0 until col) sum -= luRow[i] * lu[i, col]
                    luRow[col] = sum

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
                    val luMax = lu.row(max)
                    val luCol = lu.row(col)

                    for (i in 0 until m) {
                        val tmp = luMax[i]
                        luMax[i] = luCol[i]
                        luCol[i] = tmp
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

            val l: MatrixWrapper<T> = VirtualMatrix(rowNum, colNum) { i, j ->
                when {
                    j < i -> lu[i, j]
                    j == i -> one
                    else -> zero
                }
            }.withAttribute(LowerTriangular)

            val u = VirtualMatrix(rowNum, colNum) { i, j ->
                if (j >= i) lu[i, j] else zero
            }.withAttribute(UpperTriangular)
//
//            val p = VirtualMatrix(rowNum, colNum) { i, j ->
//                if (j == pivot[i]) one else zero
//            }.withAttribute(Determinant, if (even) one else -one)

            return LupDecomposition(l, u, pivot.asBuffer())
        }
    }
}


public fun LinearSpace<Double, DoubleField>.lup(
    matrix: Matrix<Double>,
    singularityThreshold: Double = 1e-11,
): LupDecomposition<Double> = lup(matrix) { it < singularityThreshold }

internal fun <T : Any, A : Field<T>> LinearSpace<T, A>.solve(
    lup: LupDecomposition<T>,
    matrix: Matrix<T>,
): Matrix<T> {
    require(matrix.rowNum == lup.l.rowNum) { "Matrix dimension mismatch. Expected ${lup.l.rowNum}, but got ${matrix.colNum}" }

    BufferAccessor2D(matrix.rowNum, matrix.colNum, elementAlgebra.bufferFactory).run {
        elementAlgebra {
            // Apply permutations to b
            val bp = create { _, _ -> zero }

            for (row in 0 until rowNum) {
                val bpRow = bp.row(row)
                val pRow = lup.pivot[row]
                for (col in 0 until matrix.colNum) bpRow[col] = matrix[pRow, col]
            }

            // Solve LY = b
            for (col in 0 until colNum) {
                val bpCol = bp.row(col)

                for (i in col + 1 until colNum) {
                    val bpI = bp.row(i)
                    val luICol = lup.l[i, col]
                    for (j in 0 until matrix.colNum) {
                        bpI[j] -= bpCol[j] * luICol
                    }
                }
            }

            // Solve UX = Y
            for (col in colNum - 1 downTo 0) {
                val bpCol = bp.row(col)
                val luDiag = lup.u[col, col]
                for (j in 0 until matrix.colNum) bpCol[j] /= luDiag

                for (i in 0 until col) {
                    val bpI = bp.row(i)
                    val luICol = lup.u[i, col]
                    for (j in 0 until matrix.colNum) bpI[j] -= bpCol[j] * luICol
                }
            }

            return buildMatrix(matrix.rowNum, matrix.colNum) { i, j -> bp[i, j] }
        }
    }
}

/**
 * Produce a generic solver based on LUP decomposition
 */
@OptIn(UnstableKMathAPI::class)
public fun <T : Comparable<T>, F : Field<T>> LinearSpace<T, F>.lupSolver(
    singularityCheck: (T) -> Boolean,
): LinearSolver<T> = object : LinearSolver<T> {
    override fun solve(a: Matrix<T>, b: Matrix<T>): Matrix<T> {
        // Use existing decomposition if it is provided by matrix
        val decomposition = attributeFor(a, LUP) ?: lup(a, singularityCheck)
        return solve(decomposition, b)
    }

    override fun inverse(matrix: Matrix<T>): Matrix<T> = solve(matrix, one(matrix.rowNum, matrix.colNum))
}

public fun LinearSpace<Double, DoubleField>.lupSolver(singularityThreshold: Double = 1e-11): LinearSolver<Double> =
    lupSolver { it < singularityThreshold }
