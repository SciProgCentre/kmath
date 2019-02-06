package scientifik.kmath.operations


/**
 * A general interface representing linear context of some kind.
 * The context defines sum operation for its elements and multiplication by real value.
 * One must note that in some cases context is a singleton class, but in some cases it
 * works as a context for operations inside it.
 *
 * TODO do we need non-commutative context?
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
    fun multiply(a: T, k: Number): T

    //Operation to be performed in this context
    operator fun T.unaryMinus(): T = multiply(this, -1.0)

    operator fun T.plus(b: T): T = add(this, b)
    operator fun T.minus(b: T): T = add(this, -b)
    operator fun T.times(k: Number) = multiply(this, k.toDouble())
    operator fun T.div(k: Number) = multiply(this, 1.0 / k.toDouble())
    operator fun Number.times(b: T) = b * this
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

//    operator fun T.plus(b: Number) = this.plus(b * one)
//    operator fun Number.plus(b: T) = b + this
//
//    operator fun T.minus(b: Number) = this.minus(b * one)
//    operator fun Number.minus(b: T) = -b + this
}

/**
 * Four operations algebra
 */
interface Field<T> : Ring<T> {
    fun divide(a: T, b: T): T

    operator fun T.div(b: T): T = divide(this, b)
    operator fun Number.div(b: T) = this * divide(one, b)
}
