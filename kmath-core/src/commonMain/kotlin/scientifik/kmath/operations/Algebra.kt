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
    fun Iterable<T>.sum(): T = fold(zero) { left, right -> left + right }

    fun Sequence<T>.sum(): T = fold(zero) { left, right -> left + right }
}

abstract class AbstractSpace<T> : Space<T> {
    //TODO move to external extensions when they are available
    final override operator fun T.unaryMinus(): T = multiply(this, -1.0)

    final override operator fun T.plus(b: T): T = add(this, b)
    final override operator fun T.minus(b: T): T = add(this, -b)
    final override operator fun T.times(k: Number) = multiply(this, k.toDouble())
    final override operator fun T.div(k: Number) = multiply(this, 1.0 / k.toDouble())
    final override operator fun Number.times(b: T) = b * this

    final override fun Iterable<T>.sum(): T = fold(zero) { left, right -> left + right }

    final override fun Sequence<T>.sum(): T = fold(zero) { left, right -> left + right }
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

    operator fun T.plus(b: Number) = this.plus(b * one)
    operator fun Number.plus(b: T) = b + this

    operator fun T.minus(b: Number) = this.minus(b * one)
    operator fun Number.minus(b: T) = -b + this
}

abstract class AbstractRing<T : Any> : AbstractSpace<T>(), Ring<T> {
    final override operator fun T.times(b: T): T = multiply(this, b)
}

/**
 * Four operations algebra
 */
interface Field<T> : Ring<T> {
    fun divide(a: T, b: T): T

    operator fun T.div(b: T): T = divide(this, b)
    operator fun Number.div(b: T) = this * divide(one, b)
}

abstract class AbstractField<T : Any> : AbstractRing<T>(), Field<T> {
    final override operator fun T.div(b: T): T = divide(this, b)
    final override operator fun Number.div(b: T) = this * divide(one, b)
}