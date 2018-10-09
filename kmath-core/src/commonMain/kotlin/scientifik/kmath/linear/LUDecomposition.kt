package scientifik.kmath.linear

import scientifik.kmath.operations.Field
import scientifik.kmath.structures.MutableNDArray
import scientifik.kmath.structures.NDArray
import scientifik.kmath.structures.NDArrays

/**
 * Calculates the LUP-decomposition of a square matrix.
 *
 * The LUP-decomposition of a matrix A consists of three matrices L, U and
 * P that satisfy: PA = LU. L is lower triangular (with unit
 * diagonal terms), U is upper triangular and P is a permutation matrix. All
 * matrices are mm.
 *
 * As shown by the presence of the P matrix, this decomposition is
 * implemented using partial pivoting.
 *
 * This class is based on the class with similar name from the
 * [JAMA](http://math.nist.gov/javanumerics/jama/) library.
 *
 *  * a [getP][.getP] method has been added,
 *  * the `det` method has been renamed as [   getDeterminant][.getDeterminant],
 *  * the `getDoublePivot` method has been removed (but the int based
 * [getPivot][.getPivot] method has been kept),
 *  * the `solve` and `isNonSingular` methods have been replaced
 * by a [getSolver][.getSolver] method and the equivalent methods
 * provided by the returned [DecompositionSolver].
 *
 *
 * @see [MathWorld](http://mathworld.wolfram.com/LUDecomposition.html)
 *
 * @see [Wikipedia](http://en.wikipedia.org/wiki/LU_decomposition)
 *
 * @since 2.0 (changed to concrete class in 3.0)
 *
 * @param matrix The matrix to decompose.
 * @param singularityThreshold threshold (based on partial row norm)
 * under which a matrix is considered singular
 * @throws NonSquareMatrixException if matrix is not square

 */
abstract class LUDecomposition<T : Comparable<T>>(val matrix: Matrix<T>) {

    private val field get() = matrix.context.field
    /** Entries of LU decomposition.  */
    internal val lu: NDArray<T>
    /** Pivot permutation associated with LU decomposition.  */
    internal val pivot: IntArray
    /** Parity of the permutation associated with the LU decomposition.  */
    private var even: Boolean = false

    init {
        val pair = matrix.context.field.calculateLU()
        lu = pair.first
        pivot = pair.second
    }

    /**
     * Returns the matrix L of the decomposition.
     *
     * L is a lower-triangular matrix
     * @return the L matrix (or null if decomposed matrix is singular)
     */
    val l: Matrix<out T> by lazy {
        matrix.context.produce { i, j ->
            when {
                j < i -> lu[i, j]
                j == i -> matrix.context.field.one
                else -> matrix.context.field.zero
            }
        }
    }


    /**
     * Returns the matrix U of the decomposition.
     *
     * U is an upper-triangular matrix
     * @return the U matrix (or null if decomposed matrix is singular)
     */
    val u: Matrix<out T> by lazy {
        matrix.context.produce { i, j ->
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
    val p: Matrix<out T> by lazy {
        matrix.context.produce { i, j ->
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
            with(matrix.context.field) {
                var determinant = if (even) one else -one
                for (i in 0 until matrix.rows) {
                    determinant *= lu[i, i]
                }
                return determinant
            }
        }

//    /**
//     * Get a solver for finding the A  X = B solution in exact linear
//     * sense.
//     * @return a solver
//     */
//    val solver: DecompositionSolver
//        get() = Solver(lu, pivot, singular)

    /**
     * In-place transformation for [MutableNDArray], using given transformation for each element
     */
    operator fun <T> MutableNDArray<T>.set(i: Int, j: Int, value: T) {
        this[listOf(i, j)] = value
    }

    abstract fun isSingular(value: T): Boolean

    private fun Field<T>.calculateLU(): Pair<NDArray<T>, IntArray> {
        if (matrix.rows != matrix.columns) {
            error("LU decomposition supports only square matrices")
        }

        fun T.abs() = if (this > zero) this else -this

        val m = matrix.columns
        val pivot = IntArray(matrix.rows)
        //TODO fix performance
        val lu: MutableNDArray<T> = NDArrays.createMutable(matrix.context.field, listOf(matrix.rows, matrix.columns)) { index -> matrix[index[0], index[1]] }

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

                sum.abs()
            } ?: col

            // Singularity check
            if (isSingular(lu[max, col].abs())) {
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
                lu[row, col] /= luDiag
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

    companion object {
        /** Default bound to determine effective singularity in LU decomposition.  */
        private const val DEFAULT_TOO_SMALL = 1e-11
    }
}

class RealLUDecomposition(matrix: Matrix<Double>, private val singularityThreshold: Double = 1e-11) : LUDecomposition<Double>(matrix) {
    override fun isSingular(value: Double): Boolean {
        return value < singularityThreshold
    }
}


/** Specialized solver.  */
class RealLUSolver : LinearSolver<Double> {

//
//    /** {@inheritDoc}  */
//    override fun solve(b: RealVector): RealVector {
//        val m = pivot.size
//        if (b.getDimension() != m) {
//            throw DimensionMismatchException(b.getDimension(), m)
//        }
//        if (singular) {
//            throw SingularMatrixException()
//        }
//
//        val bp = DoubleArray(m)
//
//        // Apply permutations to b
//        for (row in 0 until m) {
//            bp[row] = b.getEntry(pivot[row])
//        }
//
//        // Solve LY = b
//        for (col in 0 until m) {
//            val bpCol = bp[col]
//            for (i in col + 1 until m) {
//                bp[i] -= bpCol * lu[i][col]
//            }
//        }
//
//        // Solve UX = Y
//        for (col in m - 1 downTo 0) {
//            bp[col] /= lu[col][col]
//            val bpCol = bp[col]
//            for (i in 0 until col) {
//                bp[i] -= bpCol * lu[i][col]
//            }
//        }
//
//        return ArrayRealVector(bp, false)
//    }


    fun decompose(mat: Matrix<Double>, threshold: Double = 1e-11): RealLUDecomposition = RealLUDecomposition(mat, threshold)

    override fun solve(a: Matrix<Double>, b: Matrix<Double>): Matrix<Double> {
        val decomposition = decompose(a, a.context.field.zero)

        if (b.rows != a.rows) {
            error("Matrix dimension mismatch expected ${a.rows}, but got ${b.rows}")
        }

        // Apply permutations to b
        val bp = Array(a.rows) { DoubleArray(b.columns) }
        for (row in 0 until a.rows) {
            val bpRow = bp[row]
            val pRow = decomposition.pivot[row]
            for (col in 0 until b.columns) {
                bpRow[col] = b[pRow, col]
            }
        }

        // Solve LY = b
        for (col in 0 until a.rows) {
            val bpCol = bp[col]
            for (i in col + 1 until a.rows) {
                val bpI = bp[i]
                val luICol = decomposition.lu[i, col]
                for (j in 0 until b.columns) {
                    bpI[j] -= bpCol[j] * luICol
                }
            }
        }

        // Solve UX = Y
        for (col in a.rows - 1 downTo 0) {
            val bpCol = bp[col]
            val luDiag = decomposition.lu[col, col]
            for (j in 0 until b.columns) {
                bpCol[j] /= luDiag
            }
            for (i in 0 until col) {
                val bpI = bp[i]
                val luICol = decomposition.lu[i, col]
                for (j in 0 until b.columns) {
                    bpI[j] -= bpCol[j] * luICol
                }
            }
        }

        return a.context.produce { i, j -> bp[i][j] }
    }
}
