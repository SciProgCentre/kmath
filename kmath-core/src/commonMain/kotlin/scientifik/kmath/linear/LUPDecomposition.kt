package scientifik.kmath.linear

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.Ring
import scientifik.kmath.structures.BufferAccessor2D
import scientifik.kmath.structures.Matrix
import scientifik.kmath.structures.Structure2D
import kotlin.reflect.KClass

/**
 * Common implementation of [LUPDecompositionFeature]
 */
class LUPDecomposition<T : Any>(
    val context: GenericMatrixContext<T, out Field<T>>,
    val lu: Structure2D<T>,
    val pivot: IntArray,
    private val even: Boolean
) : LUPDecompositionFeature<T>, DeterminantFeature<T> {

    val elementContext get() = context.elementContext

    /**
     * Returns the matrix L of the decomposition.
     *
     * L is a lower-triangular matrix with [Ring.one] in diagonal
     */
    override val l: FeaturedMatrix<T> = VirtualMatrix(lu.shape[0], lu.shape[1], setOf(LFeature)) { i, j ->
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
    override val u: FeaturedMatrix<T> = VirtualMatrix(lu.shape[0], lu.shape[1], setOf(UFeature)) { i, j ->
        if (j >= i) lu[i, j] else elementContext.zero
    }


    /**
     * Returns the P rows permutation matrix.
     *
     * P is a sparse matrix with exactly one element set to [Ring.one] in
     * each row and each column, all other elements being set to [Ring.zero].
     */
    override val p: FeaturedMatrix<T> = VirtualMatrix(lu.shape[0], lu.shape[1]) { i, j ->
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

fun <T : Comparable<T>, F : Field<T>> GenericMatrixContext<T, F>.abs(value: T) =
    if (value > elementContext.zero) value else with(elementContext) { -value }


/**
 * Create a lup decomposition of generic matrix
 */
fun <T : Comparable<T>, F : Field<T>> GenericMatrixContext<T, F>.lup(
    type: KClass<T>,
    matrix: Matrix<T>,
    checkSingular: (T) -> Boolean
): LUPDecomposition<T> {
    if (matrix.rowNum != matrix.colNum) {
        error("LU decomposition supports only square matrices")
    }

    val m = matrix.colNum
    val pivot = IntArray(matrix.rowNum)

    //TODO just waits for KEEP-176
    BufferAccessor2D(type, matrix.rowNum, matrix.colNum).run {
        elementContext.run {

            val lu = create(matrix)

            // Initialize permutation array and parity
            for (row in 0 until m) {
                pivot[row] = row
            }
            var even = true

            // Initialize permutation array and parity
            for (row in 0 until m) {
                pivot[row] = row
            }

            // Loop over columns
            for (col in 0 until m) {

                // upper
                for (row in 0 until col) {
                    val luRow = lu.row(row)
                    var sum = luRow[col]
                    for (i in 0 until row) {
                        sum -= luRow[i] * lu[i, col]
                    }
                    luRow[col] = sum
                }

                // lower
                var max = col // permutation row
                var largest = -one
                for (row in col until m) {
                    val luRow = lu.row(row)
                    var sum = luRow[col]
                    for (i in 0 until col) {
                        sum -= luRow[i] * lu[i, col]
                    }
                    luRow[col] = sum

                    // maintain best permutation choice
                    if (this@lup.abs(sum) > largest) {
                        largest = this@lup.abs(sum)
                        max = row
                    }
                }

                // Singularity check
                if (checkSingular(this@lup.abs(lu[max, col]))) {
                    error("The matrix is singular")
                }

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
                for (row in col + 1 until m) {
                    lu[row, col] /= luDiag
                }
            }

            return LUPDecomposition(this@lup, lu.collect(), pivot, even)
        }
    }
}

inline fun <reified T : Comparable<T>, F : Field<T>> GenericMatrixContext<T, F>.lup(
    matrix: Matrix<T>,
    noinline checkSingular: (T) -> Boolean
) = lup(T::class, matrix, checkSingular)

fun GenericMatrixContext<Double, RealField>.lup(matrix: Matrix<Double>) = lup(Double::class, matrix) { it < 1e-11 }

fun <T : Any> LUPDecomposition<T>.solve(type: KClass<T>, matrix: Matrix<T>): Matrix<T> {

    if (matrix.rowNum != pivot.size) {
        error("Matrix dimension mismatch. Expected ${pivot.size}, but got ${matrix.colNum}")
    }

    BufferAccessor2D(type, matrix.rowNum, matrix.colNum).run {
        elementContext.run {

            // Apply permutations to b
            val bp = create { _, _ -> zero }

            for (row in 0 until pivot.size) {
                val bpRow = bp.row(row)
                val pRow = pivot[row]
                for (col in 0 until matrix.colNum) {
                    bpRow[col] = matrix[pRow, col]
                }
            }

            // Solve LY = b
            for (col in 0 until pivot.size) {
                val bpCol = bp.row(col)
                for (i in col + 1 until pivot.size) {
                    val bpI = bp.row(i)
                    val luICol = lu[i, col]
                    for (j in 0 until matrix.colNum) {
                        bpI[j] -= bpCol[j] * luICol
                    }
                }
            }

            // Solve UX = Y
            for (col in pivot.size - 1 downTo 0) {
                val bpCol = bp.row(col)
                val luDiag = lu[col, col]
                for (j in 0 until matrix.colNum) {
                    bpCol[j] /= luDiag
                }
                for (i in 0 until col) {
                    val bpI = bp.row(i)
                    val luICol = lu[i, col]
                    for (j in 0 until matrix.colNum) {
                        bpI[j] -= bpCol[j] * luICol
                    }
                }
            }
            return context.produce(pivot.size, matrix.colNum) { i, j -> bp[i, j] }
        }
    }
}

inline fun <reified T : Any> LUPDecomposition<T>.solve(matrix: Matrix<T>) = solve(T::class, matrix)

/**
 * Solve a linear equation **a*x = b**
 */
inline fun <reified T : Comparable<T>, F : Field<T>> GenericMatrixContext<T, F>.solve(
    a: Matrix<T>,
    b: Matrix<T>,
    noinline checkSingular: (T) -> Boolean
): Matrix<T> {
    // Use existing decomposition if it is provided by matrix
    val decomposition = a.getFeature() ?: lup(T::class, a, checkSingular)
    return decomposition.solve(T::class, b)
}

fun RealMatrixContext.solve(a: Matrix<Double>, b: Matrix<Double>) =
    solve(a, b) { it < 1e-11 }

inline fun <reified T : Comparable<T>, F : Field<T>> GenericMatrixContext<T, F>.inverse(
    matrix: Matrix<T>,
    noinline checkSingular: (T) -> Boolean
) = solve(matrix, one(matrix.rowNum, matrix.colNum), checkSingular)

fun RealMatrixContext.inverse(matrix: Matrix<Double>) =
    solve(matrix, one(matrix.rowNum, matrix.colNum)) { it < 1e-11 }