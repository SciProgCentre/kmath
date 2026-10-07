/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.Algebra
import space.kscience.kmath.operations.NumericAlgebra
import space.kscience.kmath.operations.bindSymbolOrNull

/**
 * Evaluates constants in given [MST] for given [algebra] at the same time with converting to [TypedMst].
 *
 * Symbols found in [constants] are substituted before binding them with [algebra].
 */
public fun <T> MST.evaluateConstants(
    algebra: Algebra<T>,
    constants: Map<Symbol, T> = emptyMap(),
): TypedMst<T> = when (this) {
    is MST.Numeric -> TypedMst.Constant(
        algebra.type,
        (algebra as? NumericAlgebra<T>)?.number(value) ?: error("Numeric nodes are not supported by $algebra"),
        value,
    )

    is MST.Unary -> when (val arg = value.evaluateConstants(algebra, constants)) {
        is TypedMst.Constant<T> -> {
            val value = algebra.unaryOperation(
                operation,
                arg.value,
            )

            TypedMst.Constant(algebra.type, value, value as? Number)
        }

        else -> TypedMst.Unary(operation, algebra.unaryOperationFunction(operation), arg)
    }

    is MST.Binary -> {
        val left = left.evaluateConstants(algebra, constants)
        val right = right.evaluateConstants(algebra, constants)

        when {
            left is TypedMst.Constant<T> && right is TypedMst.Constant<T> -> {
                val value = when (algebra) {
                    is NumericAlgebra if left.number != null -> algebra.leftSideNumberOperation(
                        operation,
                        left.number,
                        right.value,
                    )

                    is NumericAlgebra if right.number != null -> algebra.rightSideNumberOperation(
                        operation,
                        left.value,
                        right.number,
                    )

                    else -> algebra.binaryOperation(
                        operation,
                        left.value,
                        right.value,
                    )
                }

                TypedMst.Constant(algebra.type, value, if (value is Number) value else null)
            }

            algebra is NumericAlgebra && left is TypedMst.Constant && left.number != null -> TypedMst.Binary(
                operation,
                algebra.leftSideNumberOperationFunction(operation),
                left,
                right,
            )

            algebra is NumericAlgebra && right is TypedMst.Constant && right.number != null -> TypedMst.Binary(
                operation,
                algebra.rightSideNumberOperationFunction(operation),
                left,
                right,
            )

            else -> TypedMst.Binary(operation, algebra.binaryOperationFunction(operation), left, right)
        }
    }

    is Symbol -> {
        val boundSymbol = constants[this] ?: algebra.bindSymbolOrNull(this)

        if (boundSymbol != null)
            TypedMst.Constant(algebra.type, boundSymbol, if (boundSymbol is Number) boundSymbol else null)
        else
            TypedMst.Variable(algebra.type, this)
    }

    is MST.FunctionCall -> TypedMst.FunctionCall(
        name,
        arguments.mapValues { it.value.evaluateConstants(algebra, constants) },
    )
}
