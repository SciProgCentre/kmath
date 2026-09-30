/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.estree

import space.kscience.kmath.ast.*
import space.kscience.kmath.estree.internal.ESTreeBuilder
import space.kscience.kmath.expressions.*
import space.kscience.kmath.internal.estree.BaseExpression
import space.kscience.kmath.operations.Algebra

/**
 * Create a compiled expression with given [MST] and given [algebra]. [MST.FunctionCall] nodes are resolved against
 * [functions].
 */
public fun <T : Any> MST.compileToExpression(
    algebra: Algebra<T>,
    functions: Map<String, Expression<T>> = emptyMap(),
): Expression<T> = compileWith(algebra, functions, emptyMap())

private fun <T : Any> MST.compileWith(
    algebra: Algebra<T>,
    functions: Map<String, Expression<T>>,
    constants: Map<Symbol, T>,
): Expression<T> {
    val typed = evaluateConstants(algebra, constants)
    if (typed is TypedMst.Constant<T>) return Expression(algebra.type) { typed.value }

    fun ESTreeBuilder<T>.visit(node: TypedMst<T>): BaseExpression = when (node) {
        is TypedMst.Constant -> constant(node.value)
        is TypedMst.Variable -> variable(node.symbol)
        is TypedMst.Unary -> call(node.function, visit(node.value))

        is TypedMst.Binary -> call(
            node.function,
            visit(node.left),
            visit(node.right),
        )

        is TypedMst.FunctionCall -> {
            val function = resolveFunction(functions, node)
            call(positionalAdapter(function, node), positionalArguments(function, node).map { visit(it) })
        }
    }

    return ESTreeBuilder(algebra.type) { visit(typed) }.instance
}

/**
 * Compile given MST to expression and evaluate it against [arguments]
 */
public fun <T : Any> MST.compile(
    algebra: Algebra<T>,
    arguments: Map<Symbol, T>,
    functions: Map<String, Expression<T>> = emptyMap(),
): T = compileToExpression(algebra, functions)(arguments)

/**
 * Compile given MST to expression and evaluate it against [arguments]
 */
public fun <T : Any> MST.compile(algebra: Algebra<T>, vararg arguments: Pair<Symbol, T>): T =
    compileToExpression(algebra)(*arguments)

/**
 * Create a compiled expression with given [MST] using algebra, constants and functions of [MstInterpreterContext].
 * [MstInterpreterContext.arguments] are not used.
 */
context(mstContext: MstInterpreterContext<T>)
public fun <T : Any> MST.compileToExpression(): Expression<T> =
    compileWith(mstContext.algebra, mstContext.functions, mstContext.constants)

/**
 * Compile given MST to expression and evaluate it against [MstInterpreterContext.arguments].
 */
context(mstContext: MstInterpreterContext<T>)
public fun <T : Any> MST.compile(): T = compileToExpression()(mstContext.arguments)
