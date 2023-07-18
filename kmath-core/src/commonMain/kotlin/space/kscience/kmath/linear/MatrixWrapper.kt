/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.attributes.Attribute
import space.kscience.attributes.Attributes
import space.kscience.attributes.withAttribute
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.Ring

/**
 * A [Matrix] that holds [MatrixAttribute] objects.
 *
 * @param T the type of items.
 */
public class MatrixWrapper<out T : Any> internal constructor(
    public val origin: Matrix<T>,
    override val attributes: Attributes,
) : Matrix<T> by origin {

    override fun toString(): String = "MatrixWrapper(matrix=$origin, features=$attributes)"
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
public fun <T : Any, A : Attribute<T>> Matrix<T>.withAttribute(
    attribute: A,
    attrValue: T,
): MatrixWrapper<T> = if (this is MatrixWrapper) {
    MatrixWrapper(origin, attributes.withAttribute(attribute,attrValue))
} else {
    MatrixWrapper(this, Attributes(attribute, attrValue))
}

public fun <T : Any, A : Attribute<Unit>> Matrix<T>.withAttribute(
    attribute: A,
): MatrixWrapper<T> = if (this is MatrixWrapper) {
    MatrixWrapper(origin, attributes.withAttribute(attribute))
} else {
    MatrixWrapper(this, Attributes(attribute, Unit))
}

/**
 * Modify matrix attributes
 */
public fun <T : Any> Matrix<T>.modifyAttributes(modifier: (Attributes) -> Attributes): MatrixWrapper<T> =
    if (this is MatrixWrapper) {
        MatrixWrapper(origin, modifier(attributes))
    } else {
        MatrixWrapper(this, modifier(Attributes.EMPTY))
    }

/**
 * Diagonal matrix of ones. The matrix is virtual, no actual matrix is created.
 */
public fun <T : Any> LinearSpace<T, Ring<T>>.one(
    rows: Int,
    columns: Int,
): MatrixWrapper<T> = VirtualMatrix(rows, columns) { i, j ->
    if (i == j) elementAlgebra.one else elementAlgebra.zero
}.withAttribute(IsUnit)


/**
 * A virtual matrix of zeroes
 */
public fun <T : Any> LinearSpace<T, Ring<T>>.zero(
    rows: Int,
    columns: Int,
): MatrixWrapper<T> = VirtualMatrix(rows, columns) { _, _ ->
    elementAlgebra.zero
}.withAttribute(IsZero)
