package kscience.kmath.linear

import kscience.kmath.operations.Complex
import kscience.kmath.operations.Ring
import kscience.kmath.operations.conjugate
import kscience.kmath.structures.Matrix
import kscience.kmath.structures.Structure2D
import kscience.kmath.structures.asBuffer
import kotlin.jvm.JvmName
import kotlin.math.sqrt

/**
 * A [Matrix] that holds [MatrixFeature] objects.
 *
 * @param T the type of items.
 */
public interface FeaturedMatrix<T : Any> : Matrix<T> {
    public override val shape: IntArray get() = intArrayOf(rowNum, colNum)

    /**
     * The set of features this matrix possesses.
     */
    public val features: Set<MatrixFeature>

    /**
     * Suggest new feature for this matrix. The result is the new matrix that may or may not reuse existing data structure.
     *
     * The implementation does not guarantee to check that matrix actually have the feature, so one should be careful to
     * add only those features that are valid.
     */
    public fun suggestFeature(vararg features: MatrixFeature): FeaturedMatrix<T>

    public companion object
}

public inline fun Structure2D.Companion.real(
    rows: Int,
    columns: Int,
    initializer: (Int, Int) -> Double,
): BufferMatrix<Double> = MatrixContext.real.produce(rows, columns, initializer)

/**
 * Build a square matrix from given elements.
 */
public fun <T : Any> Structure2D.Companion.square(vararg elements: T): FeaturedMatrix<T> {
    val size: Int = sqrt(elements.size.toDouble()).toInt()
    require(size * size == elements.size) { "The number of elements ${elements.size} is not a full square" }
    val buffer = elements.asBuffer()
    return BufferMatrix(size, size, buffer)
}

public val Matrix<*>.features: Set<MatrixFeature> get() = (this as? FeaturedMatrix)?.features ?: emptySet()

/**
 * Check if matrix has the given feature class
 */
public inline fun <reified T : Any> Matrix<*>.hasFeature(): Boolean =
    features.find { it is T } != null

/**
 * Get the first feature matching given class. Does not guarantee that matrix has only one feature matching the criteria
 */
@Suppress("UNCHECKED_CAST")
public inline fun <reified T : Any> Matrix<*>.getFeature(): T? =
    features.find { it is T }?.let { it as T }

/**
 * Diagonal matrix of ones. The matrix is virtual no actual matrix is created
 */
public fun <T : Any, R : Ring<T>> GenericMatrixContext<T, R, *>.one(rows: Int, columns: Int): FeaturedMatrix<T> =
    VirtualMatrix(rows, columns, DiagonalFeature) { i, j ->
        if (i == j) elementContext.one else elementContext.zero
    }


/**
 * Returns a [VirtualMatrix] of zeroes.
 *
 * @param T the type of matrix's items.
 * @param R the type of ring over the matrix's items.
 * @receiver the matrix context to provide the [R] ring.
 * @param rows the count of rows.
 * @param columns the count of columns.
 * @return a new virtual matrix.
 */
public fun <T : Any, R : Ring<T>> GenericMatrixContext<T, R, *>.zero(rows: Int, columns: Int): FeaturedMatrix<T> =
    VirtualMatrix(rows, columns) { _, _ -> elementContext.zero }

/**
 * Matrices with this feature were transposed previously and hold the reference to their original.
 *
 * @param T the type of matrices' items.
 * @property original the matrix before transposition.
 */
public inline class TransposedFeature<T : Any>(public val original: Matrix<T>) : MatrixFeature

/**
 * Create a virtual transposed matrix without copying anything. `A.transpose().transpose() === A`.
 */
public fun <T : Any> Matrix<T>.transpose(): Matrix<T> = getFeature<TransposedFeature<T>>()?.original ?: VirtualMatrix(
    colNum,
    rowNum,
    setOf(TransposedFeature(this)),
) { i, j -> get(j, i) }

/**
 * Returns Hermitian conjugate of this matrix (i.e., just transposes it).
 *
 *
 */
@JvmName("transposeConjugateDouble")
public fun Matrix<Double>.transposeConjugate(): Matrix<Double> = transpose()

/**
 * Returns Hermitian conjugate of this matrix (i.e., transposes it and replaces each element with its conjugate).
 *
 * @return the Hermitian conjugate of this matrix.
 */
@JvmName("transposeConjugateComplex")
public fun Matrix<Complex>.transposeConjugate(): Matrix<Complex> {
    val t = transpose()
    return VirtualMatrix(t.rowNum, t.colNum) { i, j -> t[i, j].conjugate }
}
