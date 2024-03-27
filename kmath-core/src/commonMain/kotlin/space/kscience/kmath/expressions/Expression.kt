/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions

import space.kscience.attributes.SafeType
import space.kscience.attributes.WithType
import space.kscience.attributes.safeTypeOf
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.Algebra
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.LongRing
import kotlin.jvm.JvmName
import kotlin.properties.ReadOnlyProperty

/**
 * An elementary function that could be invoked on a map of arguments.
 *
 * @param T the type this expression takes as argument and returns.
 */
public interface Expression<T> : WithType<T> {
    /**
     * Calls this expression from arguments.
     *
     * @param arguments the map of arguments.
     * @return the value.
     */
    public operator fun invoke(arguments: Map<Symbol, T>): T
}

/**
 * Create an expression from a functional block.
 */
public fun <T> Expression(type: SafeType<T>, block: (Map<Symbol, T>) -> T): Expression<T> = object : Expression<T> {
    override fun invoke(arguments: Map<Symbol, T>): T = block(arguments)

    override val type: SafeType<T> = type
}

public inline fun <reified T> Expression(noinline block: (Map<Symbol, T>) -> T): Expression<T> =
    Expression(safeTypeOf<T>(), block)

/**
 * Specialization of [Expression] for [Double] allowing better performance because of using array.
 */
@UnstableKMathAPI
public interface DoubleExpression : Expression<Double> {

    override val type: SafeType<Double> get() = DoubleField.type

    /**
     * The indexer of this expression's arguments that should be used to build array for [invoke].
     *
     * Implementations must fulfil the following requirement: for any argument symbol `x` and its value `y`,
     * `indexer.indexOf(x) == arguments.indexOf(y)` if `arguments` is the array passed to [invoke].
     */
    public val indexer: SymbolIndexer

    public override operator fun invoke(arguments: Map<Symbol, Double>): Double =
        this(DoubleArray(indexer.symbols.size) { arguments.getValue(indexer.symbols[it]) })

    /**
     * Calls this expression from arguments.
     *
     * @param arguments the array of arguments.
     * @return the value.
     */
    public operator fun invoke(arguments: DoubleArray): Double

    public companion object {
        internal val EMPTY_DOUBLE_ARRAY = DoubleArray(0)
    }
}

/**
 * Specialization of [Expression] for [Int] allowing better performance because of using array.
 */
@UnstableKMathAPI
public interface IntExpression : Expression<Int> {

    override val type: SafeType<Int> get() = IntRing.type

    /**
     * The indexer of this expression's arguments that should be used to build array for [invoke].
     *
     * Implementations must fulfil the following requirement: for any argument symbol `x` and its value `y`,
     * `indexer.indexOf(x) == arguments.indexOf(y)` if `arguments` is the array passed to [invoke].
     */
    public val indexer: SymbolIndexer

    public override operator fun invoke(arguments: Map<Symbol, Int>): Int =
        this(IntArray(indexer.symbols.size) { arguments.getValue(indexer.symbols[it]) })

    /**
     * Calls this expression from arguments.
     *
     * @param arguments the array of arguments.
     * @return the value.
     */
    public operator fun invoke(arguments: IntArray): Int

    public companion object {
        internal val EMPTY_INT_ARRAY = IntArray(0)
    }
}

/**
 * Specialization of [Expression] for [Long] allowing better performance because of using array.
 */
@UnstableKMathAPI
public interface LongExpression : Expression<Long> {

    override val type: SafeType<Long> get() = LongRing.type

    /**
     * The indexer of this expression's arguments that should be used to build array for [invoke].
     *
     * Implementations must fulfil the following requirement: for any argument symbol `x` and its value `y`,
     * `indexer.indexOf(x) == arguments.indexOf(y)` if `arguments` is the array passed to [invoke].
     */
    public val indexer: SymbolIndexer

    public override operator fun invoke(arguments: Map<Symbol, Long>): Long =
        this(LongArray(indexer.symbols.size) { arguments.getValue(indexer.symbols[it]) })

    /**
     * Calls this expression from arguments.
     *
     * @param arguments the array of arguments.
     * @return the value.
     */
    public operator fun invoke(arguments: LongArray): Long

    public companion object {
        internal val EMPTY_LONG_ARRAY = LongArray(0)
    }
}

/**
 * Calls this expression without providing any arguments.
 *
 * @return a value.
 */
public operator fun <T> Expression<T>.invoke(): T = this(emptyMap())

/**
 * Calls this expression from arguments.
 *
 * @param pairs the pairs of arguments to values.
 * @return a value.
 */
@JvmName("callBySymbol")
public operator fun <T> Expression<T>.invoke(vararg pairs: Pair<Symbol, T>): T = this(
    when (pairs.size) {
        0 -> emptyMap()
        1 -> mapOf(pairs[0])
        else -> hashMapOf(*pairs)
    }
)

/**
 * Calls this expression from arguments.
 *
 * @param pairs the pairs of arguments' names to value.
 * @return a value.
 */
@JvmName("callByString")
public operator fun <T> Expression<T>.invoke(vararg pairs: Pair<String, T>): T = this(
    when (pairs.size) {
        0 -> emptyMap()

        1 -> {
            val (k, v) = pairs[0]
            mapOf(StringSymbol(k) to v)
        }

        else -> hashMapOf(*Array<Pair<Symbol, T>>(pairs.size) {
            val (k, v) = pairs[it]
            StringSymbol(k) to v
        })
    }
)


/**
 * Calls this expression without providing any arguments.
 *
 * @return a value.
 */
@UnstableKMathAPI
public operator fun DoubleExpression.invoke(): Double = this(DoubleExpression.EMPTY_DOUBLE_ARRAY)

/**
 * Calls this expression from arguments.
 *
 * @param pairs the pairs of arguments to values.
 * @return a value.
 */
@UnstableKMathAPI
public operator fun DoubleExpression.invoke(vararg arguments: Double): Double = this(arguments)

/**
 * Calls this expression without providing any arguments.
 *
 * @return a value.
 */
@UnstableKMathAPI
public operator fun IntExpression.invoke(): Int = this(IntExpression.EMPTY_INT_ARRAY)

/**
 * Calls this expression from arguments.
 *
 * @param pairs the pairs of arguments to values.
 * @return a value.
 */
@UnstableKMathAPI
public operator fun IntExpression.invoke(vararg arguments: Int): Int = this(arguments)

/**
 * Calls this expression without providing any arguments.
 *
 * @return a value.
 */
@UnstableKMathAPI
public operator fun LongExpression.invoke(): Long = this(LongExpression.EMPTY_LONG_ARRAY)

/**
 * Calls this expression from arguments.
 *
 * @param pairs the pairs of arguments to values.
 * @return a value.
 */
@UnstableKMathAPI
public operator fun LongExpression.invoke(vararg arguments: Long): Long = this(arguments)

/**
 * A context for expression construction
 *
 * @param T type of the constants for the expression
 * @param E type of the actual expression state
 */
public interface ExpressionAlgebra<in T, E> : Algebra<E> {

    /**
     * A constant expression that does not depend on arguments.
     */
    public fun const(value: T): E
}

/**
 * Bind a symbol by name inside the [ExpressionAlgebra]
 */
public val <T, E> ExpressionAlgebra<T, E>.binding: ReadOnlyProperty<Any?, E>
    get() = ReadOnlyProperty { _, property ->
        bindSymbol(property.name) ?: error("A variable with name ${property.name} does not exist")
    }
