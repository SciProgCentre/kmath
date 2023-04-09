/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast.rendering

import space.kscience.kmath.ast.rendering.FeaturedMathRenderer.RenderFeature
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.*
import kotlin.reflect.KClass

/**
 * Prints any [Symbol] as a [SymbolSyntax] containing the [Symbol.identity] of it.
 *
 * @author Iaroslav Postovalov
 */
public val PrintSymbol: RenderFeature = RenderFeature { _, node ->
    if (node !is Symbol) null
    else SymbolSyntax(string = node.identity)
}

/**
 * Prints any [MST.Numeric] as a [NumberSyntax] containing the [Any.toString] result of it.
 *
 * @author Iaroslav Postovalov
 */
public val PrintNumeric: RenderFeature = RenderFeature { _, node ->
    if (node !is MST.Numeric)
        null
    else
        NumberSyntax(string = node.value.toString())
}

private fun printSignedNumberString(s: String): MathSyntax = if (s.startsWith('-'))
    UnaryMinusSyntax(
        operation = GroupOps.MINUS_OPERATION,
        operand = OperandSyntax(
            operand = NumberSyntax(string = s.removePrefix("-")),
            parentheses = true,
        ),
    )
else
    NumberSyntax(string = s)

/**
 * Special printing for numeric types that are printed in form of
 * *('-'? (DIGIT+ ('.' DIGIT+)? ('E' '-'? DIGIT+)? | 'Infinity')) | 'NaN'*.
 *
 * @property types The suitable types.
 * @author Iaroslav Postovalov
 */
public class PrettyPrintFloats(public val types: Set<KClass<out Number>>) : RenderFeature {
    override fun render(renderer: FeaturedMathRenderer, node: MST): MathSyntax? {
        if (node !is MST.Numeric || node.value::class !in types) return null

        val toString = when (val v = node.value) {
            is Float -> v.multiplatformToString()
            is Double -> v.multiplatformToString()
            else -> v.toString()
        }.removeSuffix(".0")

        if (toString.contains('E', ignoreCase = true)) {
            val (beforeE, afterE) = toString.split('E', ignoreCase = true)
            val significand = beforeE.toDouble().toString().removeSuffix(".0")
            val exponent = afterE.toDouble().toString().removeSuffix(".0")

            return MultiplicationSyntax(
                operation = RingOps.TIMES_OPERATION,
                left = OperandSyntax(operand = NumberSyntax(significand), parentheses = true),
                right = OperandSyntax(
                    operand = SuperscriptSyntax(
                        operation = PowerOperations.POW_OPERATION,
                        left = NumberSyntax(string = "10"),
                        right = printSignedNumberString(exponent),
                    ),
                    parentheses = true,
                ),
                times = true,
            )
        }

        if (toString.endsWith("Infinity")) {
            val infty = SpecialSymbolSyntax(SpecialSymbolSyntax.Kind.INFINITY)

            if (toString.startsWith('-'))
                return UnaryMinusSyntax(
                    operation = GroupOps.MINUS_OPERATION,
                    operand = OperandSyntax(operand = infty, parentheses = true),
                )

            return infty
        }

        return printSignedNumberString(toString)
    }

    public companion object {
        /**
         * The default instance containing [Float], and [Double].
         */
        public val Default: PrettyPrintFloats = PrettyPrintFloats(setOf(Float::class, Double::class))
    }
}

/**
 * Special printing for numeric types that are printed in form of *'-'? DIGIT+*.
 *
 * @property types The suitable types.
 * @author Iaroslav Postovalov
 */
public class PrettyPrintIntegers(public val types: Set<KClass<out Number>>) : RenderFeature {
    override fun render(renderer: FeaturedMathRenderer, node: MST): MathSyntax? =
        if (node !is MST.Numeric || node.value::class !in types)
            null
        else
            printSignedNumberString(node.value.toString())

    public companion object {
        /**
         * The default instance containing [Byte], [Short], [Int], and [Long].
         */
        public val Default: PrettyPrintIntegers =
            PrettyPrintIntegers(setOf(Byte::class, Short::class, Int::class, Long::class))
    }
}

/**
 * Special printing for symbols meaning Pi.
 *
 * @property symbols The allowed symbols.
 * @author Iaroslav Postovalov
 */
public class PrettyPrintPi(public val symbols: Set<String>) : RenderFeature {
    override fun render(renderer: FeaturedMathRenderer, node: MST): MathSyntax? =
        if (node !is Symbol || node.identity !in symbols)
            null
        else
            SpecialSymbolSyntax(kind = SpecialSymbolSyntax.Kind.SMALL_PI)

    public companion object {
        /**
         * The default instance containing `pi`.
         */
        public val Default: PrettyPrintPi = PrettyPrintPi(setOf("pi"))
    }
}

/**
 * Abstract printing of unary operations that discards [MST] if their operation is not in [operations] or its type is
 * not [MST.Unary].
 *
 * @param operations the allowed operations. If `null`, any operation is accepted.
 * @author Iaroslav Postovalov
 */
public abstract class Unary(public val operations: Collection<String>?) : RenderFeature {
    /**
     * The actual render function specialized for [MST.Unary].
     */
    protected abstract fun renderUnary(parent: FeaturedMathRenderer, node: MST.Unary): MathSyntax?

    public final override fun render(renderer: FeaturedMathRenderer, node: MST): MathSyntax? =
        if (node !is MST.Unary || operations != null && node.operation !in operations)
            null
        else
            renderUnary(renderer, node)
}

/**
 * Abstract printing of unary operations that discards [MST] if their operation is not in [operations] or its type is
 * not [MST.Binary].
 *
 * @property operations the allowed operations. If `null`, any operation is accepted.
 * @author Iaroslav Postovalov
 */
public abstract class Binary(public val operations: Collection<String>?) : RenderFeature {
    /**
     * The actual render function specialized for [MST.Binary].
     */
    protected abstract fun renderBinary(parent: FeaturedMathRenderer, node: MST.Binary): MathSyntax?

    public final override fun render(renderer: FeaturedMathRenderer, node: MST): MathSyntax? {
        if (node !is MST.Binary || operations != null && node.operation !in operations) return null
        return renderBinary(renderer, node)
    }
}

/**
 * Handles binary nodes by producing [BinaryPlusSyntax].
 *
 * @author Iaroslav Postovalov
 */
public class BinaryPlus(operations: Collection<String>?) : Binary(operations) {
    override fun renderBinary(parent: FeaturedMathRenderer, node: MST.Binary): MathSyntax =
        BinaryPlusSyntax(
            operation = node.operation,
            left = OperandSyntax(parent.render(node.left), true),
            right = OperandSyntax(parent.render(node.right), true),
        )

    public companion object {
        /**
         * The default instance configured with [GroupOps.PLUS_OPERATION].
         */
        public val Default: BinaryPlus = BinaryPlus(setOf(GroupOps.PLUS_OPERATION))
    }
}

/**
 * Handles binary nodes by producing [BinaryMinusSyntax].
 *
 * @author Iaroslav Postovalov
 */
public class BinaryMinus(operations: Collection<String>?) : Binary(operations) {
    override fun renderBinary(parent: FeaturedMathRenderer, node: MST.Binary): MathSyntax =
        BinaryMinusSyntax(
            operation = node.operation,
            left = OperandSyntax(operand = parent.render(node.left), parentheses = true),
            right = OperandSyntax(operand = parent.render(node.right), parentheses = true),
        )

    public companion object {
        /**
         * The default instance configured with [GroupOps.MINUS_OPERATION].
         */
        public val Default: BinaryMinus = BinaryMinus(setOf(GroupOps.MINUS_OPERATION))
    }
}

/**
 * Handles unary nodes by producing [UnaryPlusSyntax].
 *
 * @author Iaroslav Postovalov
 */
public class UnaryPlus(operations: Collection<String>?) : Unary(operations) {
    override fun renderUnary(parent: FeaturedMathRenderer, node: MST.Unary): MathSyntax = UnaryPlusSyntax(
        operation = node.operation,
        operand = OperandSyntax(operand = parent.render(node.value), parentheses = true),
    )

    public companion object {
        /**
         * The default instance configured with [GroupOps.PLUS_OPERATION].
         */
        public val Default: UnaryPlus = UnaryPlus(setOf(GroupOps.PLUS_OPERATION))
    }
}

/**
 * Handles binary nodes by producing [UnaryMinusSyntax].
 *
 * @author Iaroslav Postovalov
 */
public class UnaryMinus(operations: Collection<String>?) : Unary(operations) {
    override fun renderUnary(parent: FeaturedMathRenderer, node: MST.Unary): MathSyntax = UnaryMinusSyntax(
        operation = node.operation,
        operand = OperandSyntax(operand = parent.render(node.value), parentheses = true),
    )

    public companion object {
        /**
         * The default instance configured with [GroupOps.MINUS_OPERATION].
         */
        public val Default: UnaryMinus = UnaryMinus(setOf(GroupOps.MINUS_OPERATION))
    }
}

/**
 * Handles binary nodes by producing [FractionSyntax].
 *
 * @author Iaroslav Postovalov
 */
public class Fraction(operations: Collection<String>?) : Binary(operations) {
    override fun renderBinary(parent: FeaturedMathRenderer, node: MST.Binary): MathSyntax = FractionSyntax(
        operation = node.operation,
        left = OperandSyntax(operand = parent.render(node.left), parentheses = true),
        right = OperandSyntax(operand = parent.render(node.right), parentheses = true),
        infix = true,
    )

    public companion object {
        /**
         * The default instance configured with [FieldOps.DIV_OPERATION].
         */
        public val Default: Fraction = Fraction(setOf(FieldOps.DIV_OPERATION))
    }
}

/**
 * Handles binary nodes by producing [BinaryOperatorSyntax].
 *
 * @author Iaroslav Postovalov
 */
public class BinaryOperator(operations: Collection<String>?) : Binary(operations) {
    override fun renderBinary(parent: FeaturedMathRenderer, node: MST.Binary): MathSyntax =
        BinaryOperatorSyntax(
            operation = node.operation,
            prefix = OperatorNameSyntax(name = node.operation),
            left = parent.render(node.left),
            right = parent.render(node.right),
        )

    public companion object {
        /**
         * The default instance configured with `null`.
         */
        public val Default: BinaryOperator = BinaryOperator(null)
    }
}

/**
 * Handles unary nodes by producing [UnaryOperatorSyntax].
 *
 * @author Iaroslav Postovalov
 */
public class UnaryOperator(operations: Collection<String>?) : Unary(operations) {
    override fun renderUnary(parent: FeaturedMathRenderer, node: MST.Unary): MathSyntax =
        UnaryOperatorSyntax(
            operation = node.operation,
            prefix = OperatorNameSyntax(node.operation),
            operand = OperandSyntax(parent.render(node.value), true),
        )

    public companion object {
        /**
         * The default instance configured with `null`.
         */
        public val Default: UnaryOperator = UnaryOperator(null)
    }
}

/**
 * Handles binary nodes by producing [SuperscriptSyntax].
 *
 * @author Iaroslav Postovalov
 */
public class Power(operations: Collection<String>?) : Binary(operations) {
    override fun renderBinary(parent: FeaturedMathRenderer, node: MST.Binary): MathSyntax =
        SuperscriptSyntax(
            operation = node.operation,
            left = OperandSyntax(parent.render(node.left), true),
            right = OperandSyntax(parent.render(node.right), true),
        )

    public companion object {
        /**
         * The default instance configured with [PowerOperations.POW_OPERATION].
         */
        public val Default: Power = Power(setOf(PowerOperations.POW_OPERATION))
    }
}

/**
 * Handles binary nodes by producing [RadicalSyntax] with no index.
 */
public class SquareRoot(operations: Collection<String>?) : Unary(operations) {
    override fun renderUnary(parent: FeaturedMathRenderer, node: MST.Unary): MathSyntax =
        RadicalSyntax(operation = node.operation, operand = parent.render(node.value))

    public companion object {
        /**
         * The default instance configured with [PowerOperations.SQRT_OPERATION].
         */
        public val Default: SquareRoot = SquareRoot(setOf(PowerOperations.SQRT_OPERATION))
    }
}

/**
 * Handles unary nodes by producing [ExponentSyntax].
 *
 * @author Iaroslav Postovalov
 */
public class Exponent(operations: Collection<String>?) : Unary(operations) {
    override fun renderUnary(parent: FeaturedMathRenderer, node: MST.Unary): MathSyntax = ExponentSyntax(
        operation = node.operation,
        operand = OperandSyntax(operand = parent.render(node.value), parentheses = true),
        useOperatorForm = true,
    )

    public companion object {
        /**
         * The default instance configured with [ExponentialOperations.EXP_OPERATION].
         */
        public val Default: Exponent = Exponent(setOf(ExponentialOperations.EXP_OPERATION))
    }
}

/**
 * Handles binary nodes by producing [MultiplicationSyntax].
 *
 * @author Iaroslav Postovalov
 */
public class Multiplication(operations: Collection<String>?) : Binary(operations) {
    override fun renderBinary(parent: FeaturedMathRenderer, node: MST.Binary): MathSyntax =
        MultiplicationSyntax(
            operation = node.operation,
            left = OperandSyntax(operand = parent.render(node.left), parentheses = true),
            right = OperandSyntax(operand = parent.render(node.right), parentheses = true),
            times = true,
        )

    public companion object {
        /**
         * The default instance configured with [RingOps.TIMES_OPERATION].
         */
        public val Default: Multiplication = Multiplication(setOf(RingOps.TIMES_OPERATION))
    }
}

/**
 * Handles binary nodes by producing inverse [UnaryOperatorSyntax] with *arc* prefix instead of *a*.
 *
 * @author Iaroslav Postovalov
 */
public class InverseTrigonometricOperations(operations: Collection<String>?) : Unary(operations) {
    override fun renderUnary(parent: FeaturedMathRenderer, node: MST.Unary): MathSyntax =
        UnaryOperatorSyntax(
            operation = node.operation,
            prefix = OperatorNameSyntax(name = node.operation.replaceFirst("a", "arc")),
            operand = OperandSyntax(operand = parent.render(node.value), parentheses = true),
        )

    public companion object {
        /**
         * The default instance configured with [TrigonometricOperations.ACOS_OPERATION],
         * [TrigonometricOperations.ASIN_OPERATION], [TrigonometricOperations.ATAN_OPERATION].
         */
        public val Default: InverseTrigonometricOperations = InverseTrigonometricOperations(setOf(
            TrigonometricOperations.ACOS_OPERATION,
            TrigonometricOperations.ASIN_OPERATION,
            TrigonometricOperations.ATAN_OPERATION,
        ))
    }
}

/**
 * Handles binary nodes by producing inverse [UnaryOperatorSyntax] with *ar* prefix instead of *a*.
 *
 * @author Iaroslav Postovalov
 */
public class InverseHyperbolicOperations(operations: Collection<String>?) : Unary(operations) {
    override fun renderUnary(parent: FeaturedMathRenderer, node: MST.Unary): MathSyntax =
        UnaryOperatorSyntax(
            operation = node.operation,
            prefix = OperatorNameSyntax(name = node.operation.replaceFirst("a", "ar")),
            operand = OperandSyntax(operand = parent.render(node.value), parentheses = true),
        )

    public companion object {
        /**
         * The default instance configured with [ExponentialOperations.ACOSH_OPERATION],
         * [ExponentialOperations.ASINH_OPERATION], and [ExponentialOperations.ATANH_OPERATION].
         */
        public val Default: InverseHyperbolicOperations = InverseHyperbolicOperations(setOf(
            ExponentialOperations.ACOSH_OPERATION,
            ExponentialOperations.ASINH_OPERATION,
            ExponentialOperations.ATANH_OPERATION,
        ))
    }
}
