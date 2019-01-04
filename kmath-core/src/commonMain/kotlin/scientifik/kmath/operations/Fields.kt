package scientifik.kmath.operations

import kotlin.math.pow

/**
 * Advanced Number-like field that implements basic operations
 */
interface ExtendedField<T : Any> :
    Field<T>,
    TrigonometricOperations<T>,
    PowerOperations<T>,
    ExponentialOperations<T>


/**
 * Real field element wrapping double.
 *
 * TODO inline does not work due to compiler bug. Waiting for fix for KT-27586
 */
inline class Real(val value: Double) : FieldElement<Double, Real, RealField> {
    override fun unwrap(): Double = value

    override fun Double.wrap(): Real = Real(value)

    override val context get() = RealField

    companion object {

    }
}

/**
 * A field for double without boxing. Does not produce appropriate field element
 */
object RealField : AbstractField<Double>(),ExtendedField<Double>, Norm<Double, Double> {
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
object IntField : Field<Int> {
    override val zero: Int = 0
    override fun add(a: Int, b: Int): Int = a + b
    override fun multiply(a: Int, b: Int): Int = a * b
    override fun multiply(a: Int, k: Double): Int = (k * a).toInt()
    override val one: Int = 1
    override fun divide(a: Int, b: Int): Int = a / b
}

//interface FieldAdapter<T, R> : Field<R> {
//
//    val field: Field<T>
//
//    abstract fun T.evolve(): R
//    abstract fun R.devolve(): T
//
//    override val zero get() = field.zero.evolve()
//    override val one get() = field.zero.evolve()
//
//    override fun add(a: R, b: R): R = field.add(a.devolve(), b.devolve()).evolve()
//
//    override fun multiply(a: R, k: Double): R = field.multiply(a.devolve(), k).evolve()
//
//
//    override fun multiply(a: R, b: R): R = field.multiply(a.devolve(), b.devolve()).evolve()
//
//    override fun divide(a: R, b: R): R = field.divide(a.devolve(), b.devolve()).evolve()
//}