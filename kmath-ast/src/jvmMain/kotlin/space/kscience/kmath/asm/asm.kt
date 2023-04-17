/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("UNUSED_PARAMETER")

package space.kscience.kmath.asm

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.asm.internal.*
import space.kscience.kmath.ast.TypedMst
import space.kscience.kmath.ast.evaluateConstants
import space.kscience.kmath.expressions.*
import space.kscience.kmath.operations.Algebra
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.LongRing

/**
 * Compiles given MST to an Expression using AST compiler.
 *
 * @param type the target type.
 * @param algebra the target algebra.
 * @return the compiled expression.
 * @author Alexander Nozik
 */
@OptIn(UnstableKMathAPI::class)
@PublishedApi
internal fun <T : Any> MST.compileWith(type: Class<T>, algebra: Algebra<T>): Expression<T> {
    val typed = evaluateConstants(algebra)
    if (typed is TypedMst.Constant<T>) return Expression { typed.value }

    fun GenericAsmBuilder<T>.variablesVisitor(node: TypedMst<T>): Unit = when (node) {
        is TypedMst.Unary -> variablesVisitor(node.value)

        is TypedMst.Binary -> {
            variablesVisitor(node.left)
            variablesVisitor(node.right)
        }

        is TypedMst.Variable -> prepareVariable(node.symbol)
        is TypedMst.Constant -> Unit
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
    }

    return GenericAsmBuilder<T>(
        type,
        buildName("${typed.hashCode()}_${type.simpleName}"),
        { variablesVisitor(typed) },
        { expressionVisitor(typed) },
    ).instance
}

/**
 * Create a compiled expression with given [MST] and given [algebra].
 */
public inline fun <reified T : Any> MST.compileToExpression(algebra: Algebra<T>): Expression<T> =
    compileWith(T::class.java, algebra)

/**
 * Compile given MST to expression and evaluate it against [arguments]
 */
public inline fun <reified T : Any> MST.compile(algebra: Algebra<T>, arguments: Map<Symbol, T>): T =
    compileToExpression(algebra)(arguments)

/**
 * Compile given MST to expression and evaluate it against [arguments]
 */
public inline fun <reified T : Any> MST.compile(algebra: Algebra<T>, vararg arguments: Pair<Symbol, T>): T =
    compileToExpression(algebra)(*arguments)


/**
 * Create a compiled expression with given [MST] and given [algebra].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compileToExpression(algebra: IntRing): IntExpression  {
    val typed = evaluateConstants(algebra)

    return if (typed is TypedMst.Constant) object : IntExpression {
        override val indexer = SimpleSymbolIndexer(emptyList())

        override fun invoke(arguments: IntArray): Int = typed.value
    } else
        IntAsmBuilder(typed).instance
}

/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: IntRing, arguments: Map<Symbol, Int>): Int =
    compileToExpression(algebra)(arguments)

/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: IntRing, vararg arguments: Pair<Symbol, Int>): Int =
    compileToExpression(algebra)(*arguments)


/**
 * Create a compiled expression with given [MST] and given [algebra].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compileToExpression(algebra: LongRing): LongExpression {
    val typed = evaluateConstants(algebra)

    return if (typed is TypedMst.Constant<Long>) object : LongExpression {
        override val indexer = SimpleSymbolIndexer(emptyList())

        override fun invoke(arguments: LongArray): Long = typed.value
    } else
        LongAsmBuilder(typed).instance
}

/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: LongRing, arguments: Map<Symbol, Long>): Long =
    compileToExpression(algebra)(arguments)


/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: LongRing, vararg arguments: Pair<Symbol, Long>): Long =
    compileToExpression(algebra)(*arguments)


/**
 * Create a compiled expression with given [MST] and given [algebra].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compileToExpression(algebra: DoubleField): DoubleExpression {
    val typed = evaluateConstants(algebra)

    return if (typed is TypedMst.Constant) object : DoubleExpression {
        override val indexer = SimpleSymbolIndexer(emptyList())

        override fun invoke(arguments: DoubleArray): Double = typed.value
    } else
        DoubleAsmBuilder(typed).instance
}


/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: DoubleField, arguments: Map<Symbol, Double>): Double =
    compileToExpression(algebra)(arguments)

/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: DoubleField, vararg arguments: Pair<Symbol, Double>): Double =
    compileToExpression(algebra)(*arguments)
