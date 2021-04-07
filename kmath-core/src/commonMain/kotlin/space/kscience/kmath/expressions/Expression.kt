package space.kscience.kmath.expressions

import space.kscience.kmath.misc.StringSymbol
import space.kscience.kmath.misc.Symbol
import space.kscience.kmath.operations.Algebra
import kotlin.jvm.JvmName
import kotlin.properties.ReadOnlyProperty

/**
 * An elementary function that could be invoked on a map of arguments.
 *
 * @param T the type this expression takes as argument and returns.
 */
public fun interface Expression<T> {
    /**
     * Calls this expression from arguments.
     *
     * @param arguments the map of arguments.
     * @return the value.
     */
    public operator fun invoke(arguments: Map<Symbol, T>): T
}

/**
 * Calls this expression without providing any arguments.
 *
 * @return a value.
 */
public operator fun <T> Expression<T>.invoke(): T = invoke(emptyMap())

/**
 * Calls this expression from arguments.
 *
 * @param pairs the pairs of arguments to values.
 * @return a value.
 */
@JvmName("callBySymbol")
public operator fun <T> Expression<T>.invoke(vararg pairs: Pair<Symbol, T>): T = invoke(mapOf(*pairs))

/**
 * Calls this expression from arguments.
 *
 * @param pairs the pairs of arguments' names to values.
 * @return a value.
 */
@JvmName("callByString")
public operator fun <T> Expression<T>.invoke(vararg pairs: Pair<String, T>): T =
    invoke(mapOf(*pairs).mapKeys { StringSymbol(it.key) })


/**
 * A context for expression construction
 *
 * @param T type of the constants for the expression
 * @param E type of the actual expression state
 */
public interface ExpressionAlgebra<in T, E> : Algebra<E> {

    /**
     * A constant expression which does not depend on arguments
     */
    public fun const(value: T): E
}

/**
 * Bind a symbol by name inside the [ExpressionAlgebra]
 */
public fun <T, E> ExpressionAlgebra<T, E>.binding(): ReadOnlyProperty<Any?, E> = ReadOnlyProperty { _, property ->
    bindSymbol(property.name) ?: error("A variable with name ${property.name} does not exist")
}
