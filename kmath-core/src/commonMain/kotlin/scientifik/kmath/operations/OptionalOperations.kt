package scientifik.kmath.operations


/* Trigonometric operations */

/**
 * A container for trigonometric operations for specific type. Trigonometric operations are limited to fields.
 *
 * The operations are not exposed to class directly to avoid method bloat but instead are declared in the field.
 * It also allows to override behavior for optional operations
 *
 */
interface TrigonometricOperations<T> : FieldOperations<T> {
    fun sin(arg: T): T
    fun cos(arg: T): T
    fun tan(arg: T): T

    companion object {
        const val SIN_OPERATION = "sin"
        const val COS_OPERATION = "cos"
        const val TAN_OPERATION = "tan"
    }
}

interface InverseTrigonometricOperations<T> : TrigonometricOperations<T> {
    fun asin(arg: T): T
    fun acos(arg: T): T
    fun atan(arg: T): T

    companion object {
        const val ASIN_OPERATION = "asin"
        const val ACOS_OPERATION = "acos"
        const val ATAN_OPERATION = "atan"
    }
}

fun <T : MathElement<out TrigonometricOperations<T>>> sin(arg: T): T = arg.context.sin(arg)
fun <T : MathElement<out TrigonometricOperations<T>>> cos(arg: T): T = arg.context.cos(arg)
fun <T : MathElement<out TrigonometricOperations<T>>> tan(arg: T): T = arg.context.tan(arg)
fun <T : MathElement<out InverseTrigonometricOperations<T>>> asin(arg: T): T = arg.context.asin(arg)
fun <T : MathElement<out InverseTrigonometricOperations<T>>> acos(arg: T): T = arg.context.acos(arg)
fun <T : MathElement<out InverseTrigonometricOperations<T>>> atan(arg: T): T = arg.context.atan(arg)

/* Power and roots */

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

/* Exponential */

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
