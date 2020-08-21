package scientifik.kmath.expressions

import scientifik.kmath.operations.Algebra

/**
 * An elementary function that could be invoked on a map of arguments
 */
interface Expression<T> {
    /**
     * Calls this expression from arguments.
     *
     * @param arguments the map of arguments.
     * @return the value.
     */
    operator fun invoke(arguments: Map<String, T>): T

    companion object
}

/**
 * Create simple lazily evaluated expression inside given algebra
 */
fun <T> Algebra<T>.expression(block: Algebra<T>.(arguments: Map<String, T>) -> T): Expression<T> =
    object : Expression<T> {
        override operator fun invoke(arguments: Map<String, T>): T = block(arguments)
    }

/**
 * Calls this expression from arguments.
 *
 * @param pairs the pair of arguments' names to values.
 * @return the value.
 */
operator fun <T> Expression<T>.invoke(vararg pairs: Pair<String, T>): T = invoke(mapOf(*pairs))

/**
 * A context for expression construction
 */
interface ExpressionAlgebra<T, E> : Algebra<E> {
    /**
     * Introduce a variable into expression context
     */
    fun variable(name: String, default: T? = null): E

    /**
     * A constant expression which does not depend on arguments
     */
    fun const(value: T): E
}
