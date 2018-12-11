package scientifik.kmath.operations

import kotlin.math.pow

/**
 * Advanced Number-like field that implements basic operations
 */
interface ExtendedField<N : Any> :
        Field<N>,
        TrigonometricOperations<N>,
        PowerOperations<N>,
        ExponentialOperations<N>


/**
 * Field for real values
 */
object RealField : ExtendedField<Real>, Norm<Real, Real> {
    override val zero: Real = Real(0.0)
    override fun add(a: Real, b: Real): Real = Real(a.value + b.value)
    override val one: Real = Real(1.0)
    override fun multiply(a: Real, b: Real): Real = Real(a.value * b.value)
    override fun multiply(a: Real, k: Double): Real = Real(a.value * k)
    override fun divide(a: Real, b: Real): Real = Real(a.value / b.value)

    override fun sin(arg: Real): Real = Real(kotlin.math.sin(arg.value))
    override fun cos(arg: Real): Real = Real(kotlin.math.cos(arg.value))

    override fun power(arg: Real, pow: Double): Real = Real(arg.value.pow(pow))

    override fun exp(arg: Real): Real = Real(kotlin.math.exp(arg.value))

    override fun ln(arg: Real): Real = Real(kotlin.math.ln(arg.value))

    override fun norm(arg: Real): Real = Real(kotlin.math.abs(arg.value))
}

/**
 * Real field element wrapping double.
 *
 * TODO inline does not work due to compiler bug. Waiting for fix for KT-27586
 */
inline class Real(val value: Double) : FieldElement<Real, RealField> {

    //values are dynamically calculated to save memory
    override val self
        get() = this

    override val context
        get() = RealField

    companion object {

    }
}

/**
 * A field for double without boxing. Does not produce appropriate field element
 */
object DoubleField : ExtendedField<Double>, Norm<Double, Double> {
    override val zero: Double = 0.0
    override fun add(a: Double, b: Double): Double = a + b
    override fun multiply(a: Double, @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") b: Double): Double = a * b
    override val one: Double = 1.0
    override fun divide(a: Double, b: Double): Double = a / b

    override fun sin(arg: Double): Double = kotlin.math.sin(arg)
    override fun cos(arg: Double): Double = kotlin.math.cos(arg)

    override fun power(arg: Double, pow: Double): Double = arg.pow(pow)

    override fun exp(arg: Double): Double = kotlin.math.exp(arg)

    override fun ln(arg: Double): Double = kotlin.math.ln(arg)

    override fun norm(arg: Double): Double = kotlin.math.abs(arg)
}

/**
 * A field for double without boxing. Does not produce appropriate field element
 */
object IntField : Field<Int>{
    override val zero: Int = 0
    override fun add(a: Int, b: Int): Int = a + b
    override fun multiply(a: Int, b: Int): Int = a * b
    override fun multiply(a: Int, k: Double): Int = (k*a).toInt()
    override val one: Int = 1
    override fun divide(a: Int, b: Int): Int = a / b
}