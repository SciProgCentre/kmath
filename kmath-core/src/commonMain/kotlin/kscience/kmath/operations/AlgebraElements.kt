package kscience.kmath.operations

/**
 * The generic mathematics elements which is able to store its context
 *
 * @param C the type of mathematical context for this element.
 */
public interface MathElement<C> {
    /**
     * The context this element belongs to.
     */
    public val context: C
}

/**
 * Represents element that can be wrapped to its "primitive" value.
 *
 * @param T the type wrapped by this wrapper.
 * @param I the type of this wrapper.
 */
public interface MathWrapper<T, I> {
    /**
     * Unwraps [I] to [T].
     */
    public fun unwrap(): T

    /**
     * Wraps [T] to [I].
     */
    public fun T.wrap(): I
}

/**
 * The element of [Space].
 *
 * @param T the type of space operation results.
 * @param I self type of the element. Needed for static type checking.
 * @param S the type of space.
 */
public interface SpaceElement<T, I : SpaceElement<T, I, S>, S : Space<T>> : MathElement<S>, MathWrapper<T, I> {
    /**
     * Adds element to this one.
     *
     * @param b the augend.
     * @return the sum.
     */
    public operator fun plus(b: T): I = context.add(unwrap(), b).wrap()

    /**
     * Subtracts element from this one.
     *
     * @param b the subtrahend.
     * @return the difference.
     */
    public operator fun minus(b: T): I = context.add(unwrap(), context.multiply(b, -1.0)).wrap()

    /**
     * Multiplies this element by number.
     *
     * @param k the multiplicand.
     * @return the product.
     */
    public operator fun times(k: Number): I = context.multiply(unwrap(), k.toDouble()).wrap()

    /**
     * Divides this element by number.
     *
     * @param k the divisor.
     * @return the quotient.
     */
    public operator fun div(k: Number): I = context.multiply(unwrap(), 1.0 / k.toDouble()).wrap()
}

/**
 * The element of [Ring].
 *
 * @param T the type of ring operation results.
 * @param I self type of the element. Needed for static type checking.
 * @param R the type of ring.
 */
public interface RingElement<T, I : RingElement<T, I, R>, R : Ring<T>> : SpaceElement<T, I, R> {
    /**
     * Multiplies this element by another one.
     *
     * @param b the multiplicand.
     * @return the product.
     */
    public operator fun times(b: T): I = context.multiply(unwrap(), b).wrap()
}

/**
 * The element of [Field].
 *
 * @param T the type of field operation results.
 * @param I self type of the element. Needed for static type checking.
 * @param F the type of field.
 */
public interface FieldElement<T, I : FieldElement<T, I, F>, F : Field<T>> : RingElement<T, I, F> {
    override val context: F

    /**
     * Divides this element by another one.
     *
     * @param b the divisor.
     * @return the quotient.
     */
    public operator fun div(b: T): I = context.divide(unwrap(), b).wrap()
}
