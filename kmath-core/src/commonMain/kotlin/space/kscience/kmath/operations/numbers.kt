/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
@file:Suppress("NOTHING_TO_INLINE")
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
 * A field for [Double] without boxing. Does not produce appropriate field element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE")
public object DoubleField : ExtendedField<Double>, Norm<Double, Double>, ScaleOperations<Double> {
    override val bufferFactory: MutableBufferFactory<Double> = MutableBufferFactory(::DoubleBuffer)

    override inline val zero: Double get() = 0.0
    override inline val one: Double get() = 1.0

    override inline fun number(value: Number): Double = value.toDouble()

    override fun binaryOperationFunction(operation: String): (left: Double, right: Double) -> Double =
        when (operation) {
            PowerOperations.POW_OPERATION -> { l, r -> l.kpow(r) }
            else -> super<ExtendedField>.binaryOperationFunction(operation)
        }

    override inline fun add(left: Double, right: Double): Double = left + right

    override inline fun multiply(left: Double, right: Double): Double = left * right
    override inline fun divide(left: Double, right: Double): Double = left / right

    override inline fun scale(a: Double, value: Double): Double = a * value

    override inline fun sin(arg: Double): Double = kotlin.math.sin(arg)
    override inline fun cos(arg: Double): Double = kotlin.math.cos(arg)
    override inline fun tan(arg: Double): Double = kotlin.math.tan(arg)
    override inline fun acos(arg: Double): Double = kotlin.math.acos(arg)
    override inline fun asin(arg: Double): Double = kotlin.math.asin(arg)
    override inline fun atan(arg: Double): Double = kotlin.math.atan(arg)

    override inline fun sinh(arg: Double): Double = kotlin.math.sinh(arg)
    override inline fun cosh(arg: Double): Double = kotlin.math.cosh(arg)
    override inline fun tanh(arg: Double): Double = kotlin.math.tanh(arg)
    override inline fun asinh(arg: Double): Double = kotlin.math.asinh(arg)
    override inline fun acosh(arg: Double): Double = kotlin.math.acosh(arg)
    override inline fun atanh(arg: Double): Double = kotlin.math.atanh(arg)

    override fun sqrt(arg: Double): Double = kotlin.math.sqrt(arg)
    override fun power(arg: Double, pow: Number): Double = when {
        pow.isInteger() -> arg.kpow(pow.toInt())
        arg < 0 -> throw IllegalArgumentException("Can't raise negative $arg to a fractional power $pow")
        else -> arg.kpow(pow.toDouble())
    }

    override inline fun exp(arg: Double): Double = kotlin.math.exp(arg)
    override inline fun ln(arg: Double): Double = kotlin.math.ln(arg)

    override inline fun norm(arg: Double): Double = abs(arg)

    override inline fun Double.unaryMinus(): Double = -this
    override inline fun Double.plus(arg: Double): Double = this + arg
    override inline fun Double.minus(arg: Double): Double = this - arg
    override inline fun Double.times(arg: Double): Double = this * arg
    override inline fun Double.div(arg: Double): Double = this / arg
}

public val Double.Companion.algebra: DoubleField get() = DoubleField

/**
 * A field for [Float] without boxing. Does not produce appropriate field element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE")
public object FloatField : ExtendedField<Float>, Norm<Float, Float> {
    override val bufferFactory: MutableBufferFactory<Float> = MutableBufferFactory(::FloatBuffer)

    override inline val zero: Float get() = 0.0f
    override inline val one: Float get() = 1.0f

    override fun number(value: Number): Float = value.toFloat()

    override fun binaryOperationFunction(operation: String): (left: Float, right: Float) -> Float =
        when (operation) {
            PowerOperations.POW_OPERATION -> { l, r -> l.kpow(r) }
            else -> super.binaryOperationFunction(operation)
        }

    override inline fun add(left: Float, right: Float): Float = left + right
    override fun scale(a: Float, value: Double): Float = a * value.toFloat()

    override inline fun multiply(left: Float, right: Float): Float = left * right

    override inline fun divide(left: Float, right: Float): Float = left / right

    override inline fun sin(arg: Float): Float = kotlin.math.sin(arg)
    override inline fun cos(arg: Float): Float = kotlin.math.cos(arg)
    override inline fun tan(arg: Float): Float = kotlin.math.tan(arg)
    override inline fun acos(arg: Float): Float = kotlin.math.acos(arg)
    override inline fun asin(arg: Float): Float = kotlin.math.asin(arg)
    override inline fun atan(arg: Float): Float = kotlin.math.atan(arg)

    override inline fun sinh(arg: Float): Float = kotlin.math.sinh(arg)
    override inline fun cosh(arg: Float): Float = kotlin.math.cosh(arg)
    override inline fun tanh(arg: Float): Float = kotlin.math.tanh(arg)
    override inline fun asinh(arg: Float): Float = kotlin.math.asinh(arg)
    override inline fun acosh(arg: Float): Float = kotlin.math.acosh(arg)
    override inline fun atanh(arg: Float): Float = kotlin.math.atanh(arg)

    override inline fun sqrt(arg: Float): Float = kotlin.math.sqrt(arg)
    override inline fun power(arg: Float, pow: Number): Float = arg.kpow(pow.toFloat())

    override inline fun exp(arg: Float): Float = kotlin.math.exp(arg)
    override inline fun ln(arg: Float): Float = kotlin.math.ln(arg)

    override inline fun norm(arg: Float): Float = abs(arg)

    override inline fun Float.unaryMinus(): Float = -this
    override inline fun Float.plus(arg: Float): Float = this + arg
    override inline fun Float.minus(arg: Float): Float = this - arg
    override inline fun Float.times(arg: Float): Float = this * arg
    override inline fun Float.div(arg: Float): Float = this / arg
}

public val Float.Companion.algebra: FloatField get() = FloatField

/**
 * A field for [Int] without boxing. Does not produce corresponding ring element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE")
public object IntRing : Ring<Int>, Norm<Int, Int>, NumericAlgebra<Int> {
    override val bufferFactory: MutableBufferFactory<Int> = MutableBufferFactory(::IntBuffer)

    override inline val zero: Int get() = 0
    override inline val one: Int get() = 1

    override fun number(value: Number): Int = value.toInt()
    override inline fun add(left: Int, right: Int): Int = left + right
    override inline fun multiply(left: Int, right: Int): Int = left * right
    override inline fun norm(arg: Int): Int = abs(arg)

    override inline fun Int.unaryMinus(): Int = -this
    override inline fun Int.plus(arg: Int): Int = this + arg
    override inline fun Int.minus(arg: Int): Int = this - arg
    override inline fun Int.times(arg: Int): Int = this * arg
}

public val Int.Companion.algebra: IntRing get() = IntRing

/**
 * A field for [Short] without boxing. Does not produce appropriate ring element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE")
public object ShortRing : Ring<Short>, Norm<Short, Short>, NumericAlgebra<Short> {
    override val bufferFactory: MutableBufferFactory<Short> = MutableBufferFactory(::ShortBuffer)

    override inline val zero: Short get() = 0
    override inline val one: Short get() = 1

    override fun number(value: Number): Short = value.toShort()
    override inline fun add(left: Short, right: Short): Short = (left + right).toShort()
    override inline fun multiply(left: Short, right: Short): Short = (left * right).toShort()
    override fun norm(arg: Short): Short = if (arg > 0) arg else (-arg).toShort()

    override inline fun Short.unaryMinus(): Short = (-this).toShort()
    override inline fun Short.plus(arg: Short): Short = (this + arg).toShort()
    override inline fun Short.minus(arg: Short): Short = (this - arg).toShort()
    override inline fun Short.times(arg: Short): Short = (this * arg).toShort()
}

public val Short.Companion.algebra: ShortRing get() = ShortRing

/**
 * A field for [Byte] without boxing. Does not produce appropriate ring element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE")
public object ByteRing : Ring<Byte>, Norm<Byte, Byte>, NumericAlgebra<Byte> {
    override val bufferFactory: MutableBufferFactory<Byte> = MutableBufferFactory(::ByteBuffer)

    override inline val zero: Byte get() = 0
    override inline val one: Byte get() = 1

    override fun number(value: Number): Byte = value.toByte()
    override inline fun add(left: Byte, right: Byte): Byte = (left + right).toByte()
    override inline fun multiply(left: Byte, right: Byte): Byte = (left * right).toByte()
    override fun norm(arg: Byte): Byte = if (arg > 0) arg else (-arg).toByte()

    override inline fun Byte.unaryMinus(): Byte = (-this).toByte()
    override inline fun Byte.plus(arg: Byte): Byte = (this + arg).toByte()
    override inline fun Byte.minus(arg: Byte): Byte = (this - arg).toByte()
    override inline fun Byte.times(arg: Byte): Byte = (this * arg).toByte()
}

public val Byte.Companion.algebra: ByteRing get() = ByteRing

/**
 * A field for [Double] without boxing. Does not produce appropriate ring element.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE")
public object LongRing : Ring<Long>, Norm<Long, Long>, NumericAlgebra<Long> {
    override val bufferFactory: MutableBufferFactory<Long> = MutableBufferFactory(::LongBuffer)

    override inline val zero: Long get() = 0L
    override inline val one: Long get() = 1L

    override fun number(value: Number): Long = value.toLong()
    override inline fun add(left: Long, right: Long): Long = left + right
    override inline fun multiply(left: Long, right: Long): Long = left * right
    override fun norm(arg: Long): Long = abs(arg)

    override inline fun Long.unaryMinus(): Long = (-this)
    override inline fun Long.plus(arg: Long): Long = (this + arg)
    override inline fun Long.minus(arg: Long): Long = (this - arg)
    override inline fun Long.times(arg: Long): Long = (this * arg)
}

public val Long.Companion.algebra: LongRing get() = LongRing
