/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast.rendering

/**
 * Syntax node for mathematical typography.
 *
 * @author Iaroslav Postovalov
 */
public sealed class MathSyntax {
    /**
     * The parent node of this syntax node.
     */
    public var parent: MathSyntax? = null
}

/**
 * Terminal node, which should not have any children nodes.
 *
 * @author Iaroslav Postovalov
 */
public sealed class TerminalSyntax : MathSyntax()

/**
 * Node containing a certain operation.
 *
 * @author Iaroslav Postovalov
 */
public sealed class OperationSyntax : MathSyntax() {
    /**
     * The operation token.
     */
    public abstract val operation: String
}

/**
 * Unary node, which has only one child.
 *
 * @author Iaroslav Postovalov
 */
public sealed class UnarySyntax : OperationSyntax() {
    /**
     * The operand of this node.
     */
    public abstract val operand: MathSyntax
}

/**
 * Binary node, which has only two children.
 *
 * @author Iaroslav Postovalov
 */
public sealed class BinarySyntax : OperationSyntax() {
    /**
     * The left-hand side operand.
     */
    public abstract val left: MathSyntax

    /**
     * The right-hand side operand.
     */
    public abstract val right: MathSyntax
}

/**
 * Represents a number.
 *
 * @property string The digits of number.
 * @author Iaroslav Postovalov
 */
public data class NumberSyntax(public var string: String) : TerminalSyntax()

/**
 * Represents a symbol.
 *
 * @property string The symbol.
 * @author Iaroslav Postovalov
 */
public data class SymbolSyntax(public var string: String) : TerminalSyntax()

/**
 * Represents special typing for operator name.
 *
 * @property name The operator name.
 * @see BinaryOperatorSyntax
 * @see UnaryOperatorSyntax
 * @author Iaroslav Postovalov
 */
public data class OperatorNameSyntax(public var name: String) : TerminalSyntax()

/**
 * Represents a usage of special symbols (e.g., *&infin;*).
 *
 * @property kind The kind of symbol.
 * @author Iaroslav Postovalov
 */
public data class SpecialSymbolSyntax(public var kind: Kind) : TerminalSyntax() {
    /**
     * The kind of symbol.
     */
    public enum class Kind {
        /**
         * The infinity (&infin;) symbol.
         */
        INFINITY,

        /**
         * The Pi (&pi;) symbol.
         */
        SMALL_PI;
    }
}

/**
 * Represents operand of a certain operator wrapped with parentheses or not.
 *
 * @property operand The operand.
 * @property parentheses Whether the operand should be wrapped with parentheses.
 * @author Iaroslav Postovalov
 */
public data class OperandSyntax(
    public val operand: MathSyntax,
    public var parentheses: Boolean,
) : MathSyntax() {
    init {
        operand.parent = this
    }
}

/**
 * Represents unary, prefix operator syntax (like *f(x)*).
 *
 * @property prefix The prefix.
 * @author Iaroslav Postovalov
 */
public data class UnaryOperatorSyntax(
    override val operation: String,
    public var prefix: MathSyntax,
    override val operand: OperandSyntax,
) : UnarySyntax() {
    init {
        operand.parent = this
    }
}

/**
 * Represents prefix, unary plus operator (*+x*).
 *
 * @author Iaroslav Postovalov
 */
public data class UnaryPlusSyntax(
    override val operation: String,
    override val operand: OperandSyntax,
) : UnarySyntax() {
    init {
        operand.parent = this
    }
}

/**
 * Represents prefix, unary minus operator (*-x*).
 *
 * @author Iaroslav Postovalov
 */
public data class UnaryMinusSyntax(
    override val operation: String,
    override val operand: OperandSyntax,
) : UnarySyntax() {
    init {
        operand.parent = this
    }
}

/**
 * Represents radical with a node inside it (*&radic;x*).
 *
 * @property operand The radicand.
 * @author Iaroslav Postovalov
 */
public data class RadicalSyntax(
    override val operation: String,
    override val operand: MathSyntax,
) : UnarySyntax() {
    init {
        operand.parent = this
    }
}

/**
 * Represents exponential function.
 *
 * @property operand The argument of function.
 * @property useOperatorForm `true` if operator form is used (*exp (x)*), `false` if exponentiation form is used
 * (*e<sup>x</sup>*).
 * @author Iaroslav Postovalov
 */
public data class ExponentSyntax(
    override val operation: String,
    override val operand: OperandSyntax,
    public var useOperatorForm: Boolean,
) : UnarySyntax() {
    init {
        operand.parent = this
    }
}

/**
 * Represents a syntax node with superscript (*x<sup>2</sup>*).
 *
 * @property left The node.
 * @property right The superscript.
 * @author Iaroslav Postovalov
 */
public data class SuperscriptSyntax(
    override val operation: String,
    override val left: MathSyntax,
    override val right: MathSyntax,
) : BinarySyntax() {
    init {
        left.parent = this
        right.parent = this
    }
}

/**
 * Represents a syntax node with subscript (*x<sub>i</sup>*).
 *
 * @property left The node.
 * @property right The subscript.
 * @author Iaroslav Postovalov
 */
public data class SubscriptSyntax(
    override val operation: String,
    override val left: MathSyntax,
    override val right: MathSyntax,
) : BinarySyntax() {
    init {
        left.parent = this
        right.parent = this
    }
}

/**
 * Represents binary, prefix operator syntax (like *f(a, b)*).
 *
 * @property prefix The prefix.
 * @author Iaroslav Postovalov
 */
public data class BinaryOperatorSyntax(
    override val operation: String,
    public var prefix: MathSyntax,
    override val left: MathSyntax,
    override val right: MathSyntax,
) : BinarySyntax() {
    init {
        left.parent = this
        right.parent = this
    }
}

/**
 * Represents binary, infix addition (*42 + 42*).
 *
 * @param left The augend.
 * @param right The addend.
 * @author Iaroslav Postovalov
 */
public data class BinaryPlusSyntax(
    override val operation: String,
    override val left: OperandSyntax,
    override val right: OperandSyntax,
) : BinarySyntax() {
    init {
        left.parent = this
        right.parent = this
    }
}

/**
 * Represents binary, infix subtraction (*42 &minus; 42*).
 *
 * @param left The minuend.
 * @param right The subtrahend.
 * @author Iaroslav Postovalov
 */
public data class BinaryMinusSyntax(
    override val operation: String,
    override val left: OperandSyntax,
    override val right: OperandSyntax,
) : BinarySyntax() {
    init {
        left.parent = this
        right.parent = this
    }
}

/**
 * Represents fraction with numerator and denominator.
 *
 * @property left The numerator.
 * @property right The denominator.
 * @property infix Whether infix (*1 / 2*) or normal (*&frac12;*) fraction should be made.
 * @author Iaroslav Postovalov
 */
public data class FractionSyntax(
    override val operation: String,
    override val left: OperandSyntax,
    override val right: OperandSyntax,
    public var infix: Boolean,
) : BinarySyntax() {
    init {
        left.parent = this
        right.parent = this
    }
}

/**
 * Represents radical syntax with index (*<sup>3</sup>&radic;x*).
 *
 * @property left The index.
 * @property right The radicand.
 * @author Iaroslav Postovalov
 */
public data class RadicalWithIndexSyntax(
    override val operation: String,
    override val left: MathSyntax,
    override val right: MathSyntax,
) : BinarySyntax() {
    init {
        left.parent = this
        right.parent = this
    }
}

/**
 * Represents binary, infix multiplication in the form of coefficient (*2 x*) or with operator (*x &times; 2*).
 *
 * @property left The multiplicand.
 * @property right The multiplier.
 * @property times Whether the times (&times;) symbol should be used.
 * @author Iaroslav Postovalov
 */
public data class MultiplicationSyntax(
    override val operation: String,
    override val left: OperandSyntax,
    override val right: OperandSyntax,
    public var times: Boolean,
) : BinarySyntax() {
    init {
        left.parent = this
        right.parent = this
    }
}
