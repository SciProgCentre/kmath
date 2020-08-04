package scientifik.kmath.operations

/**
 * The generic mathematics elements which is able to store its context
 *
 * @param C the type of mathematical context for this element.
 */
interface MathElement<C> {
    /**
     * The context this element belongs to.
     */
    val context: C
}

interface MathWrapper<T, I> {
    fun unwrap(): T
    fun T.wrap(): I
}

/**
 * The element of linear context
 * @param T the type of space operation results
 * @param I self type of the element. Needed for static type checking
 * @param S the type of space
 */
interface SpaceElement<T, I : SpaceElement<T, I, S>, S : Space<T>> : MathElement<S>, MathWrapper<T, I> {
    operator fun plus(b: T): I = context.add(unwrap(), b).wrap()
    operator fun minus(b: T): I = context.add(unwrap(), context.multiply(b, -1.0)).wrap()
    operator fun times(k: Number): I = context.multiply(unwrap(), k.toDouble()).wrap()
    operator fun div(k: Number): I = context.multiply(unwrap(), 1.0 / k.toDouble()).wrap()
}

/**
 * Ring element
 */
interface RingElement<T, I : RingElement<T, I, R>, R : Ring<T>> : SpaceElement<T, I, R> {
    operator fun times(b: T): I = context.multiply(unwrap(), b).wrap()
}

/**
 * Field element
 */
interface FieldElement<T, I : FieldElement<T, I, F>, F : Field<T>> : RingElement<T, I, F> {
    override val context: F
    operator fun div(b: T): I = context.divide(unwrap(), b).wrap()
}
