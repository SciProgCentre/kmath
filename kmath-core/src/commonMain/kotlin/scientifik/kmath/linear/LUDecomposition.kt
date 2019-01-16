package scientifik.kmath.linear

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.Ring
import scientifik.kmath.structures.*
import scientifik.kmath.structures.MutableBuffer.Companion.boxing

/**
 * Matrix LUP decomposition
 */
interface LUPDecompositionFeature<T : Any> : DeterminantFeature<T> {
    /**
     * A reference to L-matrix
     */
    val l: Matrix<T>
    /**
     * A reference to u-matrix
     */
    val u: Matrix<T>
    /**
     * Pivoting points for each row
     */
    val pivot: IntArray
    /**
     * Permutation matrix based on [pivot]
     */
    val p: Matrix<T>
}


private class LUPDecomposition<T : Comparable<T>, R : Ring<T>>(
    val context: R,
    val lu: NDStructure<T>,
    override val pivot: IntArray,
    private val even: Boolean
) : LUPDecompositionFeature<T> {

    /**
     * Returns the matrix L of the decomposition.
     *
     * L is a lower-triangular matrix
     * @return the L matrix (or null if decomposed matrix is singular)
     */
    override val l: Matrix<T> = VirtualMatrix(lu.shape[0], lu.shape[1]) { i, j ->
        when {
            j < i -> lu[i, j]
            j == i -> context.one
            else -> context.zero
        }
    }


    /**
     * Returns the matrix U of the decomposition.
     *
     * U is an upper-triangular matrix
     * @return the U matrix (or null if decomposed matrix is singular)
     */
    override val u: Matrix<T> = VirtualMatrix(lu.shape[0], lu.shape[1]) { i, j ->
        if (j >= i) lu[i, j] else context.zero
    }


    /**
     * Returns the P rows permutation matrix.
     *
     * P is a sparse matrix with exactly one element set to 1.0 in
     * each row and each column, all other elements being set to 0.0.
     *
     * The positions of the 1 elements are given by the [ pivot permutation vector][.getPivot].
     * @return the P rows permutation matrix (or null if decomposed matrix is singular)
     * @see .getPivot
     */
    override val p: Matrix<T> = VirtualMatrix(lu.shape[0], lu.shape[1]) { i, j ->
        if (j == pivot[i]) context.one else context.zero
    }


    /**
     * Return the determinant of the matrix
     * @return determinant of the matrix
     */
    override val determinant: T by lazy {
        with(context) {
            (0 until lu.shape[0]).fold(if (even) one else -one) { value, i -> value * lu[i, i] }
        }
    }

}


/**
 * Implementation based on Apache common-maths LU-decomposition
 */
class LUPDecompositionBuilder<T : Comparable<T>, F : Field<T>>(
    val context: F,
    val bufferFactory: MutableBufferFactory<T> = ::boxing,
    val singularityCheck: (T) -> Boolean
) {

    /**
     * In-place transformation for [MutableNDStructure], using given transformation for each element
     */
    private operator fun <T> MutableNDStructure<T>.set(i: Int, j: Int, value: T) {
        this[intArrayOf(i, j)] = value
    }

    private fun abs(value: T) = if (value > context.zero) value else with(context) { -value }

    fun decompose(matrix: Matrix<T>): LUPDecompositionFeature<T> {
        if (matrix.rowNum != matrix.colNum) {
            error("LU decomposition supports only square matrices")
        }

        val m = matrix.colNum
        val pivot = IntArray(matrix.rowNum)

        val lu = Mutable2DStructure.create(matrix.rowNum, matrix.colNum, bufferFactory) { i, j ->
            matrix[i, j]
        }


        with(context) {
            // Initialize permutation array and parity
            for (row in 0 until m) {
                pivot[row] = row
            }
            var even = true

            // Loop over columns
            for (col in 0 until m) {

                // upper
                for (row in 0 until col) {
                    var sum = lu[row, col]
                    for (i in 0 until row) {
                        sum -= lu[row, i] * lu[i, col]
                    }
                    lu[row, col] = sum
                }

                // lower
                val max = (col until m).maxBy { row ->
                    var sum = lu[row, col]
                    for (i in 0 until col) {
                        sum -= lu[row, i] * lu[i, col]
                    }
                    lu[row, col] = sum

                    abs(sum)
                } ?: col

                // Singularity check
                if (singularityCheck(lu[max, col])) {
                    error("Singular matrix")
                }

                // Pivot if necessary
                if (max != col) {
                    for (i in 0 until m) {
                        lu[max, i] = lu[col, i]
                        lu[col, i] = lu[max, i]
                    }
                    val temp = pivot[max]
                    pivot[max] = pivot[col]
                    pivot[col] = temp
                    even = !even
                }

                // Divide the lower elements by the "winning" diagonal elt.
                val luDiag = lu[col, col]
                for (row in col + 1 until m) {
                    lu[row, col] = lu[row, col] / luDiag
                }
            }
            return LUPDecomposition(context, lu, pivot, even)
        }
    }

    companion object {
        val real: LUPDecompositionBuilder<Double, RealField> = LUPDecompositionBuilder(RealField) { it < 1e-11 }
    }

}


class LUSolver<T : Comparable<T>, F : Field<T>>(
    override val context: MatrixContext<T, F>,
    val bufferFactory: MutableBufferFactory<T> = ::boxing,
    val singularityCheck: (T) -> Boolean
) : LinearSolver<T, F> {


    override fun solve(a: Matrix<T>, b: Matrix<T>): Matrix<T> {
        if (b.rowNum != a.colNum) {
            error("Matrix dimension mismatch expected ${a.rowNum}, but got ${b.colNum}")
        }

        // Use existing decomposition if it is provided by matrix
        @Suppress("UNCHECKED_CAST")
        val decomposition = a.features.find { it is LUPDecompositionFeature<*> }?.let {
            it as LUPDecompositionFeature<T>
        } ?: LUPDecompositionBuilder(context.elementContext, bufferFactory, singularityCheck).decompose(a)

        with(decomposition) {
            with(context.elementContext) {
                // Apply permutations to b
                val bp = Mutable2DStructure.create(a.rowNum, a.colNum, bufferFactory) { i, j ->
                    b[pivot[i], j]
                }

                // Solve LY = b
                for (col in 0 until a.rowNum) {
                    for (i in col + 1 until a.rowNum) {
                        for (j in 0 until b.colNum) {
                            bp[i, j] -= bp[col, j] * l[i, col]
                        }
                    }
                }


                // Solve UX = Y
                for (col in a.rowNum - 1 downTo 0) {
                    for (i in 0 until col) {
                        for (j in 0 until b.colNum) {
                            bp[i, j] -= bp[col, j] / u[col, col] * u[i, col]
                        }
                    }
                }

                return context.produce(a.rowNum, a.colNum) { i, j -> bp[i, j] }
            }
        }
    }

    companion object {
        val real = LUSolver(MatrixContext.real, MutableBuffer.Companion::auto) { it < 1e-11 }
    }
}
