/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("UNUSED_PARAMETER")

package space.kscience.kmath.asm

import space.kscience.kmath.asm.internal.*
import space.kscience.kmath.expressions.*
import space.kscience.kmath.expressions.MST.*
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.*

/**
 * Compiles given MST to an Expression using AST compiler.
 *
 * @param type the target type.
 * @param algebra the target algebra.
 * @return the compiled expression.
 * @author Alexander Nozik
 */
@PublishedApi
internal fun <T : Any> MST.compileWith(type: Class<T>, algebra: Algebra<T>): Expression<T> {
    fun GenericAsmBuilder<T>.variablesVisitor(node: MST): Unit = when (node) {
        is Symbol -> prepareVariable(node.identity)
        is Unary -> variablesVisitor(node.value)

        is Binary -> {
            variablesVisitor(node.left)
            variablesVisitor(node.right)
        }

        else -> Unit
    }

    fun GenericAsmBuilder<T>.expressionVisitor(node: MST): Unit = when (node) {
        is Symbol -> {
            val symbol = algebra.bindSymbolOrNull(node)

            if (symbol != null)
                loadObjectConstant(symbol as Any)
            else
                loadVariable(node.identity)
        }

        is Numeric -> if (algebra is NumericAlgebra) {
            if (Number::class.java.isAssignableFrom(type))
                loadNumberConstant(algebra.number(node.value) as Number)
            else
                loadObjectConstant(algebra.number(node.value))
        } else
            error("Numeric nodes are not supported by $this")

        is Unary -> when {
            algebra is NumericAlgebra && node.value is Numeric -> loadObjectConstant(
                algebra.unaryOperationFunction(node.operation)(algebra.number((node.value as Numeric).value)),
            )

            else -> buildCall(algebra.unaryOperationFunction(node.operation)) { expressionVisitor(node.value) }
        }

        is Binary -> when {
            algebra is NumericAlgebra && node.left is Numeric && node.right is Numeric -> loadObjectConstant(
                algebra.binaryOperationFunction(node.operation).invoke(
                    algebra.number((node.left as Numeric).value),
                    algebra.number((node.right as Numeric).value),
                )
            )

            algebra is NumericAlgebra && node.left is Numeric -> buildCall(
                algebra.leftSideNumberOperationFunction(node.operation),
            ) {
                expressionVisitor(node.left)
                expressionVisitor(node.right)
            }

            algebra is NumericAlgebra && node.right is Numeric -> buildCall(
                algebra.rightSideNumberOperationFunction(node.operation),
            ) {
                expressionVisitor(node.left)
                expressionVisitor(node.right)
            }

            else -> buildCall(algebra.binaryOperationFunction(node.operation)) {
                expressionVisitor(node.left)
                expressionVisitor(node.right)
            }
        }
    }

    return GenericAsmBuilder<T>(
        type,
        buildName(this),
        { variablesVisitor(this@compileWith) },
        { expressionVisitor(this@compileWith) },
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
    compileToExpression(algebra).invoke(arguments)

/**
 * Compile given MST to expression and evaluate it against [arguments]
 */
public inline fun <reified T : Any> MST.compile(algebra: Algebra<T>, vararg arguments: Pair<Symbol, T>): T =
    compileToExpression(algebra).invoke(*arguments)


/**
 * Create a compiled expression with given [MST] and given [algebra].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compileToExpression(algebra: IntRing): IntExpression = IntAsmBuilder(this).instance

/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: IntRing, arguments: Map<Symbol, Int>): Int =
    compileToExpression(algebra).invoke(arguments)

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
public fun MST.compileToExpression(algebra: LongRing): LongExpression = LongAsmBuilder(this).instance


/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: LongRing, arguments: Map<Symbol, Long>): Long =
    compileToExpression(algebra).invoke(arguments)


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
public fun MST.compileToExpression(algebra: DoubleField): DoubleExpression = DoubleAsmBuilder(this).instance

/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: DoubleField, arguments: Map<Symbol, Double>): Double =
    compileToExpression(algebra).invoke(arguments)

/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: DoubleField, vararg arguments: Pair<Symbol, Double>): Double =
    compileToExpression(algebra).invoke(*arguments)
