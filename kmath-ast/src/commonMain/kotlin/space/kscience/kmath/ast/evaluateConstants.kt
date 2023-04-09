/*
 * Copyright 2018-2022 KMath contributors.
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
 */
public fun <T> MST.evaluateConstants(algebra: Algebra<T>): TypedMst<T> = when (this) {
    is MST.Numeric -> TypedMst.Constant(
        (algebra as? NumericAlgebra<T>)?.number(value) ?: error("Numeric nodes are not supported by $algebra"),
        value,
    )

    is MST.Unary -> when (val arg = value.evaluateConstants(algebra)) {
        is TypedMst.Constant<T> -> {
            val value = algebra.unaryOperation(
                operation,
                arg.value,
            )

            TypedMst.Constant(value, if (value is Number) value else null)
        }

        else -> TypedMst.Unary(operation, algebra.unaryOperationFunction(operation), arg)
    }

    is MST.Binary -> {
        val left = left.evaluateConstants(algebra)
        val right = right.evaluateConstants(algebra)

        when {
            left is TypedMst.Constant<T> && right is TypedMst.Constant<T> -> {
                val value = when {
                    algebra is NumericAlgebra && left.number != null -> algebra.leftSideNumberOperation(
                        operation,
                        left.number,
                        right.value,
                    )

                    algebra is NumericAlgebra && right.number != null -> algebra.rightSideNumberOperation(
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

                TypedMst.Constant(value, if (value is Number) value else null)
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
        val boundSymbol = algebra.bindSymbolOrNull(this)

        if (boundSymbol != null)
            TypedMst.Constant(boundSymbol, if (boundSymbol is Number) boundSymbol else null)
        else
            TypedMst.Variable(this)
    }
}
