/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast.rendering

import space.kscience.kmath.ast.rendering.FeaturedMathRenderer.RenderFeature
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.*
import kotlin.reflect.KClass

/**
 * Prints any [MST.Symbolic] as a [SymbolSyntax] containing the [MST.Symbolic.value] of it.
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public object PrintSymbolic : RenderFeature {
    public override fun render(renderer: FeaturedMathRenderer, node: MST): SymbolSyntax? =
        if (node !is MST.Symbolic) null
        else
            SymbolSyntax(string = node.value)
}

/**
 * Prints any [MST.Numeric] as a [NumberSyntax] containing the [Any.toString] result of it.
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public object PrintNumeric : RenderFeature {
    public override fun render(renderer: FeaturedMathRenderer, node: MST): NumberSyntax? = if (node !is MST.Numeric)
        null
    else
        NumberSyntax(string = node.value.toString())
}

@UnstableKMathAPI
private fun printSignedNumberString(s: String): MathSyntax = if (s.startsWith('-'))
    UnaryMinusSyntax(
        operation = GroupOperations.MINUS_OPERATION,
        operand = OperandSyntax(
            operand = NumberSyntax(string = s.removePrefix("-")),
            parentheses = true,
        ),
    )
else
    NumberSyntax(string = s)

/**
 * Special printing for numeric types which are printed in form of
 * *('-'? (DIGIT+ ('.' DIGIT+)? ('E' '-'? DIGIT+)? | 'Infinity')) | 'NaN'*.
 *
 * @property types The suitable types.
 */
@UnstableKMathAPI
public class PrettyPrintFloats(public val types: Set<KClass<out Number>>) : RenderFeature {
    public override fun render(renderer: FeaturedMathRenderer, node: MST): MathSyntax? {
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
                operation = RingOperations.TIMES_OPERATION,
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
                    operation = GroupOperations.MINUS_OPERATION,
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
 * Special printing for numeric types which are printed in form of *'-'? DIGIT+*.
 *
 * @property types The suitable types.
 */
@UnstableKMathAPI
public class PrettyPrintIntegers(public val types: Set<KClass<out Number>>) : RenderFeature {
    public override fun render(renderer: FeaturedMathRenderer, node: MST): MathSyntax? =
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
 */
@UnstableKMathAPI
public class PrettyPrintPi(public val symbols: Set<String>) : RenderFeature {
    public override fun render(renderer: FeaturedMathRenderer, node: MST): SpecialSymbolSyntax? =
        if (node !is MST.Symbolic || node.value !in symbols)
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
 * Abstract printing of unary operations which discards [MST] if their operation is not in [operations] or its type is
 * not [MST.Unary].
 *
 * @param operations the allowed operations. If `null`, any operation is accepted.
 */
@UnstableKMathAPI
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
 * Abstract printing of unary operations which discards [MST] if their operation is not in [operations] or its type is
 * not [MST.Binary].
 *
 * @property operations the allowed operations. If `null`, any operation is accepted.
 */
@UnstableKMathAPI
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
 */
@UnstableKMathAPI
public class BinaryPlus(operations: Collection<String>?) : Binary(operations) {
    public override fun renderBinary(parent: FeaturedMathRenderer, node: MST.Binary): BinaryPlusSyntax =
        BinaryPlusSyntax(
            operation = node.operation,
            left = OperandSyntax(parent.render(node.left), true),
            right = OperandSyntax(parent.render(node.right), true),
        )

    public companion object {
        /**
         * The default instance configured with [GroupOperations.PLUS_OPERATION].
         */
        public val Default: BinaryPlus = BinaryPlus(setOf(GroupOperations.PLUS_OPERATION))
    }
}

/**
 * Handles binary nodes by producing [BinaryMinusSyntax].
 */
@UnstableKMathAPI
public class BinaryMinus(operations: Collection<String>?) : Binary(operations) {
    public override fun renderBinary(parent: FeaturedMathRenderer, node: MST.Binary): BinaryMinusSyntax =
        BinaryMinusSyntax(
            operation = node.operation,
            left = OperandSyntax(operand = parent.render(node.left), parentheses = true),
            right = OperandSyntax(operand = parent.render(node.right), parentheses = true),
        )

    public companion object {
        /**
         * The default instance configured with [GroupOperations.MINUS_OPERATION].
         */
        public val Default: BinaryMinus = BinaryMinus(setOf(GroupOperations.MINUS_OPERATION))
    }
}

/**
 * Handles unary nodes by producing [UnaryPlusSyntax].
 */
@UnstableKMathAPI
public class UnaryPlus(operations: Collection<String>?) : Unary(operations) {
    public override fun renderUnary(parent: FeaturedMathRenderer, node: MST.Unary): UnaryPlusSyntax = UnaryPlusSyntax(
        operation = node.operation,
        operand = OperandSyntax(operand = parent.render(node.value), parentheses = true),
    )

    public companion object {
        /**
         * The default instance configured with [GroupOperations.PLUS_OPERATION].
         */
        public val Default: UnaryPlus = UnaryPlus(setOf(GroupOperations.PLUS_OPERATION))
    }
}

/**
 * Handles binary nodes by producing [UnaryMinusSyntax].
 */
@UnstableKMathAPI
public class UnaryMinus(operations: Collection<String>?) : Unary(operations) {
    public override fun renderUnary(parent: FeaturedMathRenderer, node: MST.Unary): UnaryMinusSyntax = UnaryMinusSyntax(
        operation = node.operation,
        operand = OperandSyntax(operand = parent.render(node.value), parentheses = true),
    )

    public companion object {
        /**
         * The default instance configured with [GroupOperations.MINUS_OPERATION].
         */
        public val Default: UnaryMinus = UnaryMinus(setOf(GroupOperations.MINUS_OPERATION))
    }
}

/**
 * Handles binary nodes by producing [FractionSyntax].
 */
@UnstableKMathAPI
public class Fraction(operations: Collection<String>?) : Binary(operations) {
    public override fun renderBinary(parent: FeaturedMathRenderer, node: MST.Binary): FractionSyntax = FractionSyntax(
        operation = node.operation,
        left = parent.render(node.left),
        right = parent.render(node.right),
    )

    public companion object {
        /**
         * The default instance configured with [FieldOperations.DIV_OPERATION].
         */
        public val Default: Fraction = Fraction(setOf(FieldOperations.DIV_OPERATION))
    }
}

/**
 * Handles binary nodes by producing [BinaryOperatorSyntax].
 */
@UnstableKMathAPI
public class BinaryOperator(operations: Collection<String>?) : Binary(operations) {
    public override fun renderBinary(parent: FeaturedMathRenderer, node: MST.Binary): BinaryOperatorSyntax =
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
 */
@UnstableKMathAPI
public class UnaryOperator(operations: Collection<String>?) : Unary(operations) {
    public override fun renderUnary(parent: FeaturedMathRenderer, node: MST.Unary): UnaryOperatorSyntax =
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
 */
@UnstableKMathAPI
public class Power(operations: Collection<String>?) : Binary(operations) {
    public override fun renderBinary(parent: FeaturedMathRenderer, node: MST.Binary): SuperscriptSyntax =
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
@UnstableKMathAPI
public class SquareRoot(operations: Collection<String>?) : Unary(operations) {
    public override fun renderUnary(parent: FeaturedMathRenderer, node: MST.Unary): RadicalSyntax =
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
 */
@UnstableKMathAPI
public class Exponent(operations: Collection<String>?) : Unary(operations) {
    public override fun renderUnary(parent: FeaturedMathRenderer, node: MST.Unary): ExponentSyntax = ExponentSyntax(
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
 */
@UnstableKMathAPI
public class Multiplication(operations: Collection<String>?) : Binary(operations) {
    public override fun renderBinary(parent: FeaturedMathRenderer, node: MST.Binary): MultiplicationSyntax =
        MultiplicationSyntax(
            operation = node.operation,
            left = OperandSyntax(operand = parent.render(node.left), parentheses = true),
            right = OperandSyntax(operand = parent.render(node.right), parentheses = true),
            times = true,
        )

    public companion object {
        /**
         * The default instance configured with [RingOperations.TIMES_OPERATION].
         */
        public val Default: Multiplication = Multiplication(setOf(RingOperations.TIMES_OPERATION))
    }
}

/**
 * Handles binary nodes by producing inverse [UnaryOperatorSyntax] (like *sin<sup>-1</sup>*) with removing the `a`
 * prefix of operation ID.
 */
@UnstableKMathAPI
public class InverseTrigonometricOperations(operations: Collection<String>?) : Unary(operations) {
    public override fun renderUnary(parent: FeaturedMathRenderer, node: MST.Unary): UnaryOperatorSyntax =
        UnaryOperatorSyntax(
            operation = node.operation,
            prefix = SuperscriptSyntax(
                operation = PowerOperations.POW_OPERATION,
                left = OperatorNameSyntax(name = node.operation.removePrefix("a")),
                right = UnaryMinusSyntax(
                    operation = GroupOperations.MINUS_OPERATION,
                    operand = OperandSyntax(operand = NumberSyntax(string = "1"), parentheses = true),
                ),
            ),
            operand = OperandSyntax(operand = parent.render(node.value), parentheses = true),
        )

    public companion object {
        /**
         * The default instance configured with [TrigonometricOperations.ACOS_OPERATION],
         * [TrigonometricOperations.ASIN_OPERATION], [TrigonometricOperations.ATAN_OPERATION],
         * [ExponentialOperations.ACOSH_OPERATION], [ExponentialOperations.ASINH_OPERATION], and
         * [ExponentialOperations.ATANH_OPERATION].
         */
        public val Default: InverseTrigonometricOperations = InverseTrigonometricOperations(setOf(
            TrigonometricOperations.ACOS_OPERATION,
            TrigonometricOperations.ASIN_OPERATION,
            TrigonometricOperations.ATAN_OPERATION,
            ExponentialOperations.ACOSH_OPERATION,
            ExponentialOperations.ASINH_OPERATION,
            ExponentialOperations.ATANH_OPERATION,
        ))
    }
}
