package scientifik.kmath.expressions

import scientifik.kmath.operations.Algebra

/**
 * An elementary function that could be invoked on a map of arguments
 */
interface Expression<T> {
    operator fun invoke(arguments: Map<String, T>): T
}

operator fun <T> Expression<T>.invoke(vararg pairs: Pair<String, T>): T = invoke(mapOf(*pairs))

/**
 * A context for expression construction
 */
interface ExpressionContext<T, E> : Algebra<E> {
    /**
     * Introduce a variable into expression context
     */
    fun variable(name: String, default: T? = null): E

    /**
     * A constant expression which does not depend on arguments
     */
    fun const(value: T): E
}