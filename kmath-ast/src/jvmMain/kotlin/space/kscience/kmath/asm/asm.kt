/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("UNUSED_PARAMETER")

package space.kscience.kmath.asm

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.asm.internal.*
import space.kscience.kmath.ast.*
import space.kscience.kmath.expressions.*
import space.kscience.kmath.operations.Algebra
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.Int32Ring
import space.kscience.kmath.operations.Int64Ring

/**
 * Compiles given MST to an Expression using AST compiler.
 *
 * @param type the target type.
 * @return the compiled expression.
 * @author Alexander Nozik
 */
@PublishedApi
internal fun <T : Any> MST.compileWith(
    type: Class<T>,
    algebra: Algebra<T>,
    functions: Map<String, Expression<T>> = emptyMap(),
    constants: Map<Symbol, T> = emptyMap(),
): Expression<T> {
    val typed = evaluateConstants(algebra, constants)
    if (typed is TypedMst.Constant<T>) return Expression(algebra.type) { typed.value }

    fun GenericAsmBuilder<T>.variablesVisitor(node: TypedMst<T>): Unit = when (node) {
        is TypedMst.Unary -> variablesVisitor(node.value)

        is TypedMst.Binary -> {
            variablesVisitor(node.left)
            variablesVisitor(node.right)
        }

        is TypedMst.Variable -> prepareVariable(node.symbol)
        is TypedMst.Constant -> Unit
        is TypedMst.FunctionCall -> node.arguments.values.forEach { variablesVisitor(it) }
    }

    fun GenericAsmBuilder<T>.expressionVisitor(node: TypedMst<T>): Unit = when (node) {
        is TypedMst.Constant -> if (node.number != null)
            loadNumberConstant(node.number)
        else
            loadObjectConstant(node.value)

        is TypedMst.Variable -> loadVariable(node.symbol)
        is TypedMst.Unary -> buildCall(node.function) { expressionVisitor(node.value) }

        is TypedMst.Binary -> buildCall(node.function) {
            expressionVisitor(node.left)
            expressionVisitor(node.right)
        }

        is TypedMst.FunctionCall -> {
            val function = resolveFunction(functions, node)

            val arguments = positionalArguments(function, node).map { argument ->
                { expressionVisitor(argument) }
            }

            // The array call unboxes arguments and boxes the result, so T must be exactly the primitive's box
            // (e.g. java.lang.Double for DoubleExpression); otherwise fall back to the map-based adapter.
            val arrayTypes = arrayExpressionTypes(function)?.takeIf { it.second.kotlin.javaObjectType == type }

            if (arrayTypes == null)
                buildFunctionCall(positionalAdapter(function, node), arguments)
            else
                buildArrayExpressionCall(function, arrayTypes.first, arrayTypes.second.asm, arguments)
        }
    }

    return GenericAsmBuilder(
        type,
        buildName("${typed.hashCode()}_${type.simpleName}"),
        { variablesVisitor(typed) },
        { expressionVisitor(typed) },
    ).instance
}

/**
 * Create a compiled expression with given [MST] and given [algebra]. [MST.FunctionCall] nodes are resolved against
 * [functions].
 */
public inline fun <reified T : Any> MST.compileToExpression(
    algebra: Algebra<T>,
    functions: Map<String, Expression<T>> = emptyMap(),
): Expression<T> = compileWith(T::class.java, algebra, functions)

/**
 * Compile given MST to expression and evaluate it against [arguments]
 */
public inline fun <reified T : Any> MST.compile(
    algebra: Algebra<T>,
    arguments: Map<Symbol, T>,
    functions: Map<String, Expression<T>> = emptyMap(),
): T = compileToExpression(algebra, functions)(arguments)

/**
 * Compile given MST to expression and evaluate it against [arguments]
 */
public inline fun <reified T : Any> MST.compile(algebra: Algebra<T>, vararg arguments: Pair<Symbol, T>): T =
    compileToExpression(algebra)(*arguments)

/**
 * Create a compiled expression with given [MST] using algebra, constants and functions of [MstInterpreterContext].
 * [MstInterpreterContext.arguments] are not used.
 */
context(mstContext: MstInterpreterContext<T>)
public inline fun <reified T : Any> MST.compileToExpression(): Expression<T> =
    compileWith(T::class.java, mstContext.algebra, mstContext.functions, mstContext.constants)

/**
 * Compile given MST to expression and evaluate it against [MstInterpreterContext.arguments].
 */
context(mstContext: MstInterpreterContext<T>)
public inline fun <reified T : Any> MST.compile(): T = compileToExpression()(mstContext.arguments)


/**
 * Create a compiled expression with given [MST] and given [algebra].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compileToExpression(
    algebra: Int32Ring,
    functions: Map<String, Expression<Int>> = emptyMap(),
): IntExpression {
    val typed = evaluateConstants(algebra)

    return if (typed is TypedMst.Constant) object : IntExpression {
        override val indexer = SimpleSymbolIndexer(emptyList())

        override fun invoke(arguments: IntArray): Int = typed.value
    } else
        IntAsmBuilder(typed, functions).instance
}

/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(
    algebra: Int32Ring,
    arguments: Map<Symbol, Int>,
    functions: Map<String, Expression<Int>> = emptyMap(),
): Int = compileToExpression(algebra, functions)(arguments)

/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: Int32Ring, vararg arguments: Pair<Symbol, Int>): Int =
    compileToExpression(algebra)(*arguments)


/**
 * Create a compiled expression with given [MST] and given [algebra].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compileToExpression(
    algebra: Int64Ring,
    functions: Map<String, Expression<Long>> = emptyMap(),
): LongExpression {
    val typed = evaluateConstants(algebra)

    return if (typed is TypedMst.Constant<Long>) object : LongExpression {
        override val indexer = SimpleSymbolIndexer(emptyList())

        override fun invoke(arguments: LongArray): Long = typed.value
    } else
        LongAsmBuilder(typed, functions).instance
}

/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(
    algebra: Int64Ring,
    arguments: Map<Symbol, Long>,
    functions: Map<String, Expression<Long>> = emptyMap(),
): Long = compileToExpression(algebra, functions)(arguments)


/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: Int64Ring, vararg arguments: Pair<Symbol, Long>): Long =
    compileToExpression(algebra)(*arguments)


/**
 * Create a compiled expression with given [MST] and given [algebra].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compileToExpression(
    algebra: Float64Field,
    functions: Map<String, Expression<Double>> = emptyMap(),
): DoubleExpression {
    val typed = evaluateConstants(algebra)

    return if (typed is TypedMst.Constant) object : DoubleExpression {
        override val indexer = SimpleSymbolIndexer(emptyList())

        override fun invoke(arguments: DoubleArray): Double = typed.value
    } else
        DoubleAsmBuilder(typed, functions).instance
}


/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(
    algebra: Float64Field,
    arguments: Map<Symbol, Double>,
    functions: Map<String, Expression<Double>> = emptyMap(),
): Double = compileToExpression(algebra, functions)(arguments)

/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: Float64Field, vararg arguments: Pair<Symbol, Double>): Double =
    compileToExpression(algebra)(*arguments)
