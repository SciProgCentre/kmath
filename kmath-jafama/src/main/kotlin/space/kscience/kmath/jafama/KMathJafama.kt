/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.jafama

import net.jafama.FastMath
import net.jafama.StrictFastMath
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.Norm
import space.kscience.kmath.operations.PowerOperations
import space.kscience.kmath.operations.ScaleOperations

/**
 * A field for [Double] (using FastMath) without boxing. Does not produce appropriate field element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public object JafamaDoubleField : ExtendedField<Double>, Norm<Double, Double>, ScaleOperations<Double> {
    override inline val zero: Double get() = 0.0
    override inline val one: Double get() = 1.0

    override inline fun number(value: Number): Double = value.toDouble()

    override fun binaryOperationFunction(operation: String): (left: Double, right: Double) -> Double =
        when (operation) {
            PowerOperations.POW_OPERATION -> ::power
            else -> super<ExtendedField>.binaryOperationFunction(operation)
        }

    override inline fun add(left: Double, right: Double): Double = left + right

    override inline fun multiply(left: Double, right: Double): Double = left * right
    override inline fun divide(left: Double, right: Double): Double = left / right

    override inline fun scale(a: Double, value: Double): Double = a * value

    override inline fun sin(arg: Double): Double = FastMath.sin(arg)
    override inline fun cos(arg: Double): Double = FastMath.cos(arg)
    override inline fun tan(arg: Double): Double = FastMath.tan(arg)
    override inline fun acos(arg: Double): Double = FastMath.acos(arg)
    override inline fun asin(arg: Double): Double = FastMath.asin(arg)
    override inline fun atan(arg: Double): Double = FastMath.atan(arg)

    override inline fun sinh(arg: Double): Double = FastMath.sinh(arg)
    override inline fun cosh(arg: Double): Double = FastMath.cosh(arg)
    override inline fun tanh(arg: Double): Double = FastMath.tanh(arg)
    override inline fun asinh(arg: Double): Double = FastMath.asinh(arg)
    override inline fun acosh(arg: Double): Double = FastMath.acosh(arg)
    override inline fun atanh(arg: Double): Double = FastMath.atanh(arg)

    override inline fun sqrt(arg: Double): Double = FastMath.sqrt(arg)
    override inline fun power(arg: Double, pow: Number): Double = FastMath.pow(arg, pow.toDouble())
    override inline fun exp(arg: Double): Double = FastMath.exp(arg)
    override inline fun ln(arg: Double): Double = FastMath.log(arg)

    override inline fun norm(arg: Double): Double = FastMath.abs(arg)

    override inline fun Double.unaryMinus(): Double = -this
    override inline fun Double.plus(arg: Double): Double = this + arg
    override inline fun Double.minus(arg: Double): Double = this - arg
    override inline fun Double.times(arg: Double): Double = this * arg
    override inline fun Double.div(arg: Double): Double = this / arg
}

/**
 * A field for [Double] (using StrictMath) without boxing. Does not produce appropriate field element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public object StrictJafamaDoubleField : ExtendedField<Double>, Norm<Double, Double>, ScaleOperations<Double> {
    override inline val zero: Double get() = 0.0
    override inline val one: Double get() = 1.0

    override inline fun number(value: Number): Double = value.toDouble()

    override fun binaryOperationFunction(operation: String): (left: Double, right: Double) -> Double =
        when (operation) {
            PowerOperations.POW_OPERATION -> ::power
            else -> super<ExtendedField>.binaryOperationFunction(operation)
        }

    override inline fun add(left: Double, right: Double): Double = left + right

    override inline fun multiply(left: Double, right: Double): Double = left * right
    override inline fun divide(left: Double, right: Double): Double = left / right

    override inline fun scale(a: Double, value: Double): Double = a * value

    override inline fun sin(arg: Double): Double = StrictFastMath.sin(arg)
    override inline fun cos(arg: Double): Double = StrictFastMath.cos(arg)
    override inline fun tan(arg: Double): Double = StrictFastMath.tan(arg)
    override inline fun acos(arg: Double): Double = StrictFastMath.acos(arg)
    override inline fun asin(arg: Double): Double = StrictFastMath.asin(arg)
    override inline fun atan(arg: Double): Double = StrictFastMath.atan(arg)

    override inline fun sinh(arg: Double): Double = StrictFastMath.sinh(arg)
    override inline fun cosh(arg: Double): Double = StrictFastMath.cosh(arg)
    override inline fun tanh(arg: Double): Double = StrictFastMath.tanh(arg)
    override inline fun asinh(arg: Double): Double = StrictFastMath.asinh(arg)
    override inline fun acosh(arg: Double): Double = StrictFastMath.acosh(arg)
    override inline fun atanh(arg: Double): Double = StrictFastMath.atanh(arg)

    override inline fun sqrt(arg: Double): Double = StrictFastMath.sqrt(arg)
    override inline fun power(arg: Double, pow: Number): Double = StrictFastMath.pow(arg, pow.toDouble())
    override inline fun exp(arg: Double): Double = StrictFastMath.exp(arg)
    override inline fun ln(arg: Double): Double = StrictFastMath.log(arg)

    override inline fun norm(arg: Double): Double = StrictFastMath.abs(arg)

    override inline fun Double.unaryMinus(): Double = -this
    override inline fun Double.plus(arg: Double): Double = this + arg
    override inline fun Double.minus(arg: Double): Double = this - arg
    override inline fun Double.times(arg: Double): Double = this * arg
    override inline fun Double.div(arg: Double): Double = this / arg
}
