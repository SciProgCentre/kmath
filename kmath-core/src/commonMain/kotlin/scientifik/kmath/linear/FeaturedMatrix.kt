package scientifik.kmath.linear

import scientifik.kmath.operations.Ring
import scientifik.kmath.structures.Matrix
import scientifik.kmath.structures.Structure2D
import scientifik.kmath.structures.asBuffer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.math.sqrt

/**
 * A 2d structure plus optional matrix-specific features
 */
interface FeaturedMatrix<T : Any> : Matrix<T> {

    override val shape: IntArray get() = intArrayOf(rowNum, colNum)

    val features: Set<MatrixFeature>

    /**
     * Suggest new feature for this matrix. The result is the new matrix that may or may not reuse existing data structure.
     *
     * The implementation does not guarantee to check that matrix actually have the feature, so one should be careful to
     * add only those features that are valid.
     */
    fun suggestFeature(vararg features: MatrixFeature): FeaturedMatrix<T>

    companion object
}

inline fun Structure2D.Companion.real(rows: Int, columns: Int, initializer: (Int, Int) -> Double): Matrix<Double> {
    contract { callsInPlace(initializer) }
    return MatrixContext.real.produce(rows, columns, initializer)
}

/**
 * Build a square matrix from given elements.
 */
fun <T : Any> Structure2D.Companion.square(vararg elements: T): FeaturedMatrix<T> {
    val size: Int = sqrt(elements.size.toDouble()).toInt()
    require(size * size == elements.size) { "The number of elements ${elements.size} is not a full square" }
    val buffer = elements.asBuffer()
    return BufferMatrix(size, size, buffer)
}

val Matrix<*>.features: Set<MatrixFeature> get() = (this as? FeaturedMatrix)?.features ?: emptySet()

/**
 * Check if matrix has the given feature class
 */
inline fun <reified T : Any> Matrix<*>.hasFeature(): Boolean =
    features.find { it is T } != null

/**
 * Get the first feature matching given class. Does not guarantee that matrix has only one feature matching the criteria
 */
inline fun <reified T : Any> Matrix<*>.getFeature(): T? =
    features.filterIsInstance<T>().firstOrNull()

/**
 * Diagonal matrix of ones. The matrix is virtual no actual matrix is created
 */
fun <T : Any, R : Ring<T>> GenericMatrixContext<T, R>.one(rows: Int, columns: Int): FeaturedMatrix<T> =
    VirtualMatrix(rows, columns, DiagonalFeature) { i, j ->
        if (i == j) elementContext.one else elementContext.zero
    }


/**
 * A virtual matrix of zeroes
 */
fun <T : Any, R : Ring<T>> GenericMatrixContext<T, R>.zero(rows: Int, columns: Int): FeaturedMatrix<T> =
    VirtualMatrix(rows, columns) { _, _ -> elementContext.zero }

class TransposedFeature<T : Any>(val original: Matrix<T>) : MatrixFeature

/**
 * Create a virtual transposed matrix without copying anything. `A.transpose().transpose() === A`
 */
fun <T : Any> Matrix<T>.transpose(): Matrix<T> {
    return this.getFeature<TransposedFeature<T>>()?.original ?: VirtualMatrix(
        this.colNum,
        this.rowNum,
        setOf(TransposedFeature(this))
    ) { i, j -> get(j, i) }
}

infix fun Matrix<Double>.dot(other: Matrix<Double>): Matrix<Double> = with(MatrixContext.real) { dot(other) }
