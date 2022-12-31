/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions


import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MutableBuffer
import kotlin.math.min

internal fun <T> MutableBuffer<T>.fill(element: T, fromIndex: Int = 0, toIndex: Int = size) {
    for (i in fromIndex until toIndex) this[i] = element
}

/**
 * Class holding "compiled" computation rules for derivative structures.
 *
 * This class implements the computation rules described in Dan Kalman's paper
 * [Doubly Recursive Multivariate Automatic Differentiation](http://www1.american.edu/cas/mathstat/People/kalman/pdffiles/mmgautodiff.pdf),
 * Mathematics Magazine, vol. 75, no. 3, June 2002. However, to avoid performances bottlenecks, the recursive rules are
 * "compiled" once in an unfolded form. This class does this recursion unrolling and stores the computation rules as
 * simple loops with pre-computed indirection arrays.
 *
 * This class maps all derivative computation into single dimension arrays that hold the value and partial derivatives.
 * The class does not hold these arrays, which remains under the responsibility of the caller. For each combination of
 * number of free parameters and derivation order, only one compiler is necessary, and this compiler will be used to
 * perform computations on all arrays provided to it, which can represent hundreds or thousands of different parameters
 * kept together with all their partial derivatives.
 *
 * The arrays on which compilers operate contain only the partial derivatives together with the 0<sup>th</sup>
 * derivative, i.e., the value. The partial derivatives are stored in a compiler-specific order, which can be retrieved
 * using methods [getPartialDerivativeIndex] and [getPartialDerivativeOrders]. The value is guaranteed to be stored as
 * the first element (i.e., the [getPartialDerivativeIndex] method returns 0 when called with 0 for all derivation
 * orders and [getPartialDerivativeOrders] returns an array filled with 0 when called with 0 as the index).
 *
 * Note that the ordering changes with number of parameters and derivation order. For example given 2 parameters x and
 * y, df/dy is stored at index 2 when derivation order is set to 1 (in this case the array has three elements: f,
 * df/dx and df/dy). If derivation order is set to 2, then df/dy will be stored at index 3 (in this case the array has
 * six elements: f, df/dx, df/dxdx, df/dy, df/dxdy and df/dydy).
 *
 * Given this structure, users can perform some simple operations like adding, subtracting or multiplying constants and
 * negating the elements by themselves, knowing if they want to mutate their array or create a new array. These simple
 * operations are not provided by the compiler. The compiler provides only the more complex operations between several
 * arrays.
 *
 * Derived from
 * [Commons Math's `DSCompiler`](https://github.com/apache/commons-math/blob/924f6c357465b39beb50e3c916d5eb6662194175/commons-math-legacy/src/main/java/org/apache/commons/math4/legacy/analysis/differentiation/DSCompiler.java).
 *
 * @property freeParameters Number of free parameters.
 * @property order Derivation order.
 * @see DS
 */
public class DSCompiler<T, out A : Algebra<T>> internal constructor(
    public val algebra: A,
    public val freeParameters: Int,
    public val order: Int,
    valueCompiler: DSCompiler<T, A>?,
    derivativeCompiler: DSCompiler<T, A>?,
) {
    /**
     * Number of partial derivatives (including the single 0 order derivative element).
     */
    public val sizes: Array<IntArray> by lazy {
        compileSizes(
            freeParameters,
            order,
            valueCompiler,
        )
    }

    /**
     * Indirection array for partial derivatives.
     */
    internal val derivativesIndirection: Array<IntArray> by lazy {
        compileDerivativesIndirection(
            freeParameters, order,
            valueCompiler, derivativeCompiler,
        )
    }

    /**
     * Indirection array of the lower derivative elements.
     */
    internal val lowerIndirection: IntArray by lazy {
        compileLowerIndirection(
            freeParameters, order,
            valueCompiler, derivativeCompiler,
        )
    }

    /**
     * Indirection arrays for multiplication.
     */
    internal val multIndirection: Array<Array<IntArray>> by lazy {
        compileMultiplicationIndirection(
            freeParameters, order,
            valueCompiler, derivativeCompiler, lowerIndirection,
        )
    }

    /**
     * Indirection arrays for function composition.
     */
    internal val compositionIndirection: Array<Array<IntArray>> by lazy {
        compileCompositionIndirection(
            freeParameters, order,
            valueCompiler, derivativeCompiler,
            sizes, derivativesIndirection,
        )
    }

    /**
     * Get the array size required for holding partial derivatives' data.
     *
     * This number includes the single 0 order derivative element, which is
     * guaranteed to be stored in the first element of the array.
     */
    public val size: Int get() = sizes[freeParameters][order]

    /**
     * Get the index of a partial derivative in the array.
     *
     * If all orders are set to 0, then the 0<sup>th</sup> order derivative is returned, which is the value of the
     * function.
     *
     * The indices of derivatives are between 0 and [size] &minus; 1. Their specific order is fixed for a given compiler, but
     * otherwise not publicly specified. There are however some simple cases which have guaranteed indices:
     *
     *  * the index of 0<sup>th</sup> order derivative is always 0
     *  * if there is only 1 [freeParameters], then the
     * derivatives are sorted in increasing derivation order (i.e., f at index 0, df/dp
     * at index 1, d<sup>2</sup>f/dp<sup>2</sup> at index 2 &hellip;
     * d<sup>k</sup>f/dp<sup>k</sup> at index k),
     *  * if the [order] is 1, then the derivatives
     * are sorted in increasing free parameter order (i.e., f at index 0, df/dx<sub>1</sub>
     * at index 1, df/dx<sub>2</sub> at index 2 &hellip; df/dx<sub>k</sub> at index k),
     *  * all other cases are not publicly specified.
     *
     * This method is the inverse of method [getPartialDerivativeOrders].
     *
     * @param orders derivation orders with respect to each parameter.
     * @return index of the partial derivative.
     * @see getPartialDerivativeOrders
     */
    public fun getPartialDerivativeIndex(vararg orders: Int): Int {
        // safety check
        require(orders.size == freeParameters) { "dimension mismatch: ${orders.size} and $freeParameters" }
        return getPartialDerivativeIndex(freeParameters, order, sizes, *orders)
    }

    /**
     * Get the derivation orders for a specific index in the array.
     *
     * This method is the inverse of [getPartialDerivativeIndex].
     *
     * @param index of the partial derivative
     * @return orders derivation orders with respect to each parameter
     * @see getPartialDerivativeIndex
     */
    public fun getPartialDerivativeOrders(index: Int): IntArray = derivativesIndirection[index]
}

/**
 * Compute natural logarithm of a derivative structure.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param result array where result must be stored (for logarithm the result array *cannot* be the input array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.ln(
    operand: Buffer<T>,
    operandOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Field<T>, A : ExponentialOperations<T> = algebra {
    // create the function value and derivatives
    val function = bufferFactory(1 + order) { zero }
    function[0] = ln(operand[operandOffset])

    if (order > 0) {
        val inv = one / operand[operandOffset]
        var xk = inv
        for (i in 1..order) {
            function[i] = xk
            xk *= (-i * inv)
        }
    }

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Compute integer power of a derivative structure.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param n power to apply.
 * @param result array where result must be stored (for power the result array *cannot* be the input array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.pow(
    operand: Buffer<T>,
    operandOffset: Int,
    n: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Field<T>, A : PowerOperations<T> = algebra {
    if (n == 0) {
        // special case, x^0 = 1 for all x
        result[resultOffset] = one
        result.fill(zero, resultOffset + 1, resultOffset + size)
        return
    }

    // create the power function value and derivatives
    // [x^n, nx^(n-1), n(n-1)x^(n-2), ... ]
    val function = bufferFactory(1 + order) { zero }

    if (n > 0) {
        // strictly positive power
        val maxOrder: Int = min(order, n)
        var xk = operand[operandOffset] pow n - maxOrder
        for (i in maxOrder downTo 1) {
            function[i] = xk
            xk *= operand[operandOffset]
        }
        function[0] = xk
    } else {
        // strictly negative power
        val inv = one / operand[operandOffset]
        var xk = inv pow -n

        for (i in 0..order) {
            function[i] = xk
            xk *= inv
        }
    }

    var coefficient = number(n)

    for (i in 1..order) {
        function[i] = function[i] * coefficient
        coefficient *= (n - i).toDouble()
    }

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Compute exponential of a derivative structure.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param result array where result must be stored (for exponential the result array *cannot* be the input array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.exp(
    operand: Buffer<T>,
    operandOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Ring<T>, A : ScaleOperations<T>, A : ExponentialOperations<T> = algebra {
    // create the function value and derivatives
    val function = bufferFactory(1 + order) { zero }
    function.fill(exp(operand[operandOffset]))

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Compute square root of a derivative structure.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param result array where result must be stored (for n<sup>th</sup> root the result array *cannot* be the input
 * array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.sqrt(
    operand: Buffer<T>,
    operandOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Field<T>, A : PowerOperations<T> = algebra {
    // create the function value and derivatives
    // [x^(1/n), (1/n)x^((1/n)-1), (1-n)/n^2x^((1/n)-2), ... ]
    val function = bufferFactory(1 + order) { zero }
    function[0] = sqrt(operand[operandOffset])
    var xk: T = 0.5 * one / function[0]
    val xReciprocal = one / operand[operandOffset]

    for (i in 1..order) {
        function[i] = xk
        xk *= xReciprocal * (0.5 - i)
    }

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Compute cosine of a derivative structure.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param result array where result must be stored (for cosine the result array *cannot* be the input array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.cos(
    operand: Buffer<T>,
    operandOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Ring<T>, A : TrigonometricOperations<T>, A : ScaleOperations<T> = algebra {
    // create the function value and derivatives
    val function = bufferFactory(1 + order) { zero }
    function[0] = cos(operand[operandOffset])

    if (order > 0) {
        function[1] = -sin(operand[operandOffset])
        for (i in 2..order) {
            function[i] = -function[i - 2]
        }
    }

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Compute power of a derivative structure.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param p power to apply.
 * @param result array where result must be stored (for power the result array *cannot* be the input array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.pow(
    operand: Buffer<T>,
    operandOffset: Int,
    p: Double,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Ring<T>, A : NumericAlgebra<T>, A : PowerOperations<T>, A : ScaleOperations<T> = algebra {
    // create the function value and derivatives
    // [x^p, px^(p-1), p(p-1)x^(p-2), ... ]
    val function = bufferFactory(1 + order) { zero }
    var xk = operand[operandOffset] pow p - order

    for (i in order downTo 1) {
        function[i] = xk
        xk *= operand[operandOffset]
    }

    function[0] = xk
    var coefficient = p

    for (i in 1..order) {
        function[i] = function[i] * coefficient
        coefficient *= p - i
    }

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Compute tangent of a derivative structure.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param result array where result must be stored (for tangent the result array *cannot* be the input array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.tan(
    operand: Buffer<T>,
    operandOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Ring<T>, A : TrigonometricOperations<T>, A : ScaleOperations<T> = algebra {
    // create the function value and derivatives
    val function = bufferFactory(1 + order) { zero }
    val t = tan(operand[operandOffset])
    function[0] = t

    if (order > 0) {

        // the nth order derivative of tan has the form:
        // dn(tan(x)/dxn = P_n(tan(x))
        // where P_n(t) is a degree n+1 polynomial with same parity as n+1
        // P_0(t) = t, P_1(t) = 1 + t^2, P_2(t) = 2 t (1 + t^2) ...
        // the general recurrence relation for P_n is:
        // P_n(x) = (1+t^2) P_(n-1)'(t)
        // as per polynomial parity, we can store coefficients of both P_(n-1) and P_n in the same array
        val p = bufferFactory(order + 2) { zero }
        p[1] = one
        val t2 = t * t
        for (n in 1..order) {

            // update and evaluate polynomial P_n(t)
            var v = one
            p[n + 1] = n * p[n]
            var k = n + 1
            while (k >= 0) {
                v = v * t2 + p[k]
                if (k > 2) {
                    p[k - 2] = (k - 1) * p[k - 1] + (k - 3) * p[k - 3]
                } else if (k == 2) {
                    p[0] = p[1]
                }
                k -= 2
            }
            if (n and 0x1 == 0) {
                v *= t
            }
            function[n] = v
        }
    }

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Compute power of a derivative structure.
 *
 * @param x array holding the base.
 * @param xOffset offset of the base in its array.
 * @param y array holding the exponent.
 * @param yOffset offset of the exponent in its array.
 * @param result array where result must be stored (for power the result array *cannot* be the input array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.pow(
    x: Buffer<T>,
    xOffset: Int,
    y: Buffer<T>,
    yOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Field<T>, A : ExponentialOperations<T> = algebra {
    val logX = bufferFactory(size) { zero }
    ln(x, xOffset, logX, 0)
    val yLogX = bufferFactory(size) { zero }
    multiply(logX, 0, y, yOffset, yLogX, 0)
    exp(yLogX, 0, result, resultOffset)
}

/**
 * Compute sine of a derivative structure.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param result array where result must be stored (for sine the result array *cannot* be the input array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.sin(
    operand: Buffer<T>,
    operandOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Ring<T>, A : ScaleOperations<T>, A : TrigonometricOperations<T> = algebra {
    // create the function value and derivatives
    val function = bufferFactory(1 + order) { zero }
    function[0] = sin(operand[operandOffset])
    if (order > 0) {
        function[1] = cos(operand[operandOffset])
        for (i in 2..order) {
            function[i] = -function[i - 2]
        }
    }

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Compute arc cosine of a derivative structure.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param result array where result must be stored (for arc cosine the result array *cannot* be the input array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.acos(
    operand: Buffer<T>,
    operandOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Field<T>, A : TrigonometricOperations<T>, A : PowerOperations<T> = algebra {
    // create the function value and derivatives
    val function = bufferFactory(1 + order) { zero }
    val x = operand[operandOffset]
    function[0] = acos(x)
    if (order > 0) {
        // the nth order derivative of acos has the form:
        // dn(acos(x)/dxn = P_n(x) / [1 - x^2]^((2n-1)/2)
        // where P_n(x) is a degree n-1 polynomial with same parity as n-1
        // P_1(x) = -1, P_2(x) = -x, P_3(x) = -2x^2 - 1 ...
        // the general recurrence relation for P_n is:
        // P_n(x) = (1-x^2) P_(n-1)'(x) + (2n-3) x P_(n-1)(x)
        // as per polynomial parity, we can store coefficients of both P_(n-1) and P_n in the same array
        val p = bufferFactory(order) { zero }
        p[0] = -one
        val x2 = x * x
        val f = one / (one - x2)
        var coeff = sqrt(f)
        function[1] = coeff * p[0]

        for (n in 2..order) {
            // update and evaluate polynomial P_n(x)
            var v = zero
            p[n - 1] = (n - 1) * p[n - 2]
            var k = n - 1

            while (k >= 0) {
                v = v * x2 + p[k]
                if (k > 2) {
                    p[k - 2] = (k - 1) * p[k - 1] + (2 * n - k) * p[k - 3]
                } else if (k == 2) {
                    p[0] = p[1]
                }
                k -= 2
            }

            if (n and 0x1 == 0) {
                v *= x
            }

            coeff *= f
            function[n] = coeff * v
        }
    }

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Compute arc sine of a derivative structure.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param result array where result must be stored (for arc sine the result array *cannot* be the input array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.asin(
    operand: Buffer<T>,
    operandOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Field<T>, A : TrigonometricOperations<T>, A : PowerOperations<T> = algebra {
    // create the function value and derivatives
    val function = bufferFactory(1 + order) { zero }
    val x = operand[operandOffset]
    function[0] = asin(x)
    if (order > 0) {
        // the nth order derivative of asin has the form:
        // dn(asin(x)/dxn = P_n(x) / [1 - x^2]^((2n-1)/2)
        // where P_n(x) is a degree n-1 polynomial with same parity as n-1
        // P_1(x) = 1, P_2(x) = x, P_3(x) = 2x^2 + 1 ...
        // the general recurrence relation for P_n is:
        // P_n(x) = (1-x^2) P_(n-1)'(x) + (2n-3) x P_(n-1)(x)
        // as per polynomial parity, we can store coefficients of both P_(n-1) and P_n in the same array
        val p = bufferFactory(order) { zero }
        p[0] = one
        val x2 = x * x
        val f = one / (one - x2)
        var coeff = sqrt(f)
        function[1] = coeff * p[0]
        for (n in 2..order) {

            // update and evaluate polynomial P_n(x)
            var v = zero
            p[n - 1] = (n - 1) * p[n - 2]
            var k = n - 1
            while (k >= 0) {
                v = v * x2 + p[k]
                if (k > 2) {
                    p[k - 2] = (k - 1) * p[k - 1] + (2 * n - k) * p[k - 3]
                } else if (k == 2) {
                    p[0] = p[1]
                }
                k -= 2
            }
            if (n and 0x1 == 0) {
                v *= x
            }
            coeff *= f
            function[n] = coeff * v
        }
    }

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Compute arc tangent of a derivative structure.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param result array where result must be stored (for arc tangent the result array *cannot* be the input array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.atan(
    operand: Buffer<T>,
    operandOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Field<T>, A : TrigonometricOperations<T> = algebra {
    // create the function value and derivatives
    val function = bufferFactory(1 + order) { zero }
    val x = operand[operandOffset]
    function[0] = atan(x)

    if (order > 0) {
        // the nth order derivative of atan has the form:
        // dn(atan(x)/dxn = Q_n(x) / (1 + x^2)^n
        // where Q_n(x) is a degree n-1 polynomial with same parity as n-1
        // Q_1(x) = 1, Q_2(x) = -2x, Q_3(x) = 6x^2 - 2 ...
        // the general recurrence relation for Q_n is:
        // Q_n(x) = (1+x^2) Q_(n-1)'(x) - 2(n-1) x Q_(n-1)(x)
        // as per polynomial parity, we can store coefficients of both Q_(n-1) and Q_n in the same array
        val q = bufferFactory(order) { zero }
        q[0] = one
        val x2 = x * x
        val f = one / (one + x2)
        var coeff = f
        function[1] = coeff * q[0]
        for (n in 2..order) {

            // update and evaluate polynomial Q_n(x)
            var v = zero
            q[n - 1] = -n * q[n - 2]
            var k = n - 1
            while (k >= 0) {
                v = v * x2 + q[k]
                if (k > 2) {
                    q[k - 2] = (k - 1) * q[k - 1] + (k - 1 - 2 * n) * q[k - 3]
                } else if (k == 2) {
                    q[0] = q[1]
                }
                k -= 2
            }
            if (n and 0x1 == 0) {
                v *= x
            }
            coeff *= f
            function[n] = coeff * v
        }
    }

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Compute hyperbolic cosine of a derivative structure.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param result array where result must be stored (for hyperbolic cosine the result array *cannot* be the input array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.cosh(
    operand: Buffer<T>,
    operandOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Ring<T>, A : ScaleOperations<T>, A : ExponentialOperations<T> = algebra {
    // create the function value and derivatives
    val function = bufferFactory(1 + order) { zero }
    function[0] = cosh(operand[operandOffset])

    if (order > 0) {
        function[1] = sinh(operand[operandOffset])
        for (i in 2..order) {
            function[i] = function[i - 2]
        }
    }

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Compute hyperbolic tangent of a derivative structure.
 *
 * @param operand array holding the operand
 * @param operandOffset offset of the operand in its array
 * @param result array where result must be stored (for hyperbolic tangent the result array *cannot* be the input
 * array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.tanh(
    operand: Buffer<T>,
    operandOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Field<T>, A : ExponentialOperations<T> = algebra {
    // create the function value and derivatives
    val function = bufferFactory(1 + order) { zero }
    val t = tanh(operand[operandOffset])
    function[0] = t
    if (order > 0) {

        // the nth order derivative of tanh has the form:
        // dn(tanh(x)/dxn = P_n(tanh(x))
        // where P_n(t) is a degree n+1 polynomial with same parity as n+1
        // P_0(t) = t, P_1(t) = 1 - t^2, P_2(t) = -2 t (1 - t^2) ...
        // the general recurrence relation for P_n is:
        // P_n(x) = (1-t^2) P_(n-1)'(t)
        // as per polynomial parity, we can store coefficients of both P_(n-1) and P_n in the same array
        val p = bufferFactory(order + 2) { zero }
        p[1] = one
        val t2 = t * t
        for (n in 1..order) {

            // update and evaluate polynomial P_n(t)
            var v = zero
            p[n + 1] = -n * p[n]
            var k = n + 1
            while (k >= 0) {
                v = v * t2 + p[k]
                if (k > 2) {
                    p[k - 2] = (k - 1) * p[k - 1] - (k - 3) * p[k - 3]
                } else if (k == 2) {
                    p[0] = p[1]
                }
                k -= 2
            }
            if (n and 0x1 == 0) {
                v *= t
            }
            function[n] = v
        }
    }

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Compute inverse hyperbolic cosine of a derivative structure.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param result array where result must be stored (for inverse hyperbolic cosine the result array *cannot* be the input
 * array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.acosh(
    operand: Buffer<T>,
    operandOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Field<T>, A : ExponentialOperations<T>, A : PowerOperations<T> = algebra {
    // create the function value and derivatives
    val function = bufferFactory(1 + order) { zero }
    val x = operand[operandOffset]
    function[0] = acosh(x)

    if (order > 0) {
        // the nth order derivative of acosh has the form:
        // dn(acosh(x)/dxn = P_n(x) / [x^2 - 1]^((2n-1)/2)
        // where P_n(x) is a degree n-1 polynomial with same parity as n-1
        // P_1(x) = 1, P_2(x) = -x, P_3(x) = 2x^2 + 1 ...
        // the general recurrence relation for P_n is:
        // P_n(x) = (x^2-1) P_(n-1)'(x) - (2n-3) x P_(n-1)(x)
        // as per polynomial parity, we can store coefficients of both P_(n-1) and P_n in the same array
        val p = bufferFactory(order) { zero }
        p[0] = one
        val x2 = x * x
        val f = one / (x2 - one)
        var coeff = sqrt(f)
        function[1] = coeff * p[0]
        for (n in 2..order) {

            // update and evaluate polynomial P_n(x)
            var v = zero
            p[n - 1] = (1 - n) * p[n - 2]
            var k = n - 1
            while (k >= 0) {
                v = v * x2 + p[k]
                if (k > 2) {
                    p[k - 2] = (1 - k) * p[k - 1] + (k - 2 * n) * p[k - 3]
                } else if (k == 2) {
                    p[0] = -p[1]
                }
                k -= 2
            }
            if (n and 0x1 == 0) {
                v *= x
            }

            coeff *= f
            function[n] = coeff * v
        }
    }

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Compute composition of a derivative structure by a function.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param f array of value and derivatives of the function at the current point (i.e. at `operand[operandOffset]`).
 * @param result array where result must be stored (for composition the result array *cannot* be the input array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.compose(
    operand: Buffer<T>,
    operandOffset: Int,
    f: Buffer<T>,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Ring<T>, A : ScaleOperations<T> = algebra {
    for (i in compositionIndirection.indices) {
        val mappingI = compositionIndirection[i]
        var r = zero
        for (j in mappingI.indices) {
            val mappingIJ = mappingI[j]
            var product = mappingIJ[0] * f[mappingIJ[1]]
            for (k in 2 until mappingIJ.size) {
                product *= operand[operandOffset + mappingIJ[k]]
            }
            r += product
        }
        result[resultOffset + i] = r
    }
}

/**
 * Compute hyperbolic sine of a derivative structure.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param result array where result must be stored (for hyperbolic sine the result array *cannot* be the input array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.sinh(
    operand: Buffer<T>,
    operandOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Field<T>, A : ExponentialOperations<T> = algebra {
    // create the function value and derivatives
    val function = bufferFactory(1 + order) { zero }
    function[0] = sinh(operand[operandOffset])

    if (order > 0) {
        function[1] = cosh(operand[operandOffset])
        for (i in 2..order) {
            function[i] = function[i - 2]
        }
    }

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Perform division of two derivative structures.
 *
 * @param lhs array holding left-hand side of division.
 * @param lhsOffset offset of the left-hand side in its array.
 * @param rhs array right-hand side of division.
 * @param rhsOffset offset of the right-hand side in its array.
 * @param result array where result must be stored (for division the result array *cannot* be one of the input arrays).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.divide(
    lhs: Buffer<T>,
    lhsOffset: Int,
    rhs: Buffer<T>,
    rhsOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Field<T>, A : PowerOperations<T>, A : ScaleOperations<T> = algebra {
    val reciprocal = bufferFactory(size) { zero }
    pow(rhs, lhsOffset, -1, reciprocal, 0)
    multiply(lhs, lhsOffset, reciprocal, rhsOffset, result, resultOffset)
}

/**
 * Perform multiplication of two derivative structures.
 *
 * @param lhs array holding left-hand side of multiplication.
 * @param lhsOffset offset of the left-hand side in its array.
 * @param rhs array right-hand side of multiplication.
 * @param rhsOffset offset of the right-hand side in its array.
 * @param result array where result must be stored (for multiplication the result array *cannot* be one of the input
 * arrays).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.multiply(
    lhs: Buffer<T>,
    lhsOffset: Int,
    rhs: Buffer<T>,
    rhsOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Ring<T>, A : ScaleOperations<T> = algebra {
    for (i in multIndirection.indices) {
        val mappingI = multIndirection[i]
        var r = zero

        for (j in mappingI.indices) {
            r += mappingI[j][0] * lhs[lhsOffset + mappingI[j][1]] * rhs[rhsOffset + mappingI[j][2]]
        }

        result[resultOffset + i] = r
    }
}

/**
 * Perform subtraction of two derivative structures.
 *
 * @param lhs array holding left-hand side of subtraction.
 * @param lhsOffset offset of the left-hand side in its array.
 * @param rhs array right-hand side of subtraction.
 * @param rhsOffset offset of the right-hand side in its array.
 * @param result array where result must be stored (it may be one of the input arrays).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A : Group<T>> DSCompiler<T, A>.subtract(
    lhs: Buffer<T>,
    lhsOffset: Int,
    rhs: Buffer<T>,
    rhsOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) = algebra {
    for (i in 0 until size) {
        result[resultOffset + i] = lhs[lhsOffset + i] - rhs[rhsOffset + i]
    }
}

/**
 * Compute inverse hyperbolic sine of a derivative structure.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param result array where result must be stored (for inverse hyperbolic sine the result array *cannot* be the input
 * array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.asinh(
    operand: Buffer<T>,
    operandOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Field<T>, A : ExponentialOperations<T>, A : PowerOperations<T> = algebra {
    // create the function value and derivatives
    val function = bufferFactory(1 + order) { zero }
    val x = operand[operandOffset]
    function[0] = asinh(x)
    if (order > 0) {
        // the nth order derivative of asinh has the form:
        // dn(asinh(x)/dxn = P_n(x) / [x^2 + 1]^((2n-1)/2)
        // where P_n(x) is a degree n-1 polynomial with same parity as n-1
        // P_1(x) = 1, P_2(x) = -x, P_3(x) = 2x^2 - 1 ...
        // the general recurrence relation for P_n is:
        // P_n(x) = (x^2+1) P_(n-1)'(x) - (2n-3) x P_(n-1)(x)
        // as per polynomial parity, we can store coefficients of both P_(n-1) and P_n in the same array
        val p = bufferFactory(order) { zero }
        p[0] = one
        val x2 = x * x
        val f = one / (one + x2)
        var coeff = sqrt(f)
        function[1] = coeff * p[0]
        for (n in 2..order) {

            // update and evaluate polynomial P_n(x)
            var v = zero
            p[n - 1] = (1 - n) * p[n - 2]
            var k = n - 1
            while (k >= 0) {
                v = v * x2 + p[k]
                if (k > 2) {
                    p[k - 2] = (k - 1) * p[k - 1] + (k - 2 * n) * p[k - 3]
                } else if (k == 2) {
                    p[0] = p[1]
                }
                k -= 2
            }
            if (n and 0x1 == 0) {
                v *= x
            }
            coeff *= f
            function[n] = coeff * v
        }
    }

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Perform addition of two derivative structures.
 *
 * @param lhs array holding left-hand side of addition.
 * @param lhsOffset offset of the left-hand side in its array.
 * @param rhs array right-hand side of addition.
 * @param rhsOffset offset of the right-hand side in its array.
 * @param result array where result must be stored (it may be one of the input arrays).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.add(
    lhs: Buffer<T>,
    lhsOffset: Int,
    rhs: Buffer<T>,
    rhsOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Group<T> = algebra {
    for (i in 0 until size) {
        result[resultOffset + i] = lhs[lhsOffset + i] + rhs[rhsOffset + i]
    }
}

/**
 * Check rules set compatibility.
 *
 * @param compiler other compiler to check against instance.
 */
internal fun <T, A : Algebra<T>> DSCompiler<T, A>.checkCompatibility(compiler: DSCompiler<T, A>) {
    require(freeParameters == compiler.freeParameters) {
        "dimension mismatch: $freeParameters and ${compiler.freeParameters}"
    }
    require(order == compiler.order) {
        "dimension mismatch: $order and ${compiler.order}"
    }
}

/**
 * Compute inverse hyperbolic tangent of a derivative structure.
 *
 * @param operand array holding the operand.
 * @param operandOffset offset of the operand in its array.
 * @param result array where result must be stored (for inverse hyperbolic tangent the result array *cannot* be the
 * input array).
 * @param resultOffset offset of the result in its array.
 */
internal fun <T, A> DSCompiler<T, A>.atanh(
    operand: Buffer<T>,
    operandOffset: Int,
    result: MutableBuffer<T>,
    resultOffset: Int,
) where A : Field<T>, A : ExponentialOperations<T> = algebra {
    // create the function value and derivatives
    val function = bufferFactory(1 + order) { zero }
    val x = operand[operandOffset]
    function[0] = atanh(x)

    if (order > 0) {
        // the nth order derivative of atanh has the form:
        // dn(atanh(x)/dxn = Q_n(x) / (1 - x^2)^n
        // where Q_n(x) is a degree n-1 polynomial with same parity as n-1
        // Q_1(x) = 1, Q_2(x) = 2x, Q_3(x) = 6x^2 + 2 ...
        // the general recurrence relation for Q_n is:
        // Q_n(x) = (1-x^2) Q_(n-1)'(x) + 2(n-1) x Q_(n-1)(x)
        // as per polynomial parity, we can store coefficients of both Q_(n-1) and Q_n in the same array
        val q = bufferFactory(order) { zero }
        q[0] = one
        val x2 = x * x
        val f = one / (one - x2)
        var coeff = f
        function[1] = coeff * q[0]
        for (n in 2..order) {

            // update and evaluate polynomial Q_n(x)
            var v = zero
            q[n - 1] = n * q[n - 2]
            var k = n - 1
            while (k >= 0) {
                v = v * x2 + q[k]
                if (k > 2) {
                    q[k - 2] = (k - 1) * q[k - 1] + (2 * n - k + 1) * q[k - 3]
                } else if (k == 2) {
                    q[0] = q[1]
                }
                k -= 2
            }
            if (n and 0x1 == 0) {
                v *= x
            }
            coeff *= f
            function[n] = coeff * v
        }
    }

    // apply function composition
    compose(operand, operandOffset, function, result, resultOffset)
}

/**
 * Compile the sizes array.
 *
 * @param parameters number of free parameters.
 * @param order derivation order.
 * @param valueCompiler compiler for the value part.
 * @return sizes array.
 */
private fun <T, A : Algebra<T>> compileSizes(
    parameters: Int, order: Int,
    valueCompiler: DSCompiler<T, A>?,
): Array<IntArray> {
    val sizes = Array(parameters + 1) {
        IntArray(order + 1)
    }

    if (parameters == 0) {
        sizes[0].fill(1)
    } else {
        checkNotNull(valueCompiler)
        valueCompiler.sizes.copyInto(sizes, endIndex = parameters)
        sizes[parameters][0] = 1
        for (i in 0 until order) {
            sizes[parameters][i + 1] = sizes[parameters][i] + sizes[parameters - 1][i + 1]
        }
    }
    return sizes
}

/**
 * Compile the derivatives' indirection array.
 *
 * @param parameters number of free parameters.
 * @param order derivation order.
 * @param valueCompiler compiler for the value part.
 * @param derivativeCompiler compiler for the derivative part.
 * @return derivatives indirection array.
 */
private fun <T, A : Algebra<T>> compileDerivativesIndirection(
    parameters: Int,
    order: Int,
    valueCompiler: DSCompiler<T, A>?,
    derivativeCompiler: DSCompiler<T, A>?,
): Array<IntArray> {
    if (parameters == 0 || order == 0) {
        return Array(1) { IntArray(parameters) }
    }

    val vSize: Int = valueCompiler!!.derivativesIndirection.size
    val dSize: Int = derivativeCompiler!!.derivativesIndirection.size
    val derivativesIndirection = Array(vSize + dSize) { IntArray(parameters) }

    // set up the indices for the value part
    for (i in 0 until vSize) {
        // copy the first indices, the last one remaining set to 0
        valueCompiler.derivativesIndirection[i].copyInto(derivativesIndirection[i], endIndex = parameters - 1)
    }

    // set up the indices for the derivative part
    for (i in 0 until dSize) {
        // copy the indices
        derivativeCompiler.derivativesIndirection[i].copyInto(derivativesIndirection[vSize], 0, 0, parameters)

        // increment the derivation order for the last parameter
        derivativesIndirection[vSize + i][parameters - 1]++
    }

    return derivativesIndirection
}

/**
 * Compile the lower derivatives' indirection array.
 *
 * This indirection array contains the indices of all elements except derivatives for last derivation order.
 *
 * @param parameters number of free parameters.
 * @param order derivation order.
 * @param valueCompiler compiler for the value part.
 * @param derivativeCompiler compiler for the derivative part.
 * @return lower derivatives' indirection array.
 */
private fun <T, A : Algebra<T>> compileLowerIndirection(
    parameters: Int,
    order: Int,
    valueCompiler: DSCompiler<T, A>?,
    derivativeCompiler: DSCompiler<T, A>?,
): IntArray {
    if (parameters == 0 || order <= 1) return intArrayOf(0)
    checkNotNull(valueCompiler)
    checkNotNull(derivativeCompiler)

    // this is an implementation of definition 6 in Dan Kalman's paper.
    val vSize: Int = valueCompiler.lowerIndirection.size
    val dSize: Int = derivativeCompiler.lowerIndirection.size
    val lowerIndirection = IntArray(vSize + dSize)
    valueCompiler.lowerIndirection.copyInto(lowerIndirection, endIndex = vSize)
    for (i in 0 until dSize) {
        lowerIndirection[vSize + i] = valueCompiler.size + derivativeCompiler.lowerIndirection[i]
    }
    return lowerIndirection
}

/**
 * Compile the multiplication indirection array.
 *
 * This indirection array contains the indices of all pairs of elements involved when computing a multiplication. This
 * allows a straightforward loop-based multiplication (see [multiply]).
 *
 * @param parameters number of free parameters.
 * @param order derivation order.
 * @param valueCompiler compiler for the value part.
 * @param derivativeCompiler compiler for the derivative part.
 * @param lowerIndirection lower derivatives' indirection array.
 * @return multiplication indirection array.
 */
@Suppress("UNCHECKED_CAST")
private fun <T, A : Algebra<T>> compileMultiplicationIndirection(
    parameters: Int,
    order: Int,
    valueCompiler: DSCompiler<T, A>?,
    derivativeCompiler: DSCompiler<T, A>?,
    lowerIndirection: IntArray,
): Array<Array<IntArray>> {
    if (parameters == 0 || order == 0) return arrayOf(arrayOf(intArrayOf(1, 0, 0)))

    // this is an implementation of definition 3 in Dan Kalman's paper.
    val vSize = valueCompiler!!.multIndirection.size
    val dSize = derivativeCompiler!!.multIndirection.size
    val multIndirection: Array<Array<IntArray>?> = arrayOfNulls(vSize + dSize)
    valueCompiler.multIndirection.copyInto(multIndirection, endIndex = vSize)

    for (i in 0 until dSize) {
        val dRow = derivativeCompiler.multIndirection[i]
        val row: List<IntArray> = buildList(dRow.size * 2) {
            for (j in dRow.indices) {
                add(intArrayOf(dRow[j][0], lowerIndirection[dRow[j][1]], vSize + dRow[j][2]))
                add(intArrayOf(dRow[j][0], vSize + dRow[j][1], lowerIndirection[dRow[j][2]]))
            }
        }

        // combine terms with similar derivation orders
        val combined: List<IntArray> = buildList(row.size) {
            for (j in row.indices) {
                val termJ = row[j]
                if (termJ[0] > 0) {
                    for (k in j + 1 until row.size) {
                        val termK = row[k]

                        if (termJ[1] == termK[1] && termJ[2] == termK[2]) {
                            // combine termJ and termK
                            termJ[0] += termK[0]
                            // make sure we will skip termK later on in the outer loop
                            termK[0] = 0
                        }
                    }

                    add(termJ)
                }
            }
        }

        multIndirection[vSize + i] = combined.toTypedArray()
    }

    return multIndirection as Array<Array<IntArray>>
}

/**
 * Compile the indirection array of function composition.
 *
 * This indirection array contains the indices of all sets of elements involved when computing a composition. This
 * allows a straightforward loop-based composition (see [compose]).
 *
 * @param parameters number of free parameters.
 * @param order derivation order.
 * @param valueCompiler compiler for the value part.
 * @param derivativeCompiler compiler for the derivative part.
 * @param sizes sizes array.
 * @param derivativesIndirection derivatives indirection array.
 * @return multiplication indirection array.
 */
@Suppress("UNCHECKED_CAST")
private fun <T, A : Algebra<T>> compileCompositionIndirection(
    parameters: Int,
    order: Int,
    valueCompiler: DSCompiler<T, A>?,
    derivativeCompiler: DSCompiler<T, A>?,
    sizes: Array<IntArray>,
    derivativesIndirection: Array<IntArray>,
): Array<Array<IntArray>> {
    if (parameters == 0 || order == 0) {
        return arrayOf(arrayOf(intArrayOf(1, 0)))
    }

    val vSize = valueCompiler!!.compositionIndirection.size
    val dSize = derivativeCompiler!!.compositionIndirection.size
    val compIndirection: Array<Array<IntArray>?> = arrayOfNulls(vSize + dSize)

    // the composition rules from the value part can be reused as is
    valueCompiler.compositionIndirection.copyInto(compIndirection, endIndex = vSize)

    // the composition rules for the derivative part are deduced by differentiation the rules from the
    // underlying compiler once  with respect to the parameter this compiler handles and the  underlying one
    // did not handle

    // the composition rules for the derivative part are deduced by differentiation the rules from the
    // underlying compiler once  with respect to the parameter this compiler handles and the underlying one did
    // not handle
    for (i in 0 until dSize) {
        val row: List<IntArray> = buildList {
            for (term in derivativeCompiler.compositionIndirection[i]) {

                // handle term p * f_k(g(x)) * g_l1(x) * g_l2(x) * ... * g_lp(x)

                // derive the first factor in the term: f_k with respect to new parameter
                val derivedTermF = IntArray(term.size + 1)
                derivedTermF[0] = term[0] // p
                derivedTermF[1] = term[1] + 1 // f_(k+1)
                val orders = IntArray(parameters)
                orders[parameters - 1] = 1
                derivedTermF[term.size] = getPartialDerivativeIndex(
                    parameters,
                    order,
                    sizes,
                    *orders
                ) // g_1

                for (j in 2 until term.size) {
                    // convert the indices as the mapping for the current order is different from the mapping with one
                    // less order
                    derivedTermF[j] = convertIndex(
                        term[j], parameters,
                        derivativeCompiler.derivativesIndirection,
                        parameters, order, sizes
                    )
                }

                derivedTermF.sort(2, derivedTermF.size)
                add(derivedTermF)

                // derive the various g_l
                for (l in 2 until term.size) {
                    val derivedTermG = IntArray(term.size)
                    derivedTermG[0] = term[0]
                    derivedTermG[1] = term[1]

                    for (j in 2 until term.size) {
                        // convert the indices as the mapping for the current order
                        // is different from the mapping with one less order
                        derivedTermG[j] = convertIndex(
                            term[j],
                            parameters,
                            derivativeCompiler.derivativesIndirection,
                            parameters,
                            order,
                            sizes,
                        )

                        if (j == l) {
                            // derive this term
                            derivativesIndirection[derivedTermG[j]].copyInto(orders, endIndex = parameters)
                            orders[parameters - 1]++

                            derivedTermG[j] = getPartialDerivativeIndex(
                                parameters,
                                order,
                                sizes,
                                *orders,
                            )
                        }
                    }

                    derivedTermG.sort(2, derivedTermG.size)
                    add(derivedTermG)
                }
            }
        }

        // combine terms with similar derivation orders
        val combined: List<IntArray> = buildList(row.size) {
            for (j in row.indices) {
                val termJ = row[j]

                if (termJ[0] > 0) {
                    (j + 1 until row.size).map { k -> row[k] }.forEach { termK ->
                        var equals = termJ.size == termK.size
                        var l = 1

                        while (equals && l < termJ.size) {
                            equals = equals and (termJ[l] == termK[l])
                            ++l
                        }

                        if (equals) {
                            // combine termJ and termK
                            termJ[0] += termK[0]
                            // make sure we will skip termK later on in the outer loop
                            termK[0] = 0
                        }
                    }

                    add(termJ)
                }
            }
        }

        compIndirection[vSize + i] = combined.toTypedArray()
    }

    return compIndirection as Array<Array<IntArray>>
}

/**
 * Get the index of a partial derivative in an array.
 *
 * @param parameters number of free parameters.
 * @param order derivation order.
 * @param sizes sizes array.
 * @param orders derivation orders with respect to each parameter (the length of this array must match the number of
 * parameters).
 * @return index of the partial derivative.
 */
private fun getPartialDerivativeIndex(
    parameters: Int,
    order: Int,
    sizes: Array<IntArray>,
    vararg orders: Int,
): Int {

    // the value is obtained by diving into the recursive Dan Kalman's structure
    // this is theorem 2 of his paper, with recursion replaced by iteration
    var index = 0
    var m = order
    var ordersSum = 0

    for (i in parameters - 1 downTo 0) {
        // derivative order for current free parameter
        var derivativeOrder = orders[i]

        // safety check
        ordersSum += derivativeOrder
        require(ordersSum <= order) { "number is too large: $ordersSum > $order" }

        while (derivativeOrder-- > 0) {
            // as long as we differentiate according to current free parameter,
            // we have to skip the value part and dive into the derivative part,
            // so we add the size of the value part to the base index
            index += sizes[i][m--]
        }
    }

    return index
}

/**
 * Convert an index from one (parameters, order) structure to another.
 *
 * @param index index of a partial derivative in source derivative structure.
 * @param srcP number of free parameters in source derivative structure.
 * @param srcDerivativesIndirection derivatives indirection array for the source derivative structure.
 * @param destP number of free parameters in destination derivative structure.
 * @param destO derivation order in destination derivative structure.
 * @param destSizes sizes array for the destination derivative structure.
 * @return index of the partial derivative with the *same* characteristics in destination derivative structure.
 */
private fun convertIndex(
    index: Int,
    srcP: Int,
    srcDerivativesIndirection: Array<IntArray>,
    destP: Int,
    destO: Int,
    destSizes: Array<IntArray>,
): Int {
    val orders = IntArray(destP)
    srcDerivativesIndirection[index].copyInto(orders, endIndex = min(srcP, destP))
    return getPartialDerivativeIndex(destP, destO, destSizes, *orders)
}
