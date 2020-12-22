package kscience.kmath.operations

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
     * Wrap raw string or variable
     */
    public fun symbol(value: String): T = error("Wrapping of '$value' is not supported in $this")

    /**
     * Dynamic call of unary operation with name [operation] on [arg]
     */
    public fun unaryOperation(operation: String, arg: T): T

    /**
     * Currying version of [unaryOperation]
     */
    public fun unaryOperationFunction(operation: String): (T) -> T = { unaryOperation(operation, it) }

    /**
     * Dynamic call of binary operation [operation] on [left] and [right]
     */
    public fun binaryOperation(operation: String, left: T, right: T): T

    /**
     * Curring version of [binaryOperation]
     */
    public fun binaryOperationFunction(operation: String): (left: T, right: T) -> T = { left, right ->
        binaryOperation(operation, left, right)
    }
}

/**
 * An algebraic structure where elements can have numeric representation.
 *
 * @param T the type of element of this structure.
 */
public interface NumericAlgebra<T> : Algebra<T> {
    /**
     * Wraps a number.
     */
    public fun number(value: Number): T

    /**
     * Dynamic call of binary operation [operation] on [left] and [right] where left element is [Number].
     */
    public fun leftSideNumberOperation(operation: String, left: Number, right: T): T =
        binaryOperation(operation, number(left), right)

    /**
     * Curring version of [leftSideNumberOperation]
     */
    public fun leftSideNumberOperationFunction(operation: String): (left: Number, right: T) -> T =
        { left: Number, right: T ->
            leftSideNumberOperation(operation, left, right)
        }

    /**
     * Dynamic call of binary operation [operation] on [left] and [right] where right element is [Number].
     */
    public fun rightSideNumberOperation(operation: String, left: T, right: Number): T =
        leftSideNumberOperation(operation, right, left)

    /**
     * Curring version of [rightSideNumberOperation]
     */
    public fun rightSideNumberOperationFunction(operation: String): (left: T, right: Number) -> T =
        { left: T, right: Number ->
            rightSideNumberOperation(operation, left, right)
        }
}

/**
 * Call a block with an [Algebra] as receiver.
 */
// TODO add contract when KT-32313 is fixed
public inline operator fun <A : Algebra<*>, R> A.invoke(block: A.() -> R): R = block()

/**
 * Represents "semispace", i.e. algebraic structure with associative binary operation called "addition" as well as
 * multiplication by scalars.
 *
 * @param T the type of element of this semispace.
 */
public interface SpaceOperations<T> : Algebra<T> {
    /**
     * Addition of two elements.
     *
     * @param a the addend.
     * @param b the augend.
     * @return the sum.
     */
    public fun add(a: T, b: T): T

    /**
     * Multiplication of element by scalar.
     *
     * @param a the multiplier.
     * @param k the multiplicand.
     * @return the produce.
     */
    public fun multiply(a: T, k: Number): T

    // Operations to be performed in this context. Could be moved to extensions in case of KEEP-176

    /**
     * The negation of this element.
     *
     * @receiver this value.
     * @return the additive inverse of this value.
     */
    public operator fun T.unaryMinus(): T = multiply(this, -1.0)

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

    /**
     * Multiplication of this element by a scalar.
     *
     * @receiver the multiplier.
     * @param k the multiplicand.
     * @return the product.
     */
    public operator fun T.times(k: Number): T = multiply(this, k.toDouble())

    /**
     * Division of this element by scalar.
     *
     * @receiver the dividend.
     * @param k the divisor.
     * @return the quotient.
     */
    public operator fun T.div(k: Number): T = multiply(this, 1.0 / k.toDouble())

    /**
     * Multiplication of this number by element.
     *
     * @receiver the multiplier.
     * @param b the multiplicand.
     * @return the product.
     */
    public operator fun Number.times(b: T): T = b * this

    override fun unaryOperation(operation: String, arg: T): T = when (operation) {
        PLUS_OPERATION -> arg
        MINUS_OPERATION -> -arg
        else -> error("Unary operation $operation not defined in $this")
    }

    override fun binaryOperation(operation: String, left: T, right: T): T = when (operation) {
        PLUS_OPERATION -> add(left, right)
        MINUS_OPERATION -> left - right
        else -> error("Binary operation $operation not defined in $this")
    }

    public companion object {
        /**
         * The identifier of addition.
         */
        public const val PLUS_OPERATION: String = "+"

        /**
         * The identifier of subtraction (and negation).
         */
        public const val MINUS_OPERATION: String = "-"
    }
}

/**
 * Represents linear space, i.e. algebraic structure with associative binary operation called "addition" and its neutral
 * element as well as multiplication by scalars.
 *
 * @param T the type of element of this group.
 */
public interface Space<T> : SpaceOperations<T> {
    /**
     * The neutral element of addition.
     */
    public val zero: T
}

/**
 * Represents semiring, i.e. algebraic structure with two associative binary operations called "addition" and
 * "multiplication".
 *
 * @param T the type of element of this semiring.
 */
public interface RingOperations<T> : SpaceOperations<T> {
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

    override fun binaryOperation(operation: String, left: T, right: T): T = when (operation) {
        TIMES_OPERATION -> multiply(left, right)
        else -> super.binaryOperation(operation, left, right)
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
public interface Ring<T> : Space<T>, RingOperations<T>, NumericAlgebra<T> {
    /**
     * neutral operation for multiplication
     */
    public val one: T

    override fun number(value: Number): T = one * value.toDouble()

    override fun leftSideNumberOperation(operation: String, left: Number, right: T): T = when (operation) {
        SpaceOperations.PLUS_OPERATION -> left + right
        SpaceOperations.MINUS_OPERATION -> left - right
        RingOperations.TIMES_OPERATION -> left * right
        else -> super.leftSideNumberOperation(operation, left, right)
    }

    override fun rightSideNumberOperation(operation: String, left: T, right: Number): T = when (operation) {
        SpaceOperations.PLUS_OPERATION -> left + right
        SpaceOperations.MINUS_OPERATION -> left - right
        RingOperations.TIMES_OPERATION -> left * right
        else -> super.rightSideNumberOperation(operation, left, right)
    }

    /**
     * Addition of element and scalar.
     *
     * @receiver the addend.
     * @param b the augend.
     */
    public operator fun T.plus(b: Number): T = this + number(b)

    /**
     * Addition of scalar and element.
     *
     * @receiver the addend.
     * @param b the augend.
     */
    public operator fun Number.plus(b: T): T = b + this

    /**
     * Subtraction of element from number.
     *
     * @receiver the minuend.
     * @param b the subtrahend.
     * @receiver the difference.
     */
    public operator fun T.minus(b: Number): T = this - number(b)

    /**
     * Subtraction of number from element.
     *
     * @receiver the minuend.
     * @param b the subtrahend.
     * @receiver the difference.
     */
    public operator fun Number.minus(b: T): T = -b + this
}

/**
 * Represents semifield, i.e. algebraic structure with three operations: associative "addition" and "multiplication",
 * and "division".
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

    override fun binaryOperation(operation: String, left: T, right: T): T = when (operation) {
        DIV_OPERATION -> divide(left, right)
        else -> super.binaryOperation(operation, left, right)
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
public interface Field<T> : Ring<T>, FieldOperations<T> {
    /**
     * Division of element by scalar.
     *
     * @receiver the dividend.
     * @param b the divisor.
     * @return the quotient.
     */
    public operator fun Number.div(b: T): T = this * divide(one, b)
}
