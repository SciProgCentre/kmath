package scientifik.kmath.linear

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.RealField
import scientifik.kmath.structures.MutableBuffer.Companion.boxing
import scientifik.kmath.structures.MutableNDStructure
import scientifik.kmath.structures.NDStructure
import scientifik.kmath.structures.get
import scientifik.kmath.structures.mutableNdStructure
import kotlin.math.absoluteValue

/**
 * Implementation based on Apache common-maths LU-decomposition
 */
abstract class LUDecomposition<T : Comparable<T>, F : Field<T>>(val matrix: Matrix<T, F>) {

    private val field get() = matrix.context.ring
    /** Entries of LU decomposition.  */
    internal val lu: NDStructure<T>
    /** Pivot permutation associated with LU decomposition.  */
    internal val pivot: IntArray
    /** Parity of the permutation associated with the LU decomposition.  */
    private var even: Boolean = false

    init {
        val pair = calculateLU()
        lu = pair.first
        pivot = pair.second
    }

    /**
     * Returns the matrix L of the decomposition.
     *
     * L is a lower-triangular matrix
     * @return the L matrix (or null if decomposed matrix is singular)
     */
    val l: Matrix<out T, F> by lazy {
        matrix.context.produce(matrix.numRows, matrix.numCols) { i, j ->
            when {
                j < i -> lu[i, j]
                j == i -> matrix.context.ring.one
                else -> matrix.context.ring.zero
            }
        }
    }


    /**
     * Returns the matrix U of the decomposition.
     *
     * U is an upper-triangular matrix
     * @return the U matrix (or null if decomposed matrix is singular)
     */
    val u: Matrix<out T, F> by lazy {
        matrix.context.produce(matrix.numRows, matrix.numCols) { i, j ->
            if (j >= i) lu[i, j] else field.zero
        }
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
    val p: Matrix<out T, F> by lazy {
        matrix.context.produce(matrix.numRows, matrix.numCols) { i, j ->
            //TODO ineffective. Need sparse matrix for that
            if (j == pivot[i]) field.one else field.zero
        }
    }

    /**
     * Return the determinant of the matrix
     * @return determinant of the matrix
     */
    val determinant: T
        get() {
            with(matrix.context.ring) {
                var determinant = if (even) one else -one
                for (i in 0 until matrix.numRows) {
                    determinant *= lu[i, i]
                }
                return determinant
            }
        }

    /**
     * In-place transformation for [MutableNDArray], using given transformation for each element
     */
    operator fun <T> MutableNDStructure<T>.set(i: Int, j: Int, value: T) {
        this[intArrayOf(i, j)] = value
    }

    abstract fun isSingular(value: T): Boolean

    private fun abs(value: T) = if (value > matrix.context.ring.zero) value else with(matrix.context.ring) { -value }

    private fun calculateLU(): Pair<NDStructure<T>, IntArray> {
        if (matrix.numRows != matrix.numCols) {
            error("LU decomposition supports only square matrices")
        }

        val m = matrix.numCols
        val pivot = IntArray(matrix.numRows)
        //TODO fix performance
        val lu: MutableNDStructure<T> = mutableNdStructure(
            intArrayOf(matrix.numRows, matrix.numCols),
            ::boxing
        ) { index: IntArray -> matrix[index[0], index[1]] }


        with(matrix.context.ring) {
            // Initialize permutation array and parity
            for (row in 0 until m) {
                pivot[row] = row
            }
            even = true

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
                    //luRow[col] = sum
                    lu[row, col] = sum

                    abs(sum)
                } ?: col

                // Singularity check
                if (isSingular(lu[max, col])) {
                    error("Singular matrix")
                }

                // Pivot if necessary
                if (max != col) {
                    //var tmp = zero
                    //val luMax = lu[max]
                    //val luCol = lu[col]
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
//                    lu[row, col] /= luDiag
                }
            }
        }
        return Pair(lu, pivot)
    }

    /**
     * Returns the pivot permutation vector.
     * @return the pivot permutation vector
     * @see .getP
     */
    fun getPivot(): IntArray {
        return pivot.copyOf()
    }

}

class RealLUDecomposition(matrix: RealMatrix, private val singularityThreshold: Double = DEFAULT_TOO_SMALL) :
    LUDecomposition<Double, RealField>(matrix) {
    override fun isSingular(value: Double): Boolean {
        return value.absoluteValue < singularityThreshold
    }

    companion object {
        /** Default bound to determine effective singularity in LU decomposition.  */
        private const val DEFAULT_TOO_SMALL = 1e-11
    }
}


/** Specialized solver.  */
object RealLUSolver : LinearSolver<Double, RealField> {

    fun decompose(mat: Matrix<Double, RealField>, threshold: Double = 1e-11): RealLUDecomposition =
        RealLUDecomposition(mat, threshold)

    override fun solve(a: RealMatrix, b: RealMatrix): RealMatrix {
        val decomposition = decompose(a, a.context.ring.zero)

        if (b.numRows != a.numCols) {
            error("Matrix dimension mismatch expected ${a.numRows}, but got ${b.numCols}")
        }

        // Apply permutations to b
        val bp = Array(a.numRows) { DoubleArray(b.numCols) }
        for (row in 0 until a.numRows) {
            val bpRow = bp[row]
            val pRow = decomposition.pivot[row]
            for (col in 0 until b.numCols) {
                bpRow[col] = b[pRow, col]
            }
        }

        // Solve LY = b
        for (col in 0 until a.numRows) {
            val bpCol = bp[col]
            for (i in col + 1 until a.numRows) {
                val bpI = bp[i]
                val luICol = decomposition.lu[i, col]
                for (j in 0 until b.numCols) {
                    bpI[j] -= bpCol[j] * luICol
                }
            }
        }

        // Solve UX = Y
        for (col in a.numRows - 1 downTo 0) {
            val bpCol = bp[col]
            val luDiag = decomposition.lu[col, col]
            for (j in 0 until b.numCols) {
                bpCol[j] /= luDiag
            }
            for (i in 0 until col) {
                val bpI = bp[i]
                val luICol = decomposition.lu[i, col]
                for (j in 0 until b.numCols) {
                    bpI[j] -= bpCol[j] * luICol
                }
            }
        }

        return a.context.produce(a.numRows, a.numCols) { i, j -> bp[i][j] }
    }
}
