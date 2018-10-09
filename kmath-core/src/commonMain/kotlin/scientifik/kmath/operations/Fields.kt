package scientifik.kmath.operations

import kotlin.math.pow

/**
 * Field for real values
 */
object RealField : Field<Real>, TrigonometricOperations<Real>, PowerOperations<Real>, ExponentialOperations<Real> {
    override val zero: Real = Real(0.0)
    override fun add(a: Real, b: Real): Real = Real(a.value + b.value)
    override val one: Real = Real(1.0)
    override fun multiply(a: Real, b: Real): Real = Real(a.value * b.value)
    override fun multiply(a: Real, k: Double): Real = Real(a.value * k)
    override fun divide(a: Real, b: Real): Real = Real(a.value / b.value)

    override fun sin(arg: Real): Real  = Real(kotlin.math.sin(arg.value))
    override fun cos(arg: Real): Real = Real(kotlin.math.cos(arg.value))

    override fun power(arg: Real, pow: Double): Real  = Real(arg.value.pow(pow))

    override fun exp(arg: Real): Real  = Real(kotlin.math.exp(arg.value))

    override fun ln(arg: Real): Real  = Real(kotlin.math.ln(arg.value))
}

/**
 * Real field element wrapping double.
 *
 * TODO could be replaced by inline class in kotlin 1.3 if it would allow to avoid boxing
 */
data class Real(val value: Double) : Number(), FieldElement<Real, RealField> {
    /*
     * The class uses composition instead of inheritance since Double is final
     */

    override fun toByte(): Byte = value.toByte()
    override fun toChar(): Char = value.toChar()
    override fun toDouble(): Double = value
    override fun toFloat(): Float = value.toFloat()
    override fun toInt(): Int = value.toInt()
    override fun toLong(): Long = value.toLong()
    override fun toShort(): Short = value.toShort()

    //values are dynamically calculated to save memory
    override val self
        get() = this

    override val context
        get() = RealField

}

/**
 * A field for double without boxing. Does not produce appropriate field element
 */
object DoubleField : Field<Double>, TrigonometricOperations<Double>, PowerOperations<Double>, ExponentialOperations<Double> {
    override val zero: Double = 0.0
    override fun add(a: Double, b: Double): Double = a + b
    override fun multiply(a: Double, @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") b: Double): Double = a * b
    override val one: Double = 1.0
    override fun divide(a: Double, b: Double): Double = a / b

    override fun sin(arg: Double): Double  = kotlin.math.sin(arg)
    override fun cos(arg: Double): Double = kotlin.math.cos(arg)

    override fun power(arg: Double, pow: Double): Double  = arg.pow(pow)

    override fun exp(arg: Double): Double  =kotlin.math.exp(arg)

    override fun ln(arg: Double): Double  = kotlin.math.ln(arg)
}