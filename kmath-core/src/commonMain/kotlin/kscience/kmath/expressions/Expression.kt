package kscience.kmath.expressions

import kscience.kmath.operations.Algebra

/**
 * An elementary function that could be invoked on a map of arguments
 */
public fun interface Expression<T> {
    /**
     * Calls this expression from arguments.
     *
     * @param arguments the map of arguments.
     * @return the value.
     */
    public operator fun invoke(arguments: Map<String, T>): T

    public companion object
}

/**
 * Calls this expression from arguments.
 *
 * @param pairs the pair of arguments' names to values.
 * @return the value.
 */
public operator fun <T> Expression<T>.invoke(vararg pairs: Pair<String, T>): T = invoke(mapOf(*pairs))

/**
 * A context for expression construction
 */
public interface ExpressionAlgebra<T, E> : Algebra<E> {
    /**
     * Introduce a variable into expression context
     */
    public fun variable(name: String, default: T? = null): E

    /**
     * A constant expression which does not depend on arguments
     */
    public fun const(value: T): E
}
