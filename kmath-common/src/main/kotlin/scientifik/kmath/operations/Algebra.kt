package scientifik.kmath.operations


/**
 * The generic mathematics elements which is able to store its context
 * @param T the self type of the element
 * @param S the type of mathematical context for this element
 */
interface MathElement<T, S> {
    /**
     * The context this element belongs to
     */
    val context: S
}

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
 * @param T  self type of the element. Needed for static type checking
 * @param S the type of space
 */
interface SpaceElement<T, S : Space<T>> : MathElement<T, S> {

    /**
     * Self value. Needed for static type checking. Needed to avoid type erasure on JVM.
     */
    val self: T

    operator fun plus(b: T): T = context.add(self, b)
    operator fun minus(b: T): T = context.add(self, context.multiply(b, -1.0))
    operator fun times(k: Number): T = context.multiply(self, k.toDouble())
    operator fun div(k: Number): T = context.multiply(self, 1.0 / k.toDouble())
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
interface RingElement<T, S : Ring<T>> : SpaceElement<T, S> {
    override val context: S

    operator fun times(b: T): T = context.multiply(self, b)
}

/**
 * Four operations algebra
 */
interface Field<T> : Ring<T> {
    fun divide(a: T, b: T): T

    operator fun T.div(b: T): T = divide(this, b)
    operator fun Number.div(b: T) = this * divide(one, b)

    operator fun T.plus(b: Number) = this.plus(b * one)
    operator fun Number.plus(b: T) = b + this

    operator fun T.minus(b: Number) = this.minus(b * one)
    operator fun Number.minus(b: T) = -b + this
}

/**
 * Field element
 */
interface FieldElement<T, S : Field<T>> : RingElement<T, S> {
    override val context: S

    operator fun div(b: T): T = context.divide(self, b)
}