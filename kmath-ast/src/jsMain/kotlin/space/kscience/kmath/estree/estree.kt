/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.estree

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.ast.TypedMst
import space.kscience.kmath.ast.evaluateConstants
import space.kscience.kmath.estree.internal.ESTreeBuilder
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.internal.estree.BaseExpression
import space.kscience.kmath.operations.Algebra

/**
 * Create a compiled expression with given [MST] and given [algebra].
 */
@OptIn(UnstableKMathAPI::class)
public fun <T : Any> MST.compileToExpression(algebra: Algebra<T>): Expression<T> {
    val typed = evaluateConstants(algebra)
    if (typed is TypedMst.Constant<T>) return Expression { typed.value }

    fun ESTreeBuilder<T>.visit(node: TypedMst<T>): BaseExpression = when (node) {
        is TypedMst.Constant -> constant(node.value)
        is TypedMst.Variable -> variable(node.symbol)
        is TypedMst.Unary -> call(node.function, visit(node.value))

        is TypedMst.Binary -> call(
            node.function,
            visit(node.left),
            visit(node.right),
        )
    }

    return ESTreeBuilder { visit(typed) }.instance
}

/**
 * Compile given MST to expression and evaluate it against [arguments]
 */
public fun <T : Any> MST.compile(algebra: Algebra<T>, arguments: Map<Symbol, T>): T =
    compileToExpression(algebra)(arguments)

/**
 * Compile given MST to expression and evaluate it against [arguments]
 */
public fun <T : Any> MST.compile(algebra: Algebra<T>, vararg arguments: Pair<Symbol, T>): T =
    compileToExpression(algebra)(*arguments)
