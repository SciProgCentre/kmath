package scientifik.kmath.operations

/**
 * Stub for DSL the [Algebra] is.
 */
@DslMarker
annotation class KMathContext

/**
 * Represents an algebraic structure.
 *
 * @param T the type of element of this structure.
 */
interface Algebra<T> {
    /**
     * Wrap raw string or variable
     */
    fun symbol(value: String): T = error("Wrapping of '$value' is not supported in $this")

    /**
     * Dynamic call of unary operation with name [operation] on [arg]
     */
    fun unaryOperation(operation: String, arg: T): T

    /**
     * Dynamic call of binary operation [operation] on [left] and [right]
     */
    fun binaryOperation(operation: String, left: T, right: T): T
}

/**
 * An algebraic structure where elements can have numeric representation.
 *
 * @param T the type of element of this structure.
 */
interface NumericAlgebra<T> : Algebra<T> {
    /**
     * Wraps a number.
     */
    fun number(value: Number): T

    /**
     * Dynamic call of binary operation [operation] on [left] and [right] where left element is [Number].
     */
    fun leftSideNumberOperation(operation: String, left: Number, right: T): T =
        binaryOperation(operation, number(left), right)

    /**
     * Dynamic call of binary operation [operation] on [left] and [right] where right element is [Number].
     */
    fun rightSideNumberOperation(operation: String, left: T, right: Number): T =
        leftSideNumberOperation(operation, right, left)
}

/**
 * Call a block with an [Algebra] as receiver.
 */
inline operator fun <A : Algebra<*>, R> A.invoke(block: A.() -> R): R = run(block)

/**
 * Represents "semispace", i.e. algebraic structure with associative binary operation called "addition" as well as
 * multiplication by scalars.
 *
 * @param T the type of element of this semispace.
 */
interface SpaceOperations<T> : Algebra<T> {
    /**
     * Addition of two elements.
     *
     * @param a the addend.
     * @param b the augend.
     * @return the sum.
     */
    fun add(a: T, b: T): T

    /**
     * Multiplication of element by scalar.
     *
     * @param a the multiplier.
     * @param k the multiplicand.
     * @return the produce.
     */
    fun multiply(a: T, k: Number): T

    // Operations to be performed in this context. Could be moved to extensions in case of KEEP-176

    /**
     * The negation of this element.
     *
     * @receiver this value.
     * @return the additive inverse of this value.
     */
    operator fun T.unaryMinus(): T = multiply(this, -1.0)

    /**
     * Returns this value.
     *
     * @receiver this value.
     * @return this value.
     */
    operator fun T.unaryPlus(): T = this

    /**
     * Addition of two elements.
     *
     * @receiver the addend.
     * @param b the augend.
     * @return the sum.
     */
    operator fun T.plus(b: T): T = add(this, b)

    /**
     * Subtraction of two elements.
     *
     * @receiver the minuend.
     * @param b the subtrahend.
     * @return the difference.
     */
    operator fun T.minus(b: T): T = add(this, -b)

    /**
     * Multiplication of this element by a scalar.
     *
     * @receiver the multiplier.
     * @param k the multiplicand.
     * @return the product.
     */
    operator fun T.times(k: Number): T = multiply(this, k.toDouble())

    /**
     * Division of this element by scalar.
     *
     * @receiver the dividend.
     * @param k the divisor.
     * @return the quotient.
     */
    operator fun T.div(k: Number): T = multiply(this, 1.0 / k.toDouble())

    /**
     * Multiplication of this number by element.
     *
     * @receiver the multiplier.
     * @param b the multiplicand.
     * @return the product.
     */
    operator fun Number.times(b: T): T = b * this

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

    companion object {
        /**
         * The identifier of addition.
         */
        const val PLUS_OPERATION: String = "+"

        /**
         * The identifier of subtraction (and negation).
         */
        const val MINUS_OPERATION: String = "-"

        const val NOT_OPERATION: String = "!"
    }
}

/**
 * Represents linear space, i.e. algebraic structure with associative binary operation called "addition" and its neutral
 * element as well as multiplication by scalars.
 *
 * @param T the type of element of this group.
 */
interface Space<T> : SpaceOperations<T> {
    /**
     * The neutral element of addition.
     */
    val zero: T
}

/**
 * Represents semiring, i.e. algebraic structure with two associative binary operations called "addition" and
 * "multiplication".
 *
 * @param T the type of element of this semiring.
 */
interface RingOperations<T> : SpaceOperations<T> {
    /**
     * Multiplies two elements.
     *
     * @param a the multiplier.
     * @param b the multiplicand.
     */
    fun multiply(a: T, b: T): T

    /**
     * Multiplies this element by scalar.
     *
     * @receiver the multiplier.
     * @param b the multiplicand.
     */
    operator fun T.times(b: T): T = multiply(this, b)

    override fun binaryOperation(operation: String, left: T, right: T): T = when (operation) {
        TIMES_OPERATION -> multiply(left, right)
        else -> super.binaryOperation(operation, left, right)
    }

    companion object {
        /**
         * The identifier of multiplication.
         */
        const val TIMES_OPERATION: String = "*"
    }
}

/**
 * Represents ring, i.e. algebraic structure with two associative binary operations called "addition" and
 * "multiplication" and their neutral elements.
 *
 * @param T the type of element of this ring.
 */
interface Ring<T> : Space<T>, RingOperations<T>, NumericAlgebra<T> {
    /**
     * neutral operation for multiplication
     */
    val one: T

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
    operator fun T.plus(b: Number): T = this + number(b)

    /**
     * Addition of scalar and element.
     *
     * @receiver the addend.
     * @param b the augend.
     */
    operator fun Number.plus(b: T): T = b + this

    /**
     * Subtraction of element from number.
     *
     * @receiver the minuend.
     * @param b the subtrahend.
     * @receiver the difference.
     */
    operator fun T.minus(b: Number): T = this - number(b)

    /**
     * Subtraction of number from element.
     *
     * @receiver the minuend.
     * @param b the subtrahend.
     * @receiver the difference.
     */
    operator fun Number.minus(b: T): T = -b + this
}

/**
 * Represents semifield, i.e. algebraic structure with three operations: associative "addition" and "multiplication",
 * and "division".
 *
 * @param T the type of element of this semifield.
 */
interface FieldOperations<T> : RingOperations<T> {
    /**
     * Division of two elements.
     *
     * @param a the dividend.
     * @param b the divisor.
     * @return the quotient.
     */
    fun divide(a: T, b: T): T

    /**
     * Division of two elements.
     *
     * @receiver the dividend.
     * @param b the divisor.
     * @return the quotient.
     */
    operator fun T.div(b: T): T = divide(this, b)

    override fun binaryOperation(operation: String, left: T, right: T): T = when (operation) {
        DIV_OPERATION -> divide(left, right)
        else -> super.binaryOperation(operation, left, right)
    }

    companion object {
        /**
         * The identifier of division.
         */
        const val DIV_OPERATION: String = "/"
    }
}

/**
 * Represents field, i.e. algebraic structure with three operations: associative "addition" and "multiplication",
 * and "division" and their neutral elements.
 *
 * @param T the type of element of this semifield.
 */
interface Field<T> : Ring<T>, FieldOperations<T> {
    /**
     * Division of element by scalar.
     *
     * @receiver the dividend.
     * @param b the divisor.
     * @return the quotient.
     */
    operator fun Number.div(b: T): T = this * divide(one, b)
}
