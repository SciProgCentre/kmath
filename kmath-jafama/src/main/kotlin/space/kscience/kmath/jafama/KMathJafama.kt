package space.kscience.kmath.jafama

import space.kscience.kmath.operations.*
import net.jafama.*

/**
 * A field for [Double] (using FastMath) without boxing. Does not produce appropriate field element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public object JafamaDoubleField : ExtendedField<Double>, Norm<Double, Double>, ScaleOperations<Double> {
    public override val zero: Double get() = 0.0
    public override val one: Double get() = 1.0

    public override fun number(value: Number): Double = value.toDouble()

    public override fun binaryOperationFunction(operation: String): (left: Double, right: Double) -> Double =
        when (operation) {
            PowerOperations.POW_OPERATION -> ::power
            else -> super<ExtendedField>.binaryOperationFunction(operation)
        }

    public override fun add(a: Double, b: Double): Double = a + b

    public override fun multiply(a: Double, b: Double): Double = a * b
    public override fun divide(a: Double, b: Double): Double = a / b

    public override fun scale(a: Double, value: Double): Double = a * value

    public override fun sin(arg: Double): Double = FastMath.sin(arg)
    public override fun cos(arg: Double): Double = FastMath.cos(arg)
    public override fun tan(arg: Double): Double = FastMath.tan(arg)
    public override fun acos(arg: Double): Double = FastMath.acos(arg)
    public override fun asin(arg: Double): Double = FastMath.asin(arg)
    public override fun atan(arg: Double): Double = FastMath.atan(arg)

    public override fun sinh(arg: Double): Double = FastMath.sinh(arg)
    public override fun cosh(arg: Double): Double = FastMath.cosh(arg)
    public override fun tanh(arg: Double): Double = FastMath.tanh(arg)
    public override fun asinh(arg: Double): Double = FastMath.asinh(arg)
    public override fun acosh(arg: Double): Double = FastMath.acosh(arg)
    public override fun atanh(arg: Double): Double = FastMath.atanh(arg)

    public override fun power(arg: Double, pow: Number): Double = FastMath.pow(arg, pow.toDouble())
    public override fun exp(arg: Double): Double = FastMath.exp(arg)
    public override fun ln(arg: Double): Double = FastMath.log(arg)

    public override fun norm(arg: Double): Double = FastMath.abs(arg)

    public override fun Double.unaryMinus(): Double = -this
    public override fun Double.plus(b: Double): Double = this + b
    public override fun Double.minus(b: Double): Double = this - b
    public override fun Double.times(b: Double): Double = this * b
    public override fun Double.div(b: Double): Double = this / b
}

/**
 * A field for [Double] (using StrictMath) without boxing. Does not produce appropriate field element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public object StrictJafamaDoubleField : ExtendedField<Double>, Norm<Double, Double>, ScaleOperations<Double> {
    public override  val zero: Double get() = 0.0
    public override  val one: Double get() = 1.0

    public override fun number(value: Number): Double = value.toDouble()

    public override fun binaryOperationFunction(operation: String): (left: Double, right: Double) -> Double =
        when (operation) {
            PowerOperations.POW_OPERATION -> ::power
            else -> super<ExtendedField>.binaryOperationFunction(operation)
        }

    public override fun add(a: Double, b: Double): Double = a + b

    public override fun multiply(a: Double, b: Double): Double = a * b
    public override fun divide(a: Double, b: Double): Double = a / b

    public override fun scale(a: Double, value: Double): Double = a * value

    public override fun sin(arg: Double): Double = StrictFastMath.sin(arg)
    public override fun cos(arg: Double): Double = StrictFastMath.cos(arg)
    public override fun tan(arg: Double): Double = StrictFastMath.tan(arg)
    public override fun acos(arg: Double): Double = StrictFastMath.acos(arg)
    public override fun asin(arg: Double): Double = StrictFastMath.asin(arg)
    public override fun atan(arg: Double): Double = StrictFastMath.atan(arg)

    public override fun sinh(arg: Double): Double = StrictFastMath.sinh(arg)
    public override fun cosh(arg: Double): Double = StrictFastMath.cosh(arg)
    public override fun tanh(arg: Double): Double = StrictFastMath.tanh(arg)
    public override fun asinh(arg: Double): Double = StrictFastMath.asinh(arg)
    public override fun acosh(arg: Double): Double = StrictFastMath.acosh(arg)
    public override fun atanh(arg: Double): Double = StrictFastMath.atanh(arg)

    public override fun power(arg: Double, pow: Number): Double = StrictFastMath.pow(arg, pow.toDouble())
    public override fun exp(arg: Double): Double = StrictFastMath.exp(arg)
    public override fun ln(arg: Double): Double = StrictFastMath.log(arg)

    public override fun norm(arg: Double): Double = StrictFastMath.abs(arg)

    public override fun Double.unaryMinus(): Double = -this
    public override fun Double.plus(b: Double): Double = this + b
    public override fun Double.minus(b: Double): Double = this - b
    public override fun Double.times(b: Double): Double = this * b
    public override fun Double.div(b: Double): Double = this / b
}


