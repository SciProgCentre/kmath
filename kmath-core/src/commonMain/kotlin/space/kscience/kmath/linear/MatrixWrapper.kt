package space.kscience.kmath.linear

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.StructureFeature
import space.kscience.kmath.nd.getFeature
import space.kscience.kmath.operations.Ring
import kotlin.reflect.KClass

/**
 * A [Matrix] that holds [MatrixFeature] objects.
 *
 * @param T the type of items.
 */
public class MatrixWrapper<T : Any> internal constructor(
    public val origin: Matrix<T>,
    public val features: Set<MatrixFeature>,
) : Matrix<T> by origin {

    /**
     * Get the first feature matching given class. Does not guarantee that matrix has only one feature matching the criteria
     */
    @UnstableKMathAPI
    @Suppress("UNCHECKED_CAST")
    override fun <F : StructureFeature> getFeature(type: KClass<out F>): F? = features.singleOrNull { type.isInstance(it) } as? F
        ?: origin.getFeature(type)

    override fun toString(): String {
        return "MatrixWrapper(matrix=$origin, features=$features)"
    }
}

/**
 * Return the original matrix. If this is a wrapper, return its origin. If not, this matrix.
 * Origin does not necessary store all features.
 */
@UnstableKMathAPI
public val <T : Any> Matrix<T>.origin: Matrix<T>
    get() = (this as? MatrixWrapper)?.origin ?: this

/**
 * Add a single feature to a [Matrix]
 */
public operator fun <T : Any> Matrix<T>.plus(newFeature: MatrixFeature): MatrixWrapper<T> = if (this is MatrixWrapper) {
    MatrixWrapper(origin, features + newFeature)
} else {
    MatrixWrapper(this, setOf(newFeature))
}

/**
 * Add a collection of features to a [Matrix]
 */
public operator fun <T : Any> Matrix<T>.plus(newFeatures: Collection<MatrixFeature>): MatrixWrapper<T> =
    if (this is MatrixWrapper) {
        MatrixWrapper(origin, features + newFeatures)
    } else {
        MatrixWrapper(this, newFeatures.toSet())
    }

/**
 * Diagonal matrix of ones. The matrix is virtual no actual matrix is created
 */
public fun <T : Any> LinearSpace<T, Ring<T>>.one(
    rows: Int,
    columns: Int,
): Matrix<T> = VirtualMatrix(rows, columns) { i, j ->
    if (i == j) elementAlgebra.one else elementAlgebra.zero
} + UnitFeature


/**
 * A virtual matrix of zeroes
 */
public fun <T : Any> LinearSpace<T, Ring<T>>.zero(
    rows: Int,
    columns: Int,
): Matrix<T> = VirtualMatrix(rows, columns) { _, _ ->
    elementAlgebra.zero
} + ZeroFeature

public class TransposedFeature<T : Any>(public val original: Matrix<T>) : MatrixFeature

/**
 * Create a virtual transposed matrix without copying anything. `A.transpose().transpose() === A`
 */
@OptIn(UnstableKMathAPI::class)
public fun <T : Any> Matrix<T>.transpose(): Matrix<T> = getFeature<TransposedFeature<T>>()?.original ?: VirtualMatrix(
    colNum,
    rowNum,
) { i, j -> get(j, i) } + TransposedFeature(this)