/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package space.kscience.kmath.operations

import space.kscience.kmath.structures.*
import kotlin.math.pow as kpow


/**
 * Advanced Number-like semifield that implements basic operations.
 */
public interface ExtendedFieldOps<T> :
    FieldOps<T>,
    TrigonometricOperations<T>,
    ExponentialOperations<T>,
    ScaleOperations<T>,
    PowerOperations<T> {
    override fun tan(arg: T): T = sin(arg) / cos(arg)
    override fun tanh(arg: T): T = sinh(arg) / cosh(arg)

    override fun unaryOperationFunction(operation: String): (arg: T) -> T = when (operation) {
        TrigonometricOperations.COS_OPERATION -> ::cos
        TrigonometricOperations.SIN_OPERATION -> ::sin
        TrigonometricOperations.TAN_OPERATION -> ::tan
        TrigonometricOperations.ACOS_OPERATION -> ::acos
        TrigonometricOperations.ASIN_OPERATION -> ::asin
        TrigonometricOperations.ATAN_OPERATION -> ::atan
        ExponentialOperations.EXP_OPERATION -> ::exp
        ExponentialOperations.LN_OPERATION -> ::ln
        ExponentialOperations.COSH_OPERATION -> ::cosh
        ExponentialOperations.SINH_OPERATION -> ::sinh
        ExponentialOperations.TANH_OPERATION -> ::tanh
        ExponentialOperations.ACOSH_OPERATION -> ::acosh
        ExponentialOperations.ASINH_OPERATION -> ::asinh
        ExponentialOperations.ATANH_OPERATION -> ::atanh
        else -> super<FieldOps>.unaryOperationFunction(operation)
    }
}

/**
 * Advanced Number-like field that implements basic operations.
 */
public interface ExtendedField<T> : ExtendedFieldOps<T>, Field<T>, NumericAlgebra<T> {
    override fun sinh(arg: T): T = (exp(arg) - exp(-arg)) / 2.0
    override fun cosh(arg: T): T = (exp(arg) + exp(-arg)) / 2.0
    override fun tanh(arg: T): T = (exp(arg) - exp(-arg)) / (exp(-arg) + exp(arg))
    override fun asinh(arg: T): T = ln(sqrt(arg * arg + one) + arg)
    override fun acosh(arg: T): T = ln(arg + sqrt((arg - one) * (arg + one)))
    override fun atanh(arg: T): T = (ln(arg + one) - ln(one - arg)) / 2.0

    override fun unaryOperationFunction(operation: String): (arg: T) -> T {
        return if (operation == PowerOperations.SQRT_OPERATION) ::sqrt
        else super<ExtendedFieldOps>.unaryOperationFunction(operation)
    }

    override fun rightSideNumberOperationFunction(operation: String): (left: T, right: Number) -> T =
        when (operation) {
            PowerOperations.POW_OPERATION -> ::power
            else -> super<Field>.rightSideNumberOperationFunction(operation)
        }
}

/**
 * A field for [Double] without boxing. Does not produce an appropriate field element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public object Float64Field : ExtendedField<Double>, Norm<Double, Double>, ScaleOperations<Double> {
    override val bufferFactory: MutableBufferFactory<Double> = MutableBufferFactory()

    override val zero: Double get() = 0.0
    override val one: Double get() = 1.0

    override fun number(value: Number): Double = value.toDouble()

    override fun binaryOperationFunction(operation: String): (left: Double, right: Double) -> Double =
        when (operation) {
            PowerOperations.POW_OPERATION -> { l, r -> l.kpow(r) }
            else -> super<ExtendedField>.binaryOperationFunction(operation)
        }

    override fun add(left: Double, right: Double): Double = left + right

    override fun multiply(left: Double, right: Double): Double = left * right
    override fun divide(left: Double, right: Double): Double = left / right

    override fun scale(a: Double, value: Double): Double = a * value

    override fun sin(arg: Double): Double = kotlin.math.sin(arg)
    override fun cos(arg: Double): Double = kotlin.math.cos(arg)
    override fun tan(arg: Double): Double = kotlin.math.tan(arg)
    override fun acos(arg: Double): Double = kotlin.math.acos(arg)
    override fun asin(arg: Double): Double = kotlin.math.asin(arg)
    override fun atan(arg: Double): Double = kotlin.math.atan(arg)

    override fun sinh(arg: Double): Double = kotlin.math.sinh(arg)
    override fun cosh(arg: Double): Double = kotlin.math.cosh(arg)
    override fun tanh(arg: Double): Double = kotlin.math.tanh(arg)
    override fun asinh(arg: Double): Double = kotlin.math.asinh(arg)
    override fun acosh(arg: Double): Double = kotlin.math.acosh(arg)
    override fun atanh(arg: Double): Double = kotlin.math.atanh(arg)

    override fun sqrt(arg: Double): Double = kotlin.math.sqrt(arg)
    override fun power(arg: Double, pow: Number): Double = when {
        pow.isInteger() -> arg.kpow(pow.toInt())
        arg < 0 -> throw IllegalArgumentException("Can't raise negative $arg to a fractional power $pow")
        else -> arg.kpow(pow.toDouble())
    }

    override fun exp(arg: Double): Double = kotlin.math.exp(arg)
    override fun ln(arg: Double): Double = kotlin.math.ln(arg)

    override fun norm(arg: Double): Double = abs(arg)

    override fun Double.unaryMinus(): Double = -this
    override fun Double.plus(arg: Double): Double = this + arg
    override fun Double.minus(arg: Double): Double = this - arg
    override fun Double.times(arg: Double): Double = this * arg
    override fun Double.div(arg: Double): Double = this / arg
}

public typealias DoubleField = Float64Field

public val Double.Companion.algebra: Float64Field get() = Float64Field

/**
 * A field for [Float] without boxing. Does not produce an appropriate field element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public object Float32Field : ExtendedField<Float>, Norm<Float, Float> {
    override val bufferFactory: MutableBufferFactory<Float> = MutableBufferFactory(::Float32Buffer)

    override val zero: Float get() = 0.0f
    override val one: Float get() = 1.0f

    override fun number(value: Number): Float = value.toFloat()

    override fun binaryOperationFunction(operation: String): (left: Float, right: Float) -> Float =
        when (operation) {
            PowerOperations.POW_OPERATION -> { l, r -> l.kpow(r) }
            else -> super.binaryOperationFunction(operation)
        }

    override fun add(left: Float, right: Float): Float = left + right
    override fun scale(a: Float, value: Double): Float = a * value.toFloat()

    override fun multiply(left: Float, right: Float): Float = left * right

    override fun divide(left: Float, right: Float): Float = left / right

    override fun sin(arg: Float): Float = kotlin.math.sin(arg)
    override fun cos(arg: Float): Float = kotlin.math.cos(arg)
    override fun tan(arg: Float): Float = kotlin.math.tan(arg)
    override fun acos(arg: Float): Float = kotlin.math.acos(arg)
    override fun asin(arg: Float): Float = kotlin.math.asin(arg)
    override fun atan(arg: Float): Float = kotlin.math.atan(arg)

    override fun sinh(arg: Float): Float = kotlin.math.sinh(arg)
    override fun cosh(arg: Float): Float = kotlin.math.cosh(arg)
    override fun tanh(arg: Float): Float = kotlin.math.tanh(arg)
    override fun asinh(arg: Float): Float = kotlin.math.asinh(arg)
    override fun acosh(arg: Float): Float = kotlin.math.acosh(arg)
    override fun atanh(arg: Float): Float = kotlin.math.atanh(arg)

    override fun sqrt(arg: Float): Float = kotlin.math.sqrt(arg)
    override fun power(arg: Float, pow: Number): Float = arg.kpow(pow.toFloat())

    override fun exp(arg: Float): Float = kotlin.math.exp(arg)
    override fun ln(arg: Float): Float = kotlin.math.ln(arg)

    override fun norm(arg: Float): Float = abs(arg)

    override fun Float.unaryMinus(): Float = -this
    override fun Float.plus(arg: Float): Float = this + arg
    override fun Float.minus(arg: Float): Float = this - arg
    override fun Float.times(arg: Float): Float = this * arg
    override fun Float.div(arg: Float): Float = this / arg
}

public typealias FloatField = Float32Field

public val Float.Companion.algebra: Float32Field get() = Float32Field

/**
 * A field for [Int] without boxing. Does not produce a corresponding ring element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public object Int32Ring : Ring<Int>, Norm<Int, Int>, NumericAlgebra<Int> {
    override val bufferFactory: MutableBufferFactory<Int> = MutableBufferFactory(::Int32Buffer)

    override val zero: Int get() = 0
    override val one: Int get() = 1

    override fun number(value: Number): Int = value.toInt()
    override fun add(left: Int, right: Int): Int = left + right
    override fun multiply(left: Int, right: Int): Int = left * right
    override fun norm(arg: Int): Int = abs(arg)

    override fun Int.unaryMinus(): Int = -this
    override fun Int.plus(arg: Int): Int = this + arg
    override fun Int.minus(arg: Int): Int = this - arg
    override fun Int.times(arg: Int): Int = this * arg
}

public typealias IntRing = Int32Ring

public val Int.Companion.algebra: Int32Ring get() = Int32Ring

/**
 * A field for [Short] without boxing. Does not produce an appropriate ring element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public object Int16Ring : Ring<Short>, Norm<Short, Short>, NumericAlgebra<Short> {
    override val bufferFactory: MutableBufferFactory<Short> = MutableBufferFactory(::Int16Buffer)

    override val zero: Short get() = 0
    override val one: Short get() = 1

    override fun number(value: Number): Short = value.toShort()
    override fun add(left: Short, right: Short): Short = (left + right).toShort()
    override fun multiply(left: Short, right: Short): Short = (left * right).toShort()
    override fun norm(arg: Short): Short = if (arg > 0) arg else (-arg).toShort()

    override fun Short.unaryMinus(): Short = (-this).toShort()
    override fun Short.plus(arg: Short): Short = (this + arg).toShort()
    override fun Short.minus(arg: Short): Short = (this - arg).toShort()
    override fun Short.times(arg: Short): Short = (this * arg).toShort()
}

public typealias ShortRing = Int16Ring

public val Short.Companion.algebra: Int16Ring get() = Int16Ring

/**
 * A field for [Byte] without boxing. Does not produce appropriate ring element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public object Int8Ring : Ring<Byte>, Norm<Byte, Byte>, NumericAlgebra<Byte> {
    override val bufferFactory: MutableBufferFactory<Byte> = MutableBufferFactory(::Int8Buffer)

    override val zero: Byte get() = 0
    override val one: Byte get() = 1

    override fun number(value: Number): Byte = value.toByte()
    override fun add(left: Byte, right: Byte): Byte = (left + right).toByte()
    override fun multiply(left: Byte, right: Byte): Byte = (left * right).toByte()
    override fun norm(arg: Byte): Byte = if (arg > 0) arg else (-arg).toByte()

    override fun Byte.unaryMinus(): Byte = (-this).toByte()
    override fun Byte.plus(arg: Byte): Byte = (this + arg).toByte()
    override fun Byte.minus(arg: Byte): Byte = (this - arg).toByte()
    override fun Byte.times(arg: Byte): Byte = (this * arg).toByte()
}

public typealias ByteRing = Int8Ring

public val Byte.Companion.algebra: Int8Ring get() = Int8Ring

/**
 * A field for [Double] without boxing. Does not produce an appropriate ring element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public object Int64Ring : Ring<Long>, Norm<Long, Long>, NumericAlgebra<Long> {
    override val bufferFactory: MutableBufferFactory<Long> = MutableBufferFactory(::Int64Buffer)

    override val zero: Long get() = 0L
    override val one: Long get() = 1L

    override fun number(value: Number): Long = value.toLong()
    override fun add(left: Long, right: Long): Long = left + right
    override fun multiply(left: Long, right: Long): Long = left * right
    override fun norm(arg: Long): Long = abs(arg)

    override fun Long.unaryMinus(): Long = (-this)
    override fun Long.plus(arg: Long): Long = (this + arg)
    override fun Long.minus(arg: Long): Long = (this - arg)
    override fun Long.times(arg: Long): Long = (this * arg)
}

public typealias LongRing = Int64Ring

public val Long.Companion.algebra: Int64Ring get() = Int64Ring
