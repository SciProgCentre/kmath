package space.kscience.kmath.jafama

import space.kscience.kmath.operations.*

/**
 * Advanced Number-like semifield that implements basic operations.
 */
public interface ExtendedFieldOperations<T> :
    FieldOperations<T>,
    TrigonometricOperations<T>,
    PowerOperations<T>,
    ExponentialOperations<T> {
    public override fun tan(arg: T): T = sin(arg) / cos(arg)
    public override fun tanh(arg: T): T = sinh(arg) / cosh(arg)

    public override fun unaryOperationFunction(operation: String): (arg: T) -> T = when (operation) {
        TrigonometricOperations.COS_OPERATION -> ::cos
        TrigonometricOperations.SIN_OPERATION -> ::sin
        TrigonometricOperations.TAN_OPERATION -> ::tan
        TrigonometricOperations.ACOS_OPERATION -> ::acos
        TrigonometricOperations.ASIN_OPERATION -> ::asin
        TrigonometricOperations.ATAN_OPERATION -> ::atan
        PowerOperations.SQRT_OPERATION -> ::sqrt
        ExponentialOperations.EXP_OPERATION -> ::exp
        ExponentialOperations.LN_OPERATION -> ::ln
        ExponentialOperations.COSH_OPERATION -> ::cosh
        ExponentialOperations.SINH_OPERATION -> ::sinh
        ExponentialOperations.TANH_OPERATION -> ::tanh
        ExponentialOperations.ACOSH_OPERATION -> ::acosh
        ExponentialOperations.ASINH_OPERATION -> ::asinh
        ExponentialOperations.ATANH_OPERATION -> ::atanh
        else -> super<FieldOperations>.unaryOperationFunction(operation)
    }
}

/**
 * Advanced Number-like field that implements basic operations.
 */
public interface ExtendedField<T> : ExtendedFieldOperations<T>, Field<T>, NumericAlgebra<T>, ScaleOperations<T> {
    public override fun sinh(arg: T): T = (exp(arg) - exp(-arg)) / 2.0
    public override fun cosh(arg: T): T = (exp(arg) + exp(-arg)) / 2.0
    public override fun tanh(arg: T): T = (exp(arg) - exp(-arg)) / (exp(-arg) + exp(arg))
    public override fun asinh(arg: T): T = ln(sqrt(arg * arg + one) + arg)
    public override fun acosh(arg: T): T = ln(arg + sqrt((arg - one) * (arg + one)))
    public override fun atanh(arg: T): T = (ln(arg + one) - ln(one - arg)) / 2.0

    public override fun rightSideNumberOperationFunction(operation: String): (left: T, right: Number) -> T =
        when (operation) {
            PowerOperations.POW_OPERATION -> ::power
            else -> super<Field>.rightSideNumberOperationFunction(operation)
        }
}

/**
 * A field for [Double] without boxing. Does not produce appropriate field element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public object DoubleField : ExtendedField<Double>, Norm<Double, Double>, ScaleOperations<Double> {
    public override inline val zero: Double get() = 0.0
    public override inline val one: Double get() = 1.0

    public override fun number(value: Number): Double = value.toDouble()

    public override fun binaryOperationFunction(operation: String): (left: Double, right: Double) -> Double =
        when (operation) {
            PowerOperations.POW_OPERATION -> ::power
            else -> super<ExtendedField>.binaryOperationFunction(operation)
        }

    public override inline fun add(a: Double, b: Double): Double = a + b

    public override inline fun multiply(a: Double, b: Double): Double = a * b
    public override inline fun divide(a: Double, b: Double): Double = a / b

    public override fun scale(a: Double, value: Double): Double = a * value

    public override inline fun sin(arg: Double): Double = FastMath.sin(arg)
    public override inline fun cos(arg: Double): Double = FastMath.cos(arg)
    public override inline fun tan(arg: Double): Double = FastMath.tan(arg)
    public override inline fun acos(arg: Double): Double = FastMath.acos(arg)
    public override inline fun asin(arg: Double): Double = FastMath.asin(arg)
    public override inline fun atan(arg: Double): Double = FastMath.atan(arg)

    public override inline fun sinh(arg: Double): Double = FastMath.sinh(arg)
    public override inline fun cosh(arg: Double): Double = FastMath.cosh(arg)
    public override inline fun tanh(arg: Double): Double = FastMath.tanh(arg)
    public override inline fun asinh(arg: Double): Double = FastMath.asinh(arg)
    public override inline fun acosh(arg: Double): Double = FastMath.acosh(arg)
    public override inline fun atanh(arg: Double): Double = FastMath.atanh(arg)

    public override inline fun power(arg: Double, pow: Number): Double = FastMath.pow(arg, pow.toDouble())
    public override inline fun exp(arg: Double): Double = FastMath.exp(arg)
    public override inline fun ln(arg: Double): Double = FastMath.log(arg)

    public override inline fun norm(arg: Double): Double = FastMath.abs(arg)

    public override inline fun Double.unaryMinus(): Double = -this
    public override inline fun Double.plus(b: Double): Double = this + b
    public override inline fun Double.minus(b: Double): Double = this - b
    public override inline fun Double.times(b: Double): Double = this * b
    public override inline fun Double.div(b: Double): Double = this / b
}
