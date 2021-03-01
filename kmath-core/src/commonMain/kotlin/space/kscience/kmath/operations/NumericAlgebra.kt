package space.kscience.kmath.operations

import space.kscience.kmath.misc.UnstableKMathAPI

/**
 * An algebraic structure where elements can have numeric representation.
 *
 * @param T the type of element of this structure.
 */
public interface NumericAlgebra<T> : Algebra<T> {
    /**
     * Wraps a number to [T] object.
     *
     * @param value the number to wrap.
     * @return an object.
     */
    public fun number(value: Number): T

    /**
     * Dynamically dispatches a binary operation with the certain name with numeric first argument.
     *
     * This function must follow two properties:
     *
     * 1. In case if operation is not defined in the structure, the function throws [kotlin.IllegalStateException].
     * 2. This function is symmetric with the other [leftSideNumberOperation] overload:
     * i.e. `leftSideNumberOperationFunction(a)(b, c) == leftSideNumberOperation(a, b)`.
     *
     * @param operation the name of operation.
     * @return an operation.
     */
    public fun leftSideNumberOperationFunction(operation: String): (left: Number, right: T) -> T =
        { l, r -> binaryOperationFunction(operation)(number(l), r) }

    /**
     * Dynamically invokes a binary operation with the certain name with numeric first argument.
     *
     * This function must follow two properties:
     *
     * 1. In case if operation is not defined in the structure, the function throws [kotlin.IllegalStateException].
     * 2. This function is symmetric with second [leftSideNumberOperation] overload:
     * i.e. `leftSideNumberOperationFunction(a)(b, c) == leftSideNumberOperation(a, b, c)`.
     *
     * @param operation the name of operation.
     * @param left the first argument of operation.
     * @param right the second argument of operation.
     * @return a result of operation.
     */
    public fun leftSideNumberOperation(operation: String, left: Number, right: T): T =
        leftSideNumberOperationFunction(operation)(left, right)

    /**
     * Dynamically dispatches a binary operation with the certain name with numeric first argument.
     *
     * This function must follow two properties:
     *
     * 1. In case if operation is not defined in the structure, the function throws [kotlin.IllegalStateException].
     * 2. This function is symmetric with the other [rightSideNumberOperationFunction] overload:
     * i.e. `rightSideNumberOperationFunction(a)(b, c) == leftSideNumberOperation(a, b, c)`.
     *
     * @param operation the name of operation.
     * @return an operation.
     */
    public fun rightSideNumberOperationFunction(operation: String): (left: T, right: Number) -> T =
        { l, r -> binaryOperationFunction(operation)(l, number(r)) }

    /**
     * Dynamically invokes a binary operation with the certain name with numeric second argument.
     *
     * This function must follow two properties:
     *
     * 1. In case if operation is not defined in the structure, the function throws [kotlin.IllegalStateException].
     * 2. This function is symmetric with the other [rightSideNumberOperationFunction] overload:
     * i.e. `rightSideNumberOperationFunction(a)(b, c) == rightSideNumberOperation(a, b, c)`.
     *
     * @param operation the name of operation.
     * @param left the first argument of operation.
     * @param right the second argument of operation.
     * @return a result of operation.
     */
    public fun rightSideNumberOperation(operation: String, left: T, right: Number): T =
        rightSideNumberOperationFunction(operation)(left, right)
}

/**
 * A combination of [NumericAlgebra] and [Ring] that adds intrinsic simple operations on numbers like `T+1`
 * TODO to be removed and replaced by extensions after multiple receivers are there
 */
@UnstableKMathAPI
public interface RingWithNumbers<T>: Ring<T>, NumericAlgebra<T>{
    public override fun number(value: Number): T = one * value

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