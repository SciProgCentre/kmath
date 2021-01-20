package kscience.kmath.linear

import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.operations.Ring
import kscience.kmath.structures.Matrix
import kscience.kmath.structures.Structure2D
import kscience.kmath.structures.asBuffer
import kscience.kmath.structures.getFeature
import kotlin.math.sqrt
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

/**
 * A [Matrix] that holds [MatrixFeature] objects.
 *
 * @param T the type of items.
 */
public class MatrixWrapper<T : Any>(
    public val matrix: Matrix<T>,
    public val features: Set<MatrixFeature>,
) : Matrix<T> by matrix {

    /**
     * Get the first feature matching given class. Does not guarantee that matrix has only one feature matching the criteria
     */
    @UnstableKMathAPI
    override fun <T : Any> getFeature(type: KClass<T>): T? = type.safeCast(features.find { type.isInstance(it) })
        ?: matrix.getFeature(type)

    override fun equals(other: Any?): Boolean = matrix == other
    override fun hashCode(): Int = matrix.hashCode()
    override fun toString(): String {
        return "MatrixWrapper(matrix=$matrix, features=$features)"
    }
}

/**
 * Add a single feature to a [Matrix]
 */
public operator fun <T : Any> Matrix<T>.plus(newFeature: MatrixFeature): MatrixWrapper<T> = if (this is MatrixWrapper) {
    MatrixWrapper(matrix, features + newFeature)
} else {
    MatrixWrapper(this, setOf(newFeature))
}

/**
 * Add a collection of features to a [Matrix]
 */
public operator fun <T : Any> Matrix<T>.plus(newFeatures: Collection<MatrixFeature>): MatrixWrapper<T> =
    if (this is MatrixWrapper) {
        MatrixWrapper(matrix, features + newFeatures)
    } else {
        MatrixWrapper(this, newFeatures.toSet())
    }

public inline fun Structure2D.Companion.real(
    rows: Int,
    columns: Int,
    initializer: (Int, Int) -> Double,
): BufferMatrix<Double> = MatrixContext.real.produce(rows, columns, initializer)

/**
 * Build a square matrix from given elements.
 */
public fun <T : Any> Structure2D.Companion.square(vararg elements: T): Matrix<T> {
    val size: Int = sqrt(elements.size.toDouble()).toInt()
    require(size * size == elements.size) { "The number of elements ${elements.size} is not a full square" }
    val buffer = elements.asBuffer()
    return BufferMatrix(size, size, buffer)
}

/**
 * Diagonal matrix of ones. The matrix is virtual no actual matrix is created
 */
public fun <T : Any, R : Ring<T>> GenericMatrixContext<T, R, *>.one(rows: Int, columns: Int): Matrix<T> =
    VirtualMatrix(rows, columns) { i, j ->
        if (i == j) elementContext.one else elementContext.zero
    } + UnitFeature


/**
 * A virtual matrix of zeroes
 */
public fun <T : Any, R : Ring<T>> GenericMatrixContext<T, R, *>.zero(rows: Int, columns: Int): Matrix<T> =
    VirtualMatrix(rows, columns) { _, _ -> elementContext.zero } + ZeroFeature

public class TransposedFeature<T : Any>(public val original: Matrix<T>) : MatrixFeature

/**
 * Create a virtual transposed matrix without copying anything. `A.transpose().transpose() === A`
 */
@OptIn(UnstableKMathAPI::class)
public fun <T : Any> Matrix<T>.transpose(): Matrix<T> {
    return getFeature<TransposedFeature<T>>()?.original ?: VirtualMatrix(
        colNum,
        rowNum,
    ) { i, j -> get(j, i) } + TransposedFeature(this)
}