package kscience.kmath.expressions

import kscience.kmath.operations.Algebra
import kotlin.jvm.JvmName
import kotlin.properties.ReadOnlyProperty

/**
 * A marker interface for a symbol. A symbol mus have an identity
 */
public interface Symbol {
    /**
     * Identity object for the symbol. Two symbols with the same identity are considered to be the same symbol.
     * By default uses object identity
     */
    public val identity: Any get() = this
}

/**
 * A [Symbol] with a [String] identity
 */
public inline class StringSymbol(override val identity: String) : Symbol {
    override fun toString(): String = identity
}

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
    public operator fun invoke(arguments: Map<Symbol, T>): T

    public companion object
}

/**
 * Invlode an expression without parameters
 */
public operator fun <T> Expression<T>.invoke(): T = invoke(emptyMap())
//This method exists to avoid resolution ambiguity of vararg methods

/**
 * Calls this expression from arguments.
 *
 * @param pairs the pair of arguments' names to values.
 * @return the value.
 */
@JvmName("callBySymbol")
public operator fun <T> Expression<T>.invoke(vararg pairs: Pair<Symbol, T>): T = invoke(mapOf(*pairs))

@JvmName("callByString")
public operator fun <T> Expression<T>.invoke(vararg pairs: Pair<String, T>): T =
    invoke(mapOf(*pairs).mapKeys { StringSymbol(it.key) })

/**
 * And object that could be differentiated
 */
public interface Differentiable<T> {
    public fun derivative(orders: Map<Symbol, Int>): T
}

public interface DifferentiableExpression<T> : Differentiable<Expression<T>>, Expression<T>

public fun <T> DifferentiableExpression<T>.derivative(vararg orders: Pair<Symbol, Int>): Expression<T> =
    derivative(mapOf(*orders))

public fun <T> DifferentiableExpression<T>.derivative(symbol: Symbol): Expression<T> = derivative(symbol to 1)

public fun <T> DifferentiableExpression<T>.derivative(name: String): Expression<T> = derivative(StringSymbol(name) to 1)

/**
 * A context for expression construction
 *
 * @param T type of the constants for the expression
 * @param E type of the actual expression state
 */
public interface ExpressionAlgebra<in T, E> : Algebra<E> {

    /**
     * Bind a given [Symbol] to this context variable and produce context-specific object. Return null if symbol could not be bound in current context.
     */
    public fun bindOrNull(symbol: Symbol): E?

    /**
     * Bind a string to a context using [StringSymbol]
     */
    override fun symbol(value: String): E = bind(StringSymbol(value))

    /**
     * A constant expression which does not depend on arguments
     */
    public fun const(value: T): E
}

/**
 * Bind a given [Symbol] to this context variable and produce context-specific object.
 */
public fun <T, E> ExpressionAlgebra<T, E>.bind(symbol: Symbol): E =
    bindOrNull(symbol) ?: error("Symbol $symbol could not be bound to $this")

public val symbol: ReadOnlyProperty<Any?, Symbol> = ReadOnlyProperty { _, property ->
    StringSymbol(property.name)
}

public fun <T, E> ExpressionAlgebra<T, E>.binding(): ReadOnlyProperty<Any?, E> =
    ReadOnlyProperty { _, property ->
        bind(StringSymbol(property.name)) ?: error("A variable with name ${property.name} does not exist")
    }