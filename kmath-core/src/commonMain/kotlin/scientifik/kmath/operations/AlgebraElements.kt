package scientifik.kmath.operations

/**
 * The generic mathematics elements which is able to store its context
 * @param S the type of mathematical context for this element
 */
interface MathElement<S> {
    /**
     * The context this element belongs to
     */
    val context: S
}

/**
 * The element of linear context
 * @param T the type of space operation results
 * @param I self type of the element. Needed for static type checking
 * @param S the type of space
 */
interface SpaceElement<T, I : SpaceElement<T, I, S>, S : Space<T>> : MathElement<S> {
    /**
     * Self value. Needed for static type checking.
     */
    fun unwrap(): T

    fun T.wrap(): I

    operator fun plus(b: T) = context.add(unwrap(), b).wrap()
    operator fun minus(b: T) = context.add(unwrap(), context.multiply(b, -1.0)).wrap()
    operator fun times(k: Number) = context.multiply(unwrap(), k.toDouble()).wrap()
    operator fun div(k: Number) = context.multiply(unwrap(), 1.0 / k.toDouble()).wrap()
}

/**
 * Ring element
 */
interface RingElement<T, I : RingElement<T, I, R>, R : Ring<T>> : SpaceElement<T, I, R> {
    override val context: R
    operator fun times(b: T) = context.multiply(unwrap(), b).wrap()
}

/**
 * Field element
 */
interface FieldElement<T, I : FieldElement<T, I, F>, F : Field<T>> : RingElement<T, I, F> {
    override val context: F

    operator fun div(b: T) = context.divide(unwrap(), b).wrap()
}