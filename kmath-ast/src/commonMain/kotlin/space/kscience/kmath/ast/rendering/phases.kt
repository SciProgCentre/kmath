/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast.rendering

import space.kscience.kmath.ast.rendering.FeaturedMathRendererWithPostProcess.PostProcessPhase
import space.kscience.kmath.operations.FieldOps
import space.kscience.kmath.operations.GroupOps
import space.kscience.kmath.operations.PowerOperations
import space.kscience.kmath.operations.RingOps

/**
 * Removes unnecessary times (&times;) symbols from [MultiplicationSyntax].
 *
 * @author Iaroslav Postovalov
 */
public val BetterMultiplication: PostProcessPhase = PostProcessPhase { node ->
    fun perform(node: MathSyntax): Unit = when (node) {
        is NumberSyntax -> Unit
        is SymbolSyntax -> Unit
        is OperatorNameSyntax -> Unit
        is SpecialSymbolSyntax -> Unit
        is OperandSyntax -> perform(node.operand)

        is UnaryOperatorSyntax -> {
            perform(node.prefix)
            perform(node.operand)
        }

        is UnaryPlusSyntax -> perform(node.operand)
        is UnaryMinusSyntax -> perform(node.operand)
        is RadicalSyntax -> perform(node.operand)
        is ExponentSyntax -> perform(node.operand)

        is SuperscriptSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is SubscriptSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is BinaryOperatorSyntax -> {
            perform(node.prefix)
            perform(node.left)
            perform(node.right)
        }

        is BinaryPlusSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is BinaryMinusSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is FractionSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is RadicalWithIndexSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is MultiplicationSyntax -> {
            node.times = node.right.operand is NumberSyntax && !node.right.parentheses
                    || node.left.operand is NumberSyntax && node.right.operand is FractionSyntax
                    || node.left.operand is NumberSyntax && node.right.operand is NumberSyntax
                    || node.left.operand is NumberSyntax && node.right.operand is SuperscriptSyntax && node.right.operand.left is NumberSyntax

            perform(node.left)
            perform(node.right)
        }
    }

    perform(node)
}

/**
 * Chooses [FractionSyntax.infix] depending on the context.
 *
 * @author Iaroslav Postovalov
 */
public val BetterFraction: PostProcessPhase = PostProcessPhase { node ->
    fun perform(node: MathSyntax, infix: Boolean = false): Unit = when (node) {
        is NumberSyntax -> Unit
        is SymbolSyntax -> Unit
        is OperatorNameSyntax -> Unit
        is SpecialSymbolSyntax -> Unit
        is OperandSyntax -> perform(node.operand, infix)

        is UnaryOperatorSyntax -> {
            perform(node.prefix, infix)
            perform(node.operand, infix)
        }

        is UnaryPlusSyntax -> perform(node.operand, infix)
        is UnaryMinusSyntax -> perform(node.operand, infix)
        is RadicalSyntax -> perform(node.operand, infix)
        is ExponentSyntax -> perform(node.operand, infix)

        is SuperscriptSyntax -> {
            perform(node.left, true)
            perform(node.right, true)
        }

        is SubscriptSyntax -> {
            perform(node.left, true)
            perform(node.right, true)
        }

        is BinaryOperatorSyntax -> {
            perform(node.prefix, infix)
            perform(node.left, infix)
            perform(node.right, infix)
        }

        is BinaryPlusSyntax -> {
            perform(node.left, infix)
            perform(node.right, infix)
        }

        is BinaryMinusSyntax -> {
            perform(node.left, infix)
            perform(node.right, infix)
        }

        is FractionSyntax -> {
            node.infix = infix
            perform(node.left, infix)
            perform(node.right, infix)
        }

        is RadicalWithIndexSyntax -> {
            perform(node.left, true)
            perform(node.right, true)
        }

        is MultiplicationSyntax -> {
            perform(node.left, infix)
            perform(node.right, infix)
        }
    }

    perform(node)
}

/**
 * Applies [ExponentSyntax.useOperatorForm] to [ExponentSyntax] when the operand contains a fraction, a
 * superscript or a subscript to improve readability.
 *
 * @author Iaroslav Postovalov
 */
public val BetterExponent: PostProcessPhase = PostProcessPhase { node ->
    fun perform(node: MathSyntax): Boolean {
        return when (node) {
            is NumberSyntax -> false
            is SymbolSyntax -> false
            is OperatorNameSyntax -> false
            is SpecialSymbolSyntax -> false
            is OperandSyntax -> perform(node.operand)
            is UnaryOperatorSyntax -> perform(node.prefix) || perform(node.operand)
            is UnaryPlusSyntax -> perform(node.operand)
            is UnaryMinusSyntax -> perform(node.operand)
            is RadicalSyntax -> true

            is ExponentSyntax -> {
                val r = perform(node.operand)
                node.useOperatorForm = r
                r
            }

            is SuperscriptSyntax -> true
            is SubscriptSyntax -> true
            is BinaryOperatorSyntax -> perform(node.prefix) || perform(node.left) || perform(node.right)
            is BinaryPlusSyntax -> perform(node.left) || perform(node.right)
            is BinaryMinusSyntax -> perform(node.left) || perform(node.right)
            is FractionSyntax -> true
            is RadicalWithIndexSyntax -> true
            is MultiplicationSyntax -> perform(node.left) || perform(node.right)
        }
    }

    perform(node)
}

/**
 * Removes unnecessary parentheses from [OperandSyntax].
 *
 * @property precedenceFunction Returns the precedence number for syntax node. Higher number is lower priority.
 * @author Iaroslav Postovalov
 */
public class SimplifyParentheses(public val precedenceFunction: (MathSyntax) -> Int) :
    PostProcessPhase {
    override fun perform(node: MathSyntax): Unit = when (node) {
        is NumberSyntax -> Unit
        is SymbolSyntax -> Unit
        is OperatorNameSyntax -> Unit
        is SpecialSymbolSyntax -> Unit

        is OperandSyntax -> {
            val isRightOfSuperscript =
                (node.parent is SuperscriptSyntax) && (node.parent as SuperscriptSyntax).right === node

            val precedence = precedenceFunction(node.operand)

            val needParenthesesByPrecedence = when (val parent = node.parent) {
                null -> false

                is BinarySyntax -> {
                    val parentPrecedence = precedenceFunction(parent)

                    parentPrecedence < precedence ||
                            parentPrecedence == precedence && parentPrecedence != 0 && node === parent.right
                }

                else -> precedence > precedenceFunction(parent)
            }

            val isInsideExpOperator =
                node.parent is ExponentSyntax && (node.parent as ExponentSyntax).useOperatorForm

            val isOnOrUnderNormalFraction = node.parent is FractionSyntax && !((node.parent as FractionSyntax).infix)

            node.parentheses = !isRightOfSuperscript
                    && (needParenthesesByPrecedence || node.parent is UnaryOperatorSyntax || isInsideExpOperator)
                    && !isOnOrUnderNormalFraction

            perform(node.operand)
        }

        is UnaryOperatorSyntax -> {
            perform(node.prefix)
            perform(node.operand)
        }

        is UnaryPlusSyntax -> perform(node.operand)
        is UnaryMinusSyntax -> perform(node.operand)
        is RadicalSyntax -> perform(node.operand)
        is ExponentSyntax -> perform(node.operand)

        is SuperscriptSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is SubscriptSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is BinaryOperatorSyntax -> {
            perform(node.prefix)
            perform(node.left)
            perform(node.right)
        }

        is BinaryPlusSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is BinaryMinusSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is FractionSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is MultiplicationSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is RadicalWithIndexSyntax -> {
            perform(node.left)
            perform(node.right)
        }
    }

    public companion object {
        /**
         * The default configuration of [SimplifyParentheses] where power is 1, multiplicative operations are 2,
         * additive operations are 3.
         */
        public val Default: SimplifyParentheses = SimplifyParentheses {
            when (it) {
                is TerminalSyntax -> 0
                is UnarySyntax -> 2

                is BinarySyntax -> when (it.operation) {
                    PowerOperations.POW_OPERATION -> 1
                    RingOps.TIMES_OPERATION -> 3
                    FieldOps.DIV_OPERATION -> 3
                    GroupOps.MINUS_OPERATION -> 4
                    GroupOps.PLUS_OPERATION -> 4
                    else -> 0
                }

                else -> 0
            }
        }
    }
}
