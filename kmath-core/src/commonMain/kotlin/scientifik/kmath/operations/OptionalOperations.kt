package scientifik.kmath.operations

/**
 * A container for trigonometric operations for specific type. Trigonometric operations are limited to fields.
 *
 * The operations are not exposed to class directly to avoid method bloat but instead are declared in the field.
 * It also allows to override behavior for optional operations.
 */
interface TrigonometricOperations<T> : FieldOperations<T> {
    /**
     * Computes the sine of [arg] .
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
        const val SIN_OPERATION = "sin"
        const val COS_OPERATION = "cos"
        const val TAN_OPERATION = "tan"
        const val ASIN_OPERATION = "asin"
        const val ACOS_OPERATION = "acos"
        const val ATAN_OPERATION = "atan"
    }
}

fun <T : MathElement<out TrigonometricOperations<T>>> sin(arg: T): T = arg.context.sin(arg)
fun <T : MathElement<out TrigonometricOperations<T>>> cos(arg: T): T = arg.context.cos(arg)
fun <T : MathElement<out TrigonometricOperations<T>>> tan(arg: T): T = arg.context.tan(arg)
fun <T : MathElement<out TrigonometricOperations<T>>> asin(arg: T): T = arg.context.asin(arg)
fun <T : MathElement<out TrigonometricOperations<T>>> acos(arg: T): T = arg.context.acos(arg)
fun <T : MathElement<out TrigonometricOperations<T>>> atan(arg: T): T = arg.context.atan(arg)

/**
 * A container for hyperbolic trigonometric operations for specific type. Trigonometric operations are limited to
 * fields.
 *
 * The operations are not exposed to class directly to avoid method bloat but instead are declared in the field. It
 * also allows to override behavior for optional operations.
 */
interface HyperbolicTrigonometricOperations<T> : FieldOperations<T> {
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
        const val SINH_OPERATION = "sinh"
        const val COSH_OPERATION = "cosh"
        const val TANH_OPERATION = "tanh"
        const val ASINH_OPERATION = "asinh"
        const val ACOSH_OPERATION = "acosh"
        const val ATANH_OPERATION = "atanh"
    }
}

fun <T : MathElement<out HyperbolicTrigonometricOperations<T>>> sinh(arg: T): T = arg.context.sinh(arg)
fun <T : MathElement<out HyperbolicTrigonometricOperations<T>>> cosh(arg: T): T = arg.context.cosh(arg)
fun <T : MathElement<out HyperbolicTrigonometricOperations<T>>> tanh(arg: T): T = arg.context.tanh(arg)
fun <T : MathElement<out HyperbolicTrigonometricOperations<T>>> asinh(arg: T): T = arg.context.asinh(arg)
fun <T : MathElement<out HyperbolicTrigonometricOperations<T>>> acosh(arg: T): T = arg.context.acosh(arg)
fun <T : MathElement<out HyperbolicTrigonometricOperations<T>>> atanh(arg: T): T = arg.context.atanh(arg)

/**
 * A context extension to include power operations like square roots, etc
 */
interface PowerOperations<T> : Algebra<T> {
    fun power(arg: T, pow: Number): T
    fun sqrt(arg: T) = power(arg, 0.5)

    infix fun T.pow(pow: Number) = power(this, pow)

    companion object {
        const val POW_OPERATION = "pow"
        const val SQRT_OPERATION = "sqrt"
    }
}

infix fun <T : MathElement<out PowerOperations<T>>> T.pow(power: Double): T = context.power(this, power)
fun <T : MathElement<out PowerOperations<T>>> sqrt(arg: T): T = arg pow 0.5
fun <T : MathElement<out PowerOperations<T>>> sqr(arg: T): T = arg pow 2.0

interface ExponentialOperations<T> : Algebra<T> {
    fun exp(arg: T): T
    fun ln(arg: T): T

    companion object {
        const val EXP_OPERATION = "exp"
        const val LN_OPERATION = "ln"
    }
}

fun <T : MathElement<out ExponentialOperations<T>>> exp(arg: T): T = arg.context.exp(arg)
fun <T : MathElement<out ExponentialOperations<T>>> ln(arg: T): T = arg.context.ln(arg)

interface Norm<in T : Any, out R> {
    fun norm(arg: T): R
}

fun <T : MathElement<out Norm<T, R>>, R> norm(arg: T): R = arg.context.norm(arg)
