/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.misc.UnstableKMathAPI

/**
 * The generic mathematics elements which is able to store its context
 *
 * @param C the type of mathematical context for this element.
 * @param T the type wrapped by this wrapper.
 */
@UnstableKMathAPI
@Deprecated("AlgebraElements are considered odd and will be removed in future releases.")
public interface AlgebraElement<T, C : Algebra<T>> {
    /**
     * The context this element belongs to.
     */
    public val context: C
}
//
///**
// * Divides this element by number.
// *
// * @param k the divisor.
// * @return the quotient.
// */
//public operator fun <T : AlgebraElement<T, S>, S : Space<T>> T.div(k: Number): T =
//    context.multiply(this, 1.0 / k.toDouble())
//
///**
// * Multiplies this element by number.
// *
// * @param k the multiplicand.
// * @return the product.
// */
//public operator fun <T : AlgebraElement<T, S>, S : Space<T>> T.times(k: Number): T =
//    context.multiply(this, k.toDouble())

/**
 * Subtracts element from this one.
 *
 * @param b the subtrahend.
 * @return the difference.
 */
@UnstableKMathAPI
@Deprecated("AlgebraElements are considered odd and will be removed in future releases.")
public operator fun <T : AlgebraElement<T, S>, S : NumbersAddOperations<T>> T.minus(b: T): T =
    context.add(this, context.run { -b })

/**
 * Adds element to this one.
 *
 * @receiver the augend.
 * @param b the addend.
 * @return the sum.
 */
@UnstableKMathAPI
@Deprecated("AlgebraElements are considered odd and will be removed in future releases.")
public operator fun <T : AlgebraElement<T, S>, S : Ring<T>> T.plus(b: T): T =
    context.add(this, b)

///**
// * Number times element
// */
//public operator fun <T : AlgebraElement<T, S>, S : Space<T>> Number.times(element: T): T =
//    element.times(this)

/**
 * Multiplies this element by another one.
 *
 * @receiver the multiplicand.
 * @param b the multiplier.
 * @return the product.
 */
@UnstableKMathAPI
@Deprecated("AlgebraElements are considered odd and will be removed in future releases.")
public operator fun <T : AlgebraElement<T, R>, R : Ring<T>> T.times(b: T): T =
    context.multiply(this, b)


/**
 * Divides this element by another one.
 *
 * @param b the divisor.
 * @return the quotient.
 */
@UnstableKMathAPI
@Deprecated("AlgebraElements are considered odd and will be removed in future releases.")
public operator fun <T : AlgebraElement<T, F>, F : Field<T>> T.div(b: T): T =
    context.divide(this, b)


/**
 * The element of [Group].
 *
 * @param T the type of space operation results.
 * @param I self type of the element. Needed for static type checking.
 * @param S the type of space.
 */
@UnstableKMathAPI
@Deprecated("AlgebraElements are considered odd and will be removed in future releases.")
public interface GroupElement<T : GroupElement<T, S>, S : Group<T>> : AlgebraElement<T, S>

/**
 * The element of [Ring].
 *
 * @param T the type of ring operation results.
 * @param I self type of the element. Needed for static type checking.
 * @param R the type of ring.
 */
@UnstableKMathAPI
@Deprecated("AlgebraElements are considered odd and will be removed in future releases.")
public interface RingElement<T : RingElement<T, R>, R : Ring<T>> : GroupElement<T, R>

/**
 * The element of [Field].
 *
 * @param T the type of field operation results.
 * @param I self type of the element. Needed for static type checking.
 * @param F the type of field.
 */
@UnstableKMathAPI
@Deprecated("AlgebraElements are considered odd and will be removed in future releases.")
public interface FieldElement<T : FieldElement<T, F>, F : Field<T>> : RingElement<T, F>
