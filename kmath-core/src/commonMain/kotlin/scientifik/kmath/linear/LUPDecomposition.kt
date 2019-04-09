package scientifik.kmath.linear

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.Ring
import scientifik.kmath.structures.*
import kotlin.reflect.KClass

/**
 * Common implementation of [LUPDecompositionFeature]
 */
class LUPDecomposition<T : Any>(
    private val elementContext: Ring<T>,
    val lu: Structure2D<T>,
    val pivot: IntArray,
    private val even: Boolean
) : LUPDecompositionFeature<T>, DeterminantFeature<T> {

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

open class BufferAccessor<T : Any>(val type: KClass<T>, val field: Field<T>, val rowNum: Int, val colNum: Int) {
    open operator fun MutableBuffer<T>.get(i: Int, j: Int) = get(i + colNum * j)
    open operator fun MutableBuffer<T>.set(i: Int, j: Int, value: T) {
        set(i + colNum * j, value)
    }

    fun create(init: (i: Int, j: Int) -> T) =
        MutableBuffer.auto(type, rowNum * colNum) { offset -> init(offset / colNum, offset % colNum) }

    fun create(mat: Structure2D<T>) = create { i, j -> mat[i, j] }

    //TODO optimize wrapper
    fun MutableBuffer<T>.collect(): Structure2D<T> =
        NDStructure.auto(type, rowNum, colNum) { (i, j) -> get(i, j) }.as2D()

    open fun MutableBuffer<T>.innerProduct(row: Int, col: Int, max: Int): T {
        var sum = field.zero
        field.run {
            for (i in 0 until max) {
                sum += get(row, i) * get(i, col)
            }
        }
        return sum
    }

    open fun MutableBuffer<T>.divideInPlace(i: Int, j: Int, factor: T) {
        field.run { set(i, j, get(i, j) / factor) }
    }

    open fun MutableBuffer<T>.subtractInPlace(i: Int, j: Int, lu: MutableBuffer<T>, col: Int) {
        field.run {
            set(i, j, get(i, j) - get(col, j) * lu[i, col])
        }
    }
}

/**
 * Specialized LU operations for Doubles
 */
class RealBufferAccessor(rowNum: Int, colNum: Int) : BufferAccessor<Double>(Double::class, RealField, rowNum, colNum) {
    override inline fun MutableBuffer<Double>.get(i: Int, j: Int) = (this as DoubleBuffer).array[i + colNum * j]
    override inline fun MutableBuffer<Double>.set(i: Int, j: Int, value: Double) {
        (this as DoubleBuffer).array[i + colNum * j] = value
    }

    override fun MutableBuffer<Double>.innerProduct(row: Int, col: Int, max: Int): Double {
        var sum = 0.0
        for (i in 0 until max) {
            sum += get(row, i) * get(i, col)
        }
        return sum
    }

    override fun MutableBuffer<Double>.divideInPlace(i: Int, j: Int, factor: Double) {
        set(i, j, get(i, j) / factor)
    }

    override fun MutableBuffer<Double>.subtractInPlace(i: Int, j: Int, lu: MutableBuffer<Double>, col: Int) {
        set(i, j, get(i, j) - get(col, j) * lu[i, col])
    }
}

fun <T : Comparable<T>, F : Field<T>> GenericMatrixContext<T, F>.buildAccessor(
    type:KClass<T>,
    rowNum: Int,
    colNum: Int
): BufferAccessor<T> {
    return if (elementContext == RealField) {
        @Suppress("UNCHECKED_CAST")
        RealBufferAccessor(rowNum, colNum) as BufferAccessor<T>
    } else {
        BufferAccessor(type, elementContext, rowNum, colNum)
    }
}

fun <T : Comparable<T>, F : Field<T>> GenericMatrixContext<T, F>.abs(value: T) =
    if (value > elementContext.zero) value else with(elementContext) { -value }


fun <T : Comparable<T>, F : Field<T>> GenericMatrixContext<T, F>.lupDecompose(
    type: KClass<T>,
    matrix: Matrix<T>,
    checkSingular: (T) -> Boolean
): LUPDecomposition<T> {
    if (matrix.rowNum != matrix.colNum) {
        error("LU decomposition supports only square matrices")
    }


    val m = matrix.colNum
    val pivot = IntArray(matrix.rowNum)

    buildAccessor(type, matrix.rowNum, matrix.colNum).run {

        val lu = create(matrix)

        // Initialize permutation array and parity
        for (row in 0 until m) {
            pivot[row] = row
        }
        var even = true

        // Loop over columns
        for (col in 0 until m) {

            // upper
            for (row in 0 until col) {
//                var sum = lu[row, col]
//                for (i in 0 until row) {
//                    sum -= lu[row, i] * lu[i, col]
//                }
                val sum = lu.innerProduct(row, col, row)
                lu[row, col] = field.run { lu[row, col] - sum }
            }

            // lower
            val max = (col until m).maxBy { row ->
                //                var sum = lu[row, col]
//                for (i in 0 until col) {
//                    sum -= lu[row, i] * lu[i, col]
//                }
//                lu[row, col] = sum
                val sum = lu.innerProduct(row, col, col)
                lu[row, col] = field.run { lu[row, col] - sum }
                abs(sum)
            } ?: col

            // Singularity check
            if (checkSingular(lu[max, col])) {
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
                lu.divideInPlace(row, col, luDiag)
                //lu[row, col] = lu[row, col] / luDiag
            }
        }
        return scientifik.kmath.linear.LUPDecomposition(elementContext, lu.collect(), pivot, even)

    }
}

/**
 * Solve a linear equation **a*x = b**
 */
fun <T : Comparable<T>, F : Field<T>> GenericMatrixContext<T, F>.solve(
    type: KClass<T>,
    a: Matrix<T>,
    b: Matrix<T>,
    checkSingular: (T) -> Boolean
): Matrix<T> {
    if (b.rowNum != a.colNum) {
        error("Matrix dimension mismatch. Expected ${a.rowNum}, but got ${b.colNum}")
    }

    // Use existing decomposition if it is provided by matrix
    val decomposition = a.getFeature() ?: lupDecompose(type, a, checkSingular)

    buildAccessor(type, a.rowNum, a.colNum).run {

        val lu = create(decomposition.lu)

        // Apply permutations to b
        val bp = create { i, j ->
            b[decomposition.pivot[i], j]
        }

        // Solve LY = b
        for (col in 0 until a.rowNum) {
            for (i in col + 1 until a.rowNum) {
                for (j in 0 until b.colNum) {
                    bp.subtractInPlace(i, j, lu, col)
                    //bp[i, j] -= bp[col, j] * lu[i, col]
                }
            }
        }

        // Solve UX = Y
        for (col in a.rowNum - 1 downTo 0) {
            val luDiag = lu[col, col]
            for (j in 0 until b.colNum) {
                bp.divideInPlace(col, j, luDiag)
                //bp[col, j] /= lu[col, col]
            }
            for (i in 0 until col) {
                for (j in 0 until b.colNum) {
                    bp.subtractInPlace(i, j, lu, col)
                    //bp[i, j] -= bp[col, j] * lu[i, col]
                }
            }
        }

        return produce(a.rowNum, a.colNum) { i, j -> bp[i, j] }
    }

}

inline fun <reified T : Comparable<T>, F : Field<T>> GenericMatrixContext<T, F>.inverse(
    matrix: Matrix<T>,
    noinline checkSingular: (T) -> Boolean
) =
    solve(T::class, matrix, one(matrix.rowNum, matrix.colNum), checkSingular)

fun GenericMatrixContext<Double, RealField>.inverse(matrix: Matrix<Double>) =
    inverse(matrix) { it < 1e-11 }