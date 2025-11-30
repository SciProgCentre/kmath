/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.attributes.Attribute
import space.kscience.attributes.Attributes
import space.kscience.attributes.withAttribute
import space.kscience.attributes.withFlag
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.Ring

/**
 * A [Matrix] that holds [MatrixAttribute] objects.
 *
 * @param T the type of items.
 */
public class MatrixWrapper<out T> internal constructor(
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
public fun <T> Matrix<T>.withAttribute(
    attribute: Attribute<T>,
    attrValue: T,
): MatrixWrapper<T> = if (this is MatrixWrapper) {
    MatrixWrapper(origin, attributes.withAttribute(attribute, attrValue))
} else {
    MatrixWrapper(this, Attributes(attribute, attrValue))
}

public fun <T> Matrix<T>.withAttribute(
    attribute: Attribute<Unit>,
): MatrixWrapper<T> = if (this is MatrixWrapper) {
    MatrixWrapper(origin, attributes.withFlag(attribute))
} else {
    MatrixWrapper(this, Attributes(attribute, Unit))
}

/**
 * Add boolean attribute with default value `true`
 */
public fun <T> Matrix<T>.withAttribute(
    attribute: Attribute<Boolean>,
    value: Boolean = true
): MatrixWrapper<T> = if (this is MatrixWrapper) {
    MatrixWrapper(origin, attributes.withAttribute(attribute, value))
} else {
    MatrixWrapper(this, Attributes(attribute, value))
}

/**
 * Modify matrix attributes
 */
public fun <T> Matrix<T>.modifyAttributes(modifier: (Attributes) -> Attributes): MatrixWrapper<T> =
    if (this is MatrixWrapper) {
        MatrixWrapper(origin, modifier(attributes))
    } else {
        MatrixWrapper(this, modifier(Attributes.EMPTY))
    }

/**
 * Diagonal matrix of ones. The matrix is virtual, no actual matrix is created.
 */
public fun <T> LinearSpace<T, Ring<T>>.one(
    rows: Int,
    columns: Int,
): MatrixWrapper<T> = VirtualMatrix(rows, columns) { i, j ->
    if (i == j) elementAlgebra.one else elementAlgebra.zero
}.withAttribute(IsUnit)


/**
 * A virtual matrix of zeroes
 */
public fun <T> LinearSpace<T, Ring<T>>.zero(
    rows: Int,
    columns: Int,
): MatrixWrapper<T> = VirtualMatrix(rows, columns) { _, _ ->
    elementAlgebra.zero
}.withAttribute(IsZero)
