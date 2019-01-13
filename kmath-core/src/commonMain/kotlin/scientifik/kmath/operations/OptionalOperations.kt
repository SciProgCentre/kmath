package scientifik.kmath.operations


/* Trigonometric operations */

/**
 * A container for trigonometric operations for specific type. Trigonometric operations are limited to fields.
 *
 * The operations are not exposed to class directly to avoid method bloat but instead are declared in the field.
 * It also allows to override behavior for optional operations
 *
 */
interface TrigonometricOperations<T> : Field<T> {
    fun sin(arg: T): T
    fun cos(arg: T): T

    fun tg(arg: T): T = sin(arg) / cos(arg)

    fun ctg(arg: T): T = cos(arg) / sin(arg)
}

fun <T : MathElement<out TrigonometricOperations<T>>> sin(arg: T): T = arg.context.sin(arg)
fun <T : MathElement<out TrigonometricOperations<T>>> cos(arg: T): T = arg.context.cos(arg)
fun <T : MathElement<out TrigonometricOperations<T>>> tg(arg: T): T = arg.context.tg(arg)
fun <T : MathElement<out TrigonometricOperations<T>>> ctg(arg: T): T = arg.context.ctg(arg)

/* Power and roots */

/**
 * A context extension to include power operations like square roots, etc
 */
interface PowerOperations<T> {
    fun power(arg: T, pow: Number): T
    fun sqrt(arg: T) = power(arg, 0.5)
}

infix fun <T : MathElement<out PowerOperations<T>>> T.pow(power: Double): T = context.power(this, power)
fun <T : MathElement<out PowerOperations<T>>> sqrt(arg: T): T = arg pow 0.5
fun <T : MathElement<out PowerOperations<T>>> sqr(arg: T): T = arg pow 2.0

/* Exponential */

interface ExponentialOperations<T> {
    fun exp(arg: T): T
    fun ln(arg: T): T
}

fun <T : MathElement<out ExponentialOperations<T>>> exp(arg: T): T = arg.context.exp(arg)
fun <T : MathElement<out ExponentialOperations<T>>> ln(arg: T): T = arg.context.ln(arg)

interface Norm<in T: Any, out R> {
    fun norm(arg: T): R
}

fun <T : MathElement<out Norm<T, R>>, R> norm(arg: T): R = arg.context.norm(arg)