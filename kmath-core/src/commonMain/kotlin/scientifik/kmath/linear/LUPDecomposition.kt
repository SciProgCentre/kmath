package scientifik.kmath.linear

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Ring
import scientifik.kmath.structures.MutableBuffer
import scientifik.kmath.structures.MutableBuffer.Companion.boxing
import scientifik.kmath.structures.MutableBufferFactory
import scientifik.kmath.structures.NDStructure
import scientifik.kmath.structures.get

class LUPDecomposition<T : Comparable<T>>(
    private val elementContext: Ring<T>,
    internal val lu: NDStructure<T>,
    val pivot: IntArray,
    private val even: Boolean
) : LUPDecompositionFeature<T>, DeterminantFeature<T> {

    /**
     * Returns the matrix L of the decomposition.
     *
     * L is a lower-triangular matrix with [Ring.one] in diagonal
     */
    override val l: Matrix<T> = VirtualMatrix(lu.shape[0], lu.shape[1], setOf(LFeature)) { i, j ->
        when {
            j < i -> lu[i, j]
            j == i -> elementContext.one
            else -> elementContext.zero
        }
    }


    /**
     * Returns the matrix U of the decomposition.
     *
     * U is an upper-triangular matrix including the diagonal
     */
    override val u: Matrix<T> = VirtualMatrix(lu.shape[0], lu.shape[1], setOf(UFeature)) { i, j ->
        if (j >= i) lu[i, j] else elementContext.zero
    }


    /**
     * Returns the P rows permutation matrix.
     *
     * P is a sparse matrix with exactly one element set to [Ring.one] in
     * each row and each column, all other elements being set to [Ring.zero].
     */
    override val p: Matrix<T> = VirtualMatrix(lu.shape[0], lu.shape[1]) { i, j ->
        if (j == pivot[i]) elementContext.one else elementContext.zero
    }


    /**
     * Return the determinant of the matrix
     * @return determinant of the matrix
     */
    override val determinant: T by lazy {
        with(elementContext) {
            (0 until lu.shape[0]).fold(if (even) one else -one) { value, i -> value * lu[i, i] }
        }
    }

}


class LUSolver<T : Comparable<T>, F : Field<T>>(
    val context: GenericMatrixContext<T, F>,
    val bufferFactory: MutableBufferFactory<T> = ::boxing,
    val singularityCheck: (T) -> Boolean
) : LinearSolver<T> {


    private fun abs(value: T) =
        if (value > context.elementContext.zero) value else with(context.elementContext) { -value }

    fun buildDecomposition(matrix: Matrix<T>): LUPDecomposition<T> {
        if (matrix.rowNum != matrix.colNum) {
            error("LU decomposition supports only square matrices")
        }

        val m = matrix.colNum
        val pivot = IntArray(matrix.rowNum)

        val lu = Mutable2DStructure.create(matrix.rowNum, matrix.colNum, bufferFactory) { i, j ->
            matrix[i, j]
        }


        with(context.elementContext) {
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
            return LUPDecomposition(context.elementContext, lu, pivot, even)
        }
    }

    /**
     * Produce a matrix with added decomposition feature
     */
    fun decompose(matrix: Matrix<T>): Matrix<T> {
        if (matrix.hasFeature<LUPDecomposition<*>>()) {
            return matrix
        } else {
            val decomposition = buildDecomposition(matrix)
            return VirtualMatrix.wrap(matrix, decomposition)
        }
    }


    override fun solve(a: Matrix<T>, b: Matrix<T>): Matrix<T> {
        if (b.rowNum != a.colNum) {
            error("Matrix dimension mismatch expected ${a.rowNum}, but got ${b.colNum}")
        }

        // Use existing decomposition if it is provided by matrix
        val decomposition = a.getFeature() ?: buildDecomposition(a)

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
                            bp[i, j] -= bp[col, j] * lu[i, col]
                        }
                    }
                }

                // Solve UX = Y
                for (col in a.rowNum - 1 downTo 0) {
                    for (j in 0 until b.colNum) {
                        bp[col, j] /= lu[col, col]
                    }
                    for (i in 0 until col) {
                        for (j in 0 until b.colNum) {
                            bp[i, j] -= bp[col, j] * lu[i, col]
                        }
                    }
                }

                return context.produce(a.rowNum, a.colNum) { i, j -> bp[i, j] }
            }
        }
    }

    override fun inverse(a: Matrix<T>): Matrix<T> = solve(a, context.one(a.rowNum, a.colNum))

    companion object {
        val real = LUSolver(MatrixContext.real, MutableBuffer.Companion::auto) { it < 1e-11 }
    }
}