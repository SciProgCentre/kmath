package scientifik.kmath.operations

/**
 * A container for trigonometric operations for specific type.
 *
 * @param T the type of element of this structure.
 */
interface TrigonometricOperations<T> : Algebra<T> {
    /**
     * Computes the sine of [arg].
     */
    fun sin(arg: T): T

    /**
     * Computes the cosine of [arg].
     */
    fun cos(arg: T): T

    /**
     * Computes the tangent of [arg].
     */
    fun tan(arg: T): T

    /**
     * Computes the inverse sine of [arg].
     */
    fun asin(arg: T): T

    /**
     * Computes the inverse cosine of [arg].
     */
    fun acos(arg: T): T

    /**
     * Computes the inverse tangent of [arg].
     */
    fun atan(arg: T): T

    companion object {
        /**
         * The identifier of sine.
         */
        const val SIN_OPERATION: String = "sin"

        /**
         * The identifier of cosine.
         */
        const val COS_OPERATION: String = "cos"

        /**
         * The identifier of tangent.
         */
        const val TAN_OPERATION: String = "tan"

        /**
         * The identifier of inverse sine.
         */
        const val ASIN_OPERATION: String = "asin"

        /**
         * The identifier of inverse cosine.
         */
        const val ACOS_OPERATION: String = "acos"

        /**
         * The identifier of inverse tangent.
         */
        const val ATAN_OPERATION: String = "atan"
    }
}

/**
 * Computes the sine of [arg].
 */
fun <T : MathElement<out TrigonometricOperations<T>>> sin(arg: T): T = arg.context.sin(arg)

/**
 * Computes the cosine of [arg].
 */
fun <T : MathElement<out TrigonometricOperations<T>>> cos(arg: T): T = arg.context.cos(arg)

/**
 * Computes the tangent of [arg].
 */
fun <T : MathElement<out TrigonometricOperations<T>>> tan(arg: T): T = arg.context.tan(arg)

/**
 * Computes the inverse sine of [arg].
 */
fun <T : MathElement<out TrigonometricOperations<T>>> asin(arg: T): T = arg.context.asin(arg)

/**
 * Computes the inverse cosine of [arg].
 */
fun <T : MathElement<out TrigonometricOperations<T>>> acos(arg: T): T = arg.context.acos(arg)

/**
 * Computes the inverse tangent of [arg].
 */
fun <T : MathElement<out TrigonometricOperations<T>>> atan(arg: T): T = arg.context.atan(arg)

/**
 * A container for hyperbolic trigonometric operations for specific type.
 *
 * @param T the type of element of this structure.
 */
interface HyperbolicOperations<T> : Algebra<T> {
    /**
     * Computes the hyperbolic sine of [arg].
     */
    fun sinh(arg: T): T

    /**
     * Computes the hyperbolic cosine of [arg].
     */
    fun cosh(arg: T): T

    /**
     * Computes the hyperbolic tangent of [arg].
     */
    fun tanh(arg: T): T

    /**
     * Computes the inverse hyperbolic sine of [arg].
     */
    fun asinh(arg: T): T

    /**
     * Computes the inverse hyperbolic cosine of [arg].
     */
    fun acosh(arg: T): T

    /**
     * Computes the inverse hyperbolic tangent of [arg].
     */
    fun atanh(arg: T): T

    companion object {
        /**
         * The identifier of hyperbolic sine.
         */
        const val SINH_OPERATION: String = "sinh"

        /**
         * The identifier of hyperbolic cosine.
         */
        const val COSH_OPERATION: String = "cosh"

        /**
         * The identifier of hyperbolic tangent.
         */
        const val TANH_OPERATION: String = "tanh"

        /**
         * The identifier of inverse hyperbolic sine.
         */
        const val ASINH_OPERATION: String = "asinh"

        /**
         * The identifier of inverse hyperbolic cosine.
         */
        const val ACOSH_OPERATION: String = "acosh"

        /**
         * The identifier of inverse hyperbolic tangent.
         */
        const val ATANH_OPERATION: String = "atanh"
    }
}

/**
 * Computes the hyperbolic sine of [arg].
 */
fun <T : MathElement<out HyperbolicOperations<T>>> sinh(arg: T): T = arg.context.sinh(arg)

/**
 * Computes the hyperbolic cosine of [arg].
 */
fun <T : MathElement<out HyperbolicOperations<T>>> cosh(arg: T): T = arg.context.cosh(arg)

/**
 * Computes the hyperbolic tangent of [arg].
 */
fun <T : MathElement<out HyperbolicOperations<T>>> tanh(arg: T): T = arg.context.tanh(arg)

/**
 * Computes the inverse hyperbolic sine of [arg].
 */
fun <T : MathElement<out HyperbolicOperations<T>>> asinh(arg: T): T = arg.context.asinh(arg)

/**
 * Computes the inverse hyperbolic cosine of [arg].
 */
fun <T : MathElement<out HyperbolicOperations<T>>> acosh(arg: T): T = arg.context.acosh(arg)

/**
 * Computes the inverse hyperbolic tangent of [arg].
 */
fun <T : MathElement<out HyperbolicOperations<T>>> atanh(arg: T): T = arg.context.atanh(arg)

/**
 * A context extension to include power operations based on exponentiation.
 *
 * @param T the type of element of this structure.
 */
interface PowerOperations<T> : Algebra<T> {
    /**
     * Raises [arg] to the power [pow].
     */
    fun power(arg: T, pow: Number): T

    /**
     * Computes the square root of the value [arg].
     */
    fun sqrt(arg: T): T = power(arg, 0.5)

    /**
     * Raises this value to the power [pow].
     */
    infix fun T.pow(pow: Number): T = power(this, pow)

    companion object {
        /**
         * The identifier of exponentiation.
         */
        const val POW_OPERATION: String = "pow"

        /**
         * The identifier of square root.
         */
        const val SQRT_OPERATION: String = "sqrt"
    }
}

/**
 * Raises this element to the power [pow].
 *
 * @receiver the base.
 * @param power the exponent.
 * @return the base raised to the power.
 */
infix fun <T : MathElement<out PowerOperations<T>>> T.pow(power: Double): T = context.power(this, power)

/**
 * Computes the square root of the value [arg].
 */
fun <T : MathElement<out PowerOperations<T>>> sqrt(arg: T): T = arg pow 0.5

/**
 * Computes the square of the value [arg].
 */
fun <T : MathElement<out PowerOperations<T>>> sqr(arg: T): T = arg pow 2.0

/**
 * A container for operations related to `exp` and `ln` functions.
 *
 * @param T the type of element of this structure.
 */
interface ExponentialOperations<T> : Algebra<T> {
    /**
     * Computes Euler's number `e` raised to the power of the value [arg].
     */
    fun exp(arg: T): T

    /**
     * Computes the natural logarithm (base `e`) of the value [arg].
     */
    fun ln(arg: T): T

    companion object {
        /**
         * The identifier of exponential function.
         */
        const val EXP_OPERATION: String = "exp"

        /**
         * The identifier of natural logarithm.
         */
        const val LN_OPERATION: String = "ln"
    }
}

/**
 * The identifier of exponential function.
 */
fun <T : MathElement<out ExponentialOperations<T>>> exp(arg: T): T = arg.context.exp(arg)

/**
 * The identifier of natural logarithm.
 */
fun <T : MathElement<out ExponentialOperations<T>>> ln(arg: T): T = arg.context.ln(arg)

/**
 * A container for norm functional on element.
 *
 * @param T the type of element having norm defined.
 * @param R the type of norm.
 */
interface Norm<in T : Any, out R> {
    /**
     * Computes the norm of [arg] (i.e. absolute value or vector length).
     */
    fun norm(arg: T): R
}

/**
 * Computes the norm of [arg] (i.e. absolute value or vector length).
 */
fun <T : MathElement<out Norm<T, R>>, R> norm(arg: T): R = arg.context.norm(arg)
