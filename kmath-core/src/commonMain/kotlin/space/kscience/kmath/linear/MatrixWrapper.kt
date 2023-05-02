/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.misc.FeatureSet
import space.kscience.kmath.nd.StructureFeature
import space.kscience.kmath.operations.Ring
import kotlin.reflect.KClass

/**
 * A [Matrix] that holds [MatrixFeature] objects.
 *
 * @param T the type of items.
 */
public class MatrixWrapper<out T : Any> internal constructor(
    public val origin: Matrix<T>,
    public val features: FeatureSet<StructureFeature>,
) : Matrix<T> by origin {

    /**
     * Get the first feature matching given class. Does not guarantee that matrix has only one feature matching the
     * criteria.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <F : StructureFeature> getFeature(type: KClass<out F>): F? =
        features.getFeature(type) ?: origin.getFeature(type)

    override fun toString(): String = "MatrixWrapper(matrix=$origin, features=$features)"
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
public fun <T : Any> Matrix<T>.withFeature(newFeature: MatrixFeature): MatrixWrapper<T> = if (this is MatrixWrapper) {
    MatrixWrapper(origin, features.with(newFeature))
} else {
    MatrixWrapper(this, FeatureSet.of(newFeature))
}

@Deprecated("To be replaced by withFeature")
public operator fun <T : Any> Matrix<T>.plus(newFeature: MatrixFeature): MatrixWrapper<T> = withFeature(newFeature)

/**
 * Add a collection of features to a [Matrix]
 */
public fun <T : Any> Matrix<T>.withFeatures(newFeatures: Iterable<MatrixFeature>): MatrixWrapper<T> =
    if (this is MatrixWrapper) {
        MatrixWrapper(origin, features.with(newFeatures))
    } else {
        MatrixWrapper(this, FeatureSet.of(newFeatures))
    }

/**
 * Diagonal matrix of ones. The matrix is virtual no actual matrix is created.
 */
public fun <T : Any> LinearSpace<T, Ring<T>>.one(
    rows: Int,
    columns: Int,
): Matrix<T> = VirtualMatrix(rows, columns) { i, j ->
    if (i == j) elementAlgebra.one else elementAlgebra.zero
}.withFeature(UnitFeature)


/**
 * A virtual matrix of zeroes
 */
public fun <T : Any> LinearSpace<T, Ring<T>>.zero(
    rows: Int,
    columns: Int,
): Matrix<T> = VirtualMatrix(rows, columns) { _, _ ->
    elementAlgebra.zero
}.withFeature(ZeroFeature)

public class TransposedFeature<out T : Any>(public val original: Matrix<T>) : MatrixFeature

/**
 * Create a virtual transposed matrix without copying anything. `A.transpose().transpose() === A`
 */
@Suppress("UNCHECKED_CAST")
@OptIn(UnstableKMathAPI::class)
public fun <T : Any> Matrix<T>.transpose(): Matrix<T> = getFeature(TransposedFeature::class)?.original as? Matrix<T>
    ?: VirtualMatrix(colNum, rowNum) { i, j -> get(j, i) }.withFeature(TransposedFeature(this))
