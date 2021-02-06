package kscience.kmath.operations

import kscience.kmath.misc.UnstableKMathAPI

/**
 * The generic mathematics elements which is able to store its context
 *
 * @param C the type of mathematical context for this element.
 * @param T the type wrapped by this wrapper.
 */
public interface AlgebraElement<T, C : Algebra<T>> {
    /**
     * The context this element belongs to.
     */
    public val context: C
}

/**
 * Divides this element by number.
 *
 * @param k the divisor.
 * @return the quotient.
 */
public operator fun <T : AlgebraElement<T, S>, S : Space<T>> T.div(k: Number): T =
    context.multiply(this, 1.0 / k.toDouble())

/**
 * Multiplies this element by number.
 *
 * @param k the multiplicand.
 * @return the product.
 */
public operator fun <T : AlgebraElement<T, S>, S : Space<T>> T.times(k: Number): T =
    context.multiply(this, k.toDouble())

/**
 * Subtracts element from this one.
 *
 * @param b the subtrahend.
 * @return the difference.
 */
public operator fun <T : AlgebraElement<T, S>, S : Space<T>> T.minus(b: T): T =
    context.add(this, context.multiply(b, -1.0))

/**
 * Adds element to this one.
 *
 * @param b the augend.
 * @return the sum.
 */
public operator fun <T : AlgebraElement<T, S>, S : Space<T>> T.plus(b: T): T =
    context.add(this, b)

/**
 * Number times element
 */
public operator fun <T : AlgebraElement<T, S>, S : Space<T>> Number.times(element: T): T =
    element.times(this)


/**
 * Multiplies this element by another one.
 *
 * @param b the multiplicand.
 * @return the product.
 */
public operator fun <T : AlgebraElement<T, R>, R : Ring<T>> T.times(b: T): T =
    context.multiply(this, b)


/**
 * Divides this element by another one.
 *
 * @param b the divisor.
 * @return the quotient.
 */
public operator fun <T : AlgebraElement<T, F>, F : Field<T>> T.div(b: T): T =
    context.divide(this, b)


/**
 * The element of [Space].
 *
 * @param T the type of space operation results.
 * @param I self type of the element. Needed for static type checking.
 * @param S the type of space.
 */
@UnstableKMathAPI
public interface SpaceElement<T : SpaceElement<T, S>, S : Space<T>> : AlgebraElement<T, S>

/**
 * The element of [Ring].
 *
 * @param T the type of ring operation results.
 * @param I self type of the element. Needed for static type checking.
 * @param R the type of ring.
 */
@UnstableKMathAPI
public interface RingElement<T : RingElement<T, R>, R : Ring<T>> : SpaceElement<T, R>

/**
 * The element of [Field].
 *
 * @param T the type of field operation results.
 * @param I self type of the element. Needed for static type checking.
 * @param F the type of field.
 */
@UnstableKMathAPI
public interface FieldElement<T : FieldElement<T, F>, F : Field<T>> : RingElement<T, F>