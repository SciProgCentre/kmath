package space.kscience.kmath.operations

import space.kscience.kmath.expressions.Symbol

/**
 * Stub for DSL the [Algebra] is.
 */
@DslMarker
public annotation class KMathContext

/**
 * Represents an algebraic structure.
 *
 * @param T the type of element of this structure.
 */
public interface Algebra<T> {
    /**
     * Wraps a raw string to [T] object. This method is designed for three purposes:
     *
     * 1. Mathematical constants (`e`, `pi`).
     * 2. Variables for expression-like contexts (`a`, `b`, `c`...).
     * 3. Literals (`{1, 2}`, (`(3; 4)`)).
     *
     * In case if algebra can't parse the string, this method must throw [kotlin.IllegalStateException].
     *
     * @param value the raw string.
     * @return an object.
     */
    public fun bindSymbol(value: String): T = error("Wrapping of '$value' is not supported in $this")

    /**
     * Dynamically dispatches an unary operation with the certain name.
     *
     * This function must follow two properties:
     *
     * 1. In case if operation is not defined in the structure, the function throws [kotlin.IllegalStateException].
     * 2. This function is symmetric with second `unaryOperation` overload:
     * i.e. `unaryOperationFunction(a)(b) == unaryOperation(a, b)`.
     *
     * @param operation the name of operation.
     * @return an operation.
     */
    public fun unaryOperationFunction(operation: String): (arg: T) -> T =
        error("Unary operation $operation not defined in $this")

    /**
     * Dynamically invokes an unary operation with the certain name.
     *
     * This function must follow two properties:
     *
     * 1. In case if operation is not defined in the structure, the function throws [kotlin.IllegalStateException].
     * 2. This function is symmetric with second [unaryOperationFunction] overload:
     * i.e. `unaryOperationFunction(a)(b) == unaryOperation(a, b)`.
     *
     * @param operation the name of operation.
     * @param arg the argument of operation.
     * @return a result of operation.
     */
    public fun unaryOperation(operation: String, arg: T): T = unaryOperationFunction(operation)(arg)

    /**
     * Dynamically dispatches a binary operation with the certain name.
     *
     * This function must follow two properties:
     *
     * 1. In case if operation is not defined in the structure, the function throws [kotlin.IllegalStateException].
     * 2. This function is symmetric with second [binaryOperationFunction] overload:
     * i.e. `binaryOperationFunction(a)(b, c) == binaryOperation(a, b, c)`.
     *
     * @param operation the name of operation.
     * @return an operation.
     */
    public fun binaryOperationFunction(operation: String): (left: T, right: T) -> T =
        error("Binary operation $operation not defined in $this")

    /**
     * Dynamically invokes a binary operation with the certain name.
     *
     * This function must follow two properties:
     *
     * 1. In case if operation is not defined in the structure, the function throws [kotlin.IllegalStateException].
     * 2. This function is symmetric with second [binaryOperationFunction] overload:
     * i.e. `binaryOperationFunction(a)(b, c) == binaryOperation(a, b, c)`.
     *
     * @param operation the name of operation.
     * @param left the first argument of operation.
     * @param right the second argument of operation.
     * @return a result of operation.
     */
    public fun binaryOperation(operation: String, left: T, right: T): T =
        binaryOperationFunction(operation)(left, right)
}

public fun <T : Any> Algebra<T>.bindSymbol(symbol: Symbol): T = bindSymbol(symbol.identity)

/**
 * Call a block with an [Algebra] as receiver.
 */
// TODO add contract when KT-32313 is fixed
public inline operator fun <A : Algebra<*>, R> A.invoke(block: A.() -> R): R = run(block)

/**
 * Represents linear space without neutral element, i.e. algebraic structure with associative, binary operation [add]
 * and scalar multiplication [multiply].
 *
 * @param T the type of element of this semispace.
 */
public interface GroupOperations<T> : Algebra<T> {
    /**
     * Addition of two elements.
     *
     * @param a the addend.
     * @param b the augend.
     * @return the sum.
     */
    public fun add(a: T, b: T): T

    // Operations to be performed in this context. Could be moved to extensions in case of KEEP-176

    /**
     * The negation of this element.
     *
     * @receiver this value.
     * @return the additive inverse of this value.
     */
    public operator fun T.unaryMinus(): T

    /**
     * Returns this value.
     *
     * @receiver this value.
     * @return this value.
     */
    public operator fun T.unaryPlus(): T = this

    /**
     * Addition of two elements.
     *
     * @receiver the addend.
     * @param b the augend.
     * @return the sum.
     */
    public operator fun T.plus(b: T): T = add(this, b)

    /**
     * Subtraction of two elements.
     *
     * @receiver the minuend.
     * @param b the subtrahend.
     * @return the difference.
     */
    public operator fun T.minus(b: T): T = add(this, -b)

    public override fun unaryOperationFunction(operation: String): (arg: T) -> T = when (operation) {
        PLUS_OPERATION -> { arg -> +arg }
        MINUS_OPERATION -> { arg -> -arg }
        else -> super.unaryOperationFunction(operation)
    }

    public override fun binaryOperationFunction(operation: String): (left: T, right: T) -> T = when (operation) {
        PLUS_OPERATION -> ::add
        MINUS_OPERATION -> { left, right -> left - right }
        else -> super.binaryOperationFunction(operation)
    }

    public companion object {
        /**
         * The identifier of addition and unary positive operator.
         */
        public const val PLUS_OPERATION: String = "+"

        /**
         * The identifier of subtraction and unary negative operator.
         */
        public const val MINUS_OPERATION: String = "-"
    }
}

/**
 * Represents linear space with neutral element, i.e. algebraic structure with associative, binary operation [add].
 *
 * @param T the type of element of this semispace.
 */
public interface Group<T> : GroupOperations<T> {
    /**
     * The neutral element of addition.
     */
    public val zero: T
}

/**
 * Represents rng, i.e. algebraic structure with associative, binary, commutative operation [add] and associative,
 * operation [multiply] distributive over [add].
 *
 * @param T the type of element of this semiring.
 */
public interface RingOperations<T> : GroupOperations<T> {
    /**
     * Multiplies two elements.
     *
     * @param a the multiplier.
     * @param b the multiplicand.
     */
    public fun multiply(a: T, b: T): T

    /**
     * Multiplies this element by scalar.
     *
     * @receiver the multiplier.
     * @param b the multiplicand.
     */
    public operator fun T.times(b: T): T = multiply(this, b)

    public override fun binaryOperationFunction(operation: String): (left: T, right: T) -> T = when (operation) {
        TIMES_OPERATION -> ::multiply
        else -> super.binaryOperationFunction(operation)
    }

    public companion object {
        /**
         * The identifier of multiplication.
         */
        public const val TIMES_OPERATION: String = "*"
    }
}

/**
 * Represents ring, i.e. algebraic structure with two associative binary operations called "addition" and
 * "multiplication" and their neutral elements.
 *
 * @param T the type of element of this ring.
 */
public interface Ring<T> : Group<T>, RingOperations<T> {
    /**
     * neutral operation for multiplication
     */
    public val one: T
}

/**
 * Represents field without identity elements, i.e. algebraic structure with associative, binary, commutative operations
 * [add] and [multiply]; binary operation [divide] as multiplication of left operand by reciprocal of right one.
 *
 * @param T the type of element of this semifield.
 */
public interface FieldOperations<T> : RingOperations<T> {
    /**
     * Division of two elements.
     *
     * @param a the dividend.
     * @param b the divisor.
     * @return the quotient.
     */
    public fun divide(a: T, b: T): T

    /**
     * Division of two elements.
     *
     * @receiver the dividend.
     * @param b the divisor.
     * @return the quotient.
     */
    public operator fun T.div(b: T): T = divide(this, b)

    public override fun binaryOperationFunction(operation: String): (left: T, right: T) -> T = when (operation) {
        DIV_OPERATION -> ::divide
        else -> super.binaryOperationFunction(operation)
    }

    public companion object {
        /**
         * The identifier of division.
         */
        public const val DIV_OPERATION: String = "/"
    }
}

/**
 * Represents field, i.e. algebraic structure with three operations: associative "addition" and "multiplication",
 * and "division" and their neutral elements.
 *
 * @param T the type of element of this semifield.
 */
public interface Field<T> : Ring<T>, FieldOperations<T>, ScaleOperations<T>, NumericAlgebra<T> {
    override fun number(value: Number): T = scale(one, value.toDouble())
}