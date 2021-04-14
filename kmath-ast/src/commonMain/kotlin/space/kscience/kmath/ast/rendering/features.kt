package space.kscience.kmath.ast.rendering

import space.kscience.kmath.ast.rendering.FeaturedMathRenderer.RenderFeature
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.operations.*
import kotlin.reflect.KClass

/**
 * Prints any [MST.Symbolic] as a [SymbolSyntax] containing the [MST.Symbolic.value] of it.
 *
 * @author Iaroslav Postovalov
 */
public object PrintSymbolic : RenderFeature {
    public override fun render(renderer: FeaturedMathRenderer, node: MST): MathSyntax? {
        if (node !is MST.Symbolic) return null
        return SymbolSyntax(string = node.value)
    }
}

/**
 * Prints any [MST.Numeric] as a [NumberSyntax] containing the [Any.toString] result of it.
 *
 * @author Iaroslav Postovalov
 */
public object PrintNumeric : RenderFeature {
    public override fun render(renderer: FeaturedMathRenderer, node: MST): MathSyntax? {
        if (node !is MST.Numeric) return null
        return NumberSyntax(string = node.value.toString())
    }
}

private fun printSignedNumberString(s: String): MathSyntax {
    if (s.startsWith('-'))
        return UnaryMinusSyntax(
            operation = GroupOperations.MINUS_OPERATION,
            operand = OperandSyntax(
                operand = NumberSyntax(string = s.removePrefix("-")),
                parentheses = true,
            ),
        )

    return NumberSyntax(string = s)
}

/**
 * Special printing for numeric types which are printed in form of
 * *('-'? (DIGIT+ ('.' DIGIT+)? ('E' '-'? DIGIT+)? | 'Infinity')) | 'NaN'*.
 *
 * @property types The suitable types.
 */
public class PrettyPrintFloats(public val types: Set<KClass<out Number>>) : RenderFeature {
    public override fun render(renderer: FeaturedMathRenderer, node: MST): MathSyntax? {
        if (node !is MST.Numeric || node.value::class !in types) return null
        val toString = node.value.toString().removeSuffix(".0")

        if ('E' in toString) {
            val (beforeE, afterE) = toString.split('E')
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
public class PrettyPrintIntegers(public val types: Set<KClass<out Number>>) : RenderFeature {
    public override fun render(renderer: FeaturedMathRenderer, node: MST): MathSyntax? {
        if (node !is MST.Numeric || node.value::class !in types)
            return null

        return printSignedNumberString(node.value.toString())
    }

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
public class PrettyPrintPi(public val symbols: Set<String>) : RenderFeature {
    public override fun render(renderer: FeaturedMathRenderer, node: MST): MathSyntax? {
        if (node !is MST.Symbolic || node.value !in symbols) return null
        return SpecialSymbolSyntax(kind = SpecialSymbolSyntax.Kind.SMALL_PI)
    }

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
public abstract class Unary(public val operations: Collection<String>?) : RenderFeature {
    /**
     * The actual render function.
     */
    protected abstract fun render0(parent: FeaturedMathRenderer, node: MST.Unary): MathSyntax?

    public final override fun render(renderer: FeaturedMathRenderer, node: MST): MathSyntax? {
        if (node !is MST.Unary || operations != null && node.operation !in operations) return null
        return render0(renderer, node)
    }
}

/**
 * Abstract printing of unary operations which discards [MST] if their operation is not in [operations] or its type is
 * not [MST.Binary].
 *
 * @property operations the allowed operations. If `null`, any operation is accepted.
 */
public abstract class Binary(public val operations: Collection<String>?) : RenderFeature {
    /**
     * The actual render function.
     */
    protected abstract fun render0(parent: FeaturedMathRenderer, node: MST.Binary): MathSyntax?

    public final override fun render(renderer: FeaturedMathRenderer, node: MST): MathSyntax? {
        if (node !is MST.Binary || operations != null && node.operation !in operations) return null
        return render0(renderer, node)
    }
}

public class BinaryPlus(operations: Collection<String>?) : Binary(operations) {
    public override fun render0(parent: FeaturedMathRenderer, node: MST.Binary): MathSyntax = BinaryPlusSyntax(
        operation = node.operation,
        left = OperandSyntax(parent.render(node.left), true),
        right = OperandSyntax(parent.render(node.right), true),
    )

    public companion object {
        public val Default: BinaryPlus = BinaryPlus(setOf(GroupOperations.PLUS_OPERATION))
    }
}

public class BinaryMinus(operations: Collection<String>?) : Binary(operations) {
    public override fun render0(parent: FeaturedMathRenderer, node: MST.Binary): MathSyntax = BinaryMinusSyntax(
        operation = node.operation,
        left = OperandSyntax(operand = parent.render(node.left), parentheses = true),
        right = OperandSyntax(operand = parent.render(node.right), parentheses = true),
    )

    public companion object {
        public val Default: BinaryMinus = BinaryMinus(setOf(GroupOperations.MINUS_OPERATION))
    }
}

public class UnaryPlus(operations: Collection<String>?) : Unary(operations) {
    public override fun render0(parent: FeaturedMathRenderer, node: MST.Unary): MathSyntax = UnaryPlusSyntax(
        operation = node.operation,
        operand = OperandSyntax(operand = parent.render(node.value), parentheses = true),
    )

    public companion object {
        public val Default: UnaryPlus = UnaryPlus(setOf(GroupOperations.PLUS_OPERATION))
    }
}

public class UnaryMinus(operations: Collection<String>?) : Unary(operations) {
    public override fun render0(parent: FeaturedMathRenderer, node: MST.Unary): MathSyntax = UnaryMinusSyntax(
        operation = node.operation,
        operand = OperandSyntax(operand = parent.render(node.value), parentheses = true),
    )

    public companion object {
        public val Default: UnaryMinus = UnaryMinus(setOf(GroupOperations.MINUS_OPERATION))
    }
}

public class Fraction(operations: Collection<String>?) : Binary(operations) {
    public override fun render0(parent: FeaturedMathRenderer, node: MST.Binary): MathSyntax = FractionSyntax(
        operation = node.operation,
        left = parent.render(node.left),
        right = parent.render(node.right),
    )

    public companion object {
        public val Default: Fraction = Fraction(setOf(FieldOperations.DIV_OPERATION))
    }
}

public class BinaryOperator(operations: Collection<String>?) : Binary(operations) {
    public override fun render0(parent: FeaturedMathRenderer, node: MST.Binary): MathSyntax = BinaryOperatorSyntax(
        operation = node.operation,
        prefix = OperatorNameSyntax(name = node.operation),
        left = parent.render(node.left),
        right = parent.render(node.right),
    )

    public companion object {
        public val Default: BinaryOperator = BinaryOperator(null)
    }
}

public class UnaryOperator(operations: Collection<String>?) : Unary(operations) {
    public override fun render0(parent: FeaturedMathRenderer, node: MST.Unary): MathSyntax = UnaryOperatorSyntax(
        operation = node.operation,
        prefix = OperatorNameSyntax(node.operation),
        operand = OperandSyntax(parent.render(node.value), true),
    )

    public companion object {
        public val Default: UnaryOperator = UnaryOperator(null)
    }
}

public class Power(operations: Collection<String>?) : Binary(operations) {
    public override fun render0(parent: FeaturedMathRenderer, node: MST.Binary): MathSyntax = SuperscriptSyntax(
        operation = node.operation,
        left = OperandSyntax(parent.render(node.left), true),
        right = OperandSyntax(parent.render(node.right), true),
    )

    public companion object {
        public val Default: Power = Power(setOf(PowerOperations.POW_OPERATION))
    }
}

public class SquareRoot(operations: Collection<String>?) : Unary(operations) {
    public override fun render0(parent: FeaturedMathRenderer, node: MST.Unary): MathSyntax =
        RadicalSyntax(operation = node.operation, operand = parent.render(node.value))

    public companion object {
        public val Default: SquareRoot = SquareRoot(setOf(PowerOperations.SQRT_OPERATION))
    }
}

public class Exponential(operations: Collection<String>?) : Unary(operations) {
    public override fun render0(parent: FeaturedMathRenderer, node: MST.Unary): MathSyntax = SuperscriptSyntax(
        operation = node.operation,
        left = SymbolSyntax(string = "e"),
        right = parent.render(node.value),
    )

    public companion object {
        public val Default: Exponential = Exponential(setOf(ExponentialOperations.EXP_OPERATION))
    }
}

public class Multiplication(operations: Collection<String>?) : Binary(operations) {
    public override fun render0(parent: FeaturedMathRenderer, node: MST.Binary): MathSyntax = MultiplicationSyntax(
        operation = node.operation,
        left = OperandSyntax(operand = parent.render(node.left), parentheses = true),
        right = OperandSyntax(operand = parent.render(node.right), parentheses = true),
        times = true,
    )

    public companion object {
        public val Default: Multiplication = Multiplication(setOf(
            RingOperations.TIMES_OPERATION,
        ))
    }
}

public class InverseTrigonometricOperations(operations: Collection<String>?) : Unary(operations) {
    public override fun render0(parent: FeaturedMathRenderer, node: MST.Unary): MathSyntax = UnaryOperatorSyntax(
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
