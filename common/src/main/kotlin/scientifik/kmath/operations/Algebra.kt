package scientifik.kmath.operations

/**
 * A general interface representing linear context of some kind.
 * The context defines sum operation for its elements and multiplication by real value.
 * One must note that in some cases context is a singleton class, but in some cases it
 * works as a context for operations inside it.
 *
 * TODO do we need commutative context?
 */
interface Space<T> {
    /**
     * Neutral element for sum operation
     */
    val zero: T

    /**
     * Addition operation for two context elements
     */
    fun add(a: T, b: T): T

    /**
     * Multiplication operation for context element and real number
     */
    fun multiply(a: T, k: Double): T

    //Operation to be performed in this context
    operator fun T.unaryMinus(): T = multiply(this, -1.0)

    operator fun T.plus(b: T): T = add(this, b)
    operator fun T.minus(b: T): T = add(this, -b)
    operator fun T.times(k: Number) = multiply(this, k.toDouble())
    operator fun T.div(k: Number) = multiply(this, 1.0 / k.toDouble())
    operator fun Number.times(b: T) = b * this

}

/**
 * The element of linear context
 * @param S self type of the element. Needed for static type checking
 */
interface SpaceElement<S : SpaceElement<S>> {
    /**
     * The context this element belongs to
     */
    val context: Space<S>

    /**
     * Self value. Needed for static type checking. Needed to avoid type erasure on JVM.
     */
    val self: S

    operator fun plus(b: S): S = with(context) { self + b }
    operator fun minus(b: S): S = with(context) { self - b }
    operator fun times(k: Number): S = with(context) { self * k }
    operator fun div(k: Number): S = with(context) { self / k }
}

/**
 * The same as {@link Space} but with additional multiplication operation
 */
interface Ring<T> : Space<T> {
    /**
     * neutral operation for multiplication
     */
    val one: T

    /**
     * Multiplication for two field elements
     */
    fun multiply(a: T, b: T): T

    operator fun T.times(b: T): T = multiply(this, b)

}

/**
 * Ring element
 */
interface RingElement<S : RingElement<S>> : SpaceElement<S> {
    override val context: Ring<S>

    operator fun times(b: S): S = with(context) { self * b }
}

/**
 * Four operations algebra
 */
interface Field<T> : Ring<T> {
    fun divide(a: T, b: T): T

    operator fun T.div(b: T): T = divide(this, b)
    operator fun Double.div(b: T) = this * divide(one, b)
}

/**
 * Field element
 */
interface FieldElement<S : FieldElement<S>> : RingElement<S> {
    override val context: Field<S>

    operator fun div(b: S): S = with(context) { self / b }
}