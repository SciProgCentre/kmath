package scientifik.kmath.operations

import kotlin.math.abs
import kotlin.math.pow as kpow

/**
 * Advanced Number-like semifield that implements basic operations.
 */
interface ExtendedFieldOperations<T> :
    InverseTrigonometricOperations<T>,
    PowerOperations<T>,
    ExponentialOperations<T> {

    override fun tan(arg: T): T = sin(arg) / cos(arg)

    override fun unaryOperation(operation: String, arg: T): T = when (operation) {
        TrigonometricOperations.COS_OPERATION -> cos(arg)
        TrigonometricOperations.SIN_OPERATION -> sin(arg)
        TrigonometricOperations.TAN_OPERATION -> tan(arg)
        InverseTrigonometricOperations.ACOS_OPERATION -> acos(arg)
        InverseTrigonometricOperations.ASIN_OPERATION -> asin(arg)
        InverseTrigonometricOperations.ATAN_OPERATION -> atan(arg)
        PowerOperations.SQRT_OPERATION -> sqrt(arg)
        ExponentialOperations.EXP_OPERATION -> exp(arg)
        ExponentialOperations.LN_OPERATION -> ln(arg)
        else -> super.unaryOperation(operation, arg)
    }
}


/**
 * Advanced Number-like field that implements basic operations.
 */
interface ExtendedField<T> : ExtendedFieldOperations<T>, Field<T> {
    override fun rightSideNumberOperation(operation: String, left: T, right: Number): T = when (operation) {
        PowerOperations.POW_OPERATION -> power(left, right)
        else -> super.rightSideNumberOperation(operation, left, right)
    }
}

/**
 * Real field element wrapping double.
 *
 * @property value the [Double] value wrapped by this [Real].
 *
 * TODO inline does not work due to compiler bug. Waiting for fix for KT-27586
 */
inline class Real(val value: Double) : FieldElement<Double, Real, RealField> {
    override fun unwrap(): Double = value

    override fun Double.wrap(): Real = Real(value)

    override val context: RealField get() = RealField

    companion object
}

/**
 * A field for [Double] without boxing. Does not produce appropriate field element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
object RealField : ExtendedField<Double>, Norm<Double, Double> {
    override val zero: Double = 0.0
    override inline fun add(a: Double, b: Double): Double = a + b
    override inline fun multiply(a: Double, b: Double): Double = a * b
    override inline fun multiply(a: Double, k: Number): Double = a * k.toDouble()

    override val one: Double = 1.0
    override inline fun divide(a: Double, b: Double): Double = a / b

    override inline fun sin(arg: Double): Double = kotlin.math.sin(arg)
    override inline fun cos(arg: Double): Double = kotlin.math.cos(arg)
    override inline fun tan(arg: Double): Double = kotlin.math.tan(arg)
    override inline fun acos(arg: Double): Double = kotlin.math.acos(arg)
    override inline fun asin(arg: Double): Double = kotlin.math.asin(arg)
    override inline fun atan(arg: Double): Double = kotlin.math.atan(arg)

    override inline fun power(arg: Double, pow: Number): Double = arg.kpow(pow.toDouble())

    override inline fun exp(arg: Double): Double = kotlin.math.exp(arg)
    override inline fun ln(arg: Double): Double = kotlin.math.ln(arg)

    override inline fun norm(arg: Double): Double = abs(arg)

    override inline fun Double.unaryMinus(): Double = -this

    override inline fun Double.plus(b: Double): Double = this + b

    override inline fun Double.minus(b: Double): Double = this - b

    override inline fun Double.times(b: Double): Double = this * b

    override inline fun Double.div(b: Double): Double = this / b

    override fun binaryOperation(operation: String, left: Double, right: Double): Double = when (operation) {
        PowerOperations.POW_OPERATION -> left pow right
        else -> super.binaryOperation(operation, left, right)
    }
}

/**
 * A field for [Float] without boxing. Does not produce appropriate field element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
object FloatField : ExtendedField<Float>, Norm<Float, Float> {
    override val zero: Float = 0f
    override inline fun add(a: Float, b: Float): Float = a + b
    override inline fun multiply(a: Float, b: Float): Float = a * b
    override inline fun multiply(a: Float, k: Number): Float = a * k.toFloat()

    override val one: Float = 1f
    override inline fun divide(a: Float, b: Float): Float = a / b

    override inline fun sin(arg: Float): Float = kotlin.math.sin(arg)
    override inline fun cos(arg: Float): Float = kotlin.math.cos(arg)
    override inline fun tan(arg: Float): Float = kotlin.math.tan(arg)
    override inline fun acos(arg: Float): Float = kotlin.math.acos(arg)
    override inline fun asin(arg: Float): Float = kotlin.math.asin(arg)
    override inline fun atan(arg: Float): Float = kotlin.math.atan(arg)

    override inline fun power(arg: Float, pow: Number): Float = arg.pow(pow.toFloat())

    override inline fun exp(arg: Float): Float = kotlin.math.exp(arg)
    override inline fun ln(arg: Float): Float = kotlin.math.ln(arg)

    override inline fun norm(arg: Float): Float = abs(arg)

    override inline fun Float.unaryMinus(): Float = -this

    override inline fun Float.plus(b: Float): Float = this + b

    override inline fun Float.minus(b: Float): Float = this - b

    override inline fun Float.times(b: Float): Float = this * b

    override inline fun Float.div(b: Float): Float = this / b
}

/**
 * A field for [Int] without boxing. Does not produce corresponding field element
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
object IntRing : Ring<Int>, Norm<Int, Int>, RemainderDivisionOperations<Int> {
    override val zero: Int = 0
    override inline fun add(a: Int, b: Int): Int = a + b
    override inline fun multiply(a: Int, b: Int): Int = a * b
    override inline fun multiply(a: Int, k: Number): Int = k.toInt() * a
    override val one: Int = 1

    override inline fun norm(arg: Int): Int = abs(arg)

    override inline fun Int.unaryMinus(): Int = -this

    override inline fun Int.plus(b: Int): Int = this + b

    override inline fun Int.minus(b: Int): Int = this - b

    override inline fun Int.times(b: Int): Int = this * b

    override fun Int.rem(arg: Int): Int = this % arg
    override fun Int.div(arg: Int): Int = this / arg
}

/**
 * A field for [Short] without boxing. Does not produce appropriate field element
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
object ShortRing : Ring<Short>, Norm<Short, Short>, RemainderDivisionOperations<Short> {
    override val zero: Short = 0
    override inline fun add(a: Short, b: Short): Short = (a + b).toShort()
    override inline fun multiply(a: Short, b: Short): Short = (a * b).toShort()
    override inline fun multiply(a: Short, k: Number): Short = (a * k.toShort()).toShort()
    override val one: Short = 1

    override fun norm(arg: Short): Short = if (arg > 0) arg else (-arg).toShort()

    override inline fun Short.unaryMinus(): Short = (-this).toShort()

    override inline fun Short.plus(b: Short): Short = (this + b).toShort()

    override inline fun Short.minus(b: Short): Short = (this - b).toShort()

    override inline fun Short.times(b: Short): Short = (this * b).toShort()

    override fun Short.rem(arg: Short): Short = (this % arg).toShort()
    override fun Short.div(arg: Short): Short = (this / arg).toShort()
}

/**
 * A field for [Byte] values
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
object ByteRing : Ring<Byte>, Norm<Byte, Byte>, RemainderDivisionOperations<Byte> {
    override val zero: Byte = 0
    override inline fun add(a: Byte, b: Byte): Byte = (a + b).toByte()
    override inline fun multiply(a: Byte, b: Byte): Byte = (a * b).toByte()
    override inline fun multiply(a: Byte, k: Number): Byte = (a * k.toByte()).toByte()
    override val one: Byte = 1

    override fun norm(arg: Byte): Byte = if (arg > 0) arg else (-arg).toByte()

    override inline fun Byte.unaryMinus(): Byte = (-this).toByte()

    override inline fun Byte.plus(b: Byte): Byte = (this + b).toByte()

    override inline fun Byte.minus(b: Byte): Byte = (this - b).toByte()

    override inline fun Byte.times(b: Byte): Byte = (this * b).toByte()

    override fun Byte.rem(arg: Byte): Byte = (this % arg).toByte()
    override fun Byte.div(arg: Byte): Byte = (this / arg).toByte()
}

/**
 * A field for [Long] values
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
object LongRing : Ring<Long>, Norm<Long, Long>, RemainderDivisionOperations<Long> {
    override val zero: Long = 0
    override inline fun add(a: Long, b: Long): Long = (a + b)
    override inline fun multiply(a: Long, b: Long): Long = (a * b)
    override inline fun multiply(a: Long, k: Number): Long = a * k.toLong()
    override val one: Long = 1

    override fun norm(arg: Long): Long = abs(arg)

    override inline fun Long.unaryMinus(): Long = (-this)

    override inline fun Long.plus(b: Long): Long = (this + b)

    override inline fun Long.minus(b: Long): Long = (this - b)

    override inline fun Long.times(b: Long): Long = (this * b)

    override fun Long.rem(arg: Long): Long = this % arg
    override fun Long.div(arg: Long): Long = this / arg
}
