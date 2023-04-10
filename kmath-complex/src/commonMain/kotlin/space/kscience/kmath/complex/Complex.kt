/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.complex

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.memory.MemoryReader
import space.kscience.kmath.memory.MemorySpec
import space.kscience.kmath.memory.MemoryWriter
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.*
import kotlin.math.*

/**
 * This complex's conjugate.
 */
public val Complex.conjugate: Complex
    get() = Complex(re, -im)

/**
 * This complex's reciprocal.
 */
public val Complex.reciprocal: Complex
    get() {
        val scale = re * re + im * im
        return Complex(re / scale, -im / scale)
    }

/**
 * Absolute value of complex number.
 */
public val Complex.r: Double
    get() = sqrt(re * re + im * im)

/**
 * An angle between vector represented by complex number and X axis.
 */
public val Complex.theta: Double
    get() = atan2(im, re)

private val PI_DIV_2 = Complex(PI / 2, 0)

/**
 * A field of [Complex].
 */
@OptIn(UnstableKMathAPI::class)
public object ComplexField :
    ExtendedField<Complex>,
    Norm<Complex, Complex>,
    NumbersAddOps<Complex>,
    ScaleOperations<Complex> {
    override val bufferFactory: MutableBufferFactory<Complex> = MutableBufferFactory { size, init ->
        MutableMemoryBuffer.create(Complex, size, init)
    }

    override val zero: Complex = 0.0.toComplex()
    override val one: Complex = 1.0.toComplex()

    override fun bindSymbolOrNull(value: String): Complex? = if (value == "i") i else null

    override fun binaryOperationFunction(operation: String): (left: Complex, right: Complex) -> Complex =
        when (operation) {
            PowerOperations.POW_OPERATION -> ComplexField::power
            else -> super<ExtendedField>.binaryOperationFunction(operation)
        }

    /**
     * The imaginary unit.
     */
    public val i: Complex by lazy { Complex(0.0, 1.0) }

    override fun Complex.unaryMinus(): Complex = Complex(-re, -im)

    override fun number(value: Number): Complex = Complex(value.toDouble(), 0.0)

    override fun scale(a: Complex, value: Double): Complex = Complex(a.re * value, a.im * value)

    override fun add(left: Complex, right: Complex): Complex = Complex(left.re + right.re, left.im + right.im)
//    override fun multiply(a: Complex, k: Number): Complex = Complex(a.re * k.toDouble(), a.im * k.toDouble())

//    override fun Complex.minus(arg: Complex): Complex = Complex(re - arg.re, im - arg.im)

    override fun multiply(left: Complex, right: Complex): Complex =
        Complex(left.re * right.re - left.im * right.im, left.re * right.im + left.im * right.re)

    override fun divide(left: Complex, right: Complex): Complex = when {
        abs(right.im) < abs(right.re) -> {
            val wr = right.im / right.re
            val wd = right.re + wr * right.im

            if (wd.isNaN() || wd == 0.0)
                throw ArithmeticException("Division by zero or infinity")
            else
                Complex((left.re + left.im * wr) / wd, (left.im - left.re * wr) / wd)
        }

        right.im == 0.0 -> throw ArithmeticException("Division by zero")

        else -> {
            val wr = right.re / right.im
            val wd = right.im + wr * right.re

            if (wd.isNaN() || wd == 0.0)
                throw ArithmeticException("Division by zero or infinity")
            else
                Complex((left.re * wr + left.im) / wd, (left.im * wr - left.re) / wd)
        }
    }

    override operator fun Complex.div(k: Number): Complex = Complex(re / k.toDouble(), im / k.toDouble())

    override fun sin(arg: Complex): Complex = i * (exp(-i * arg) - exp(i * arg)) / 2.0
    override fun cos(arg: Complex): Complex = (exp(-i * arg) + exp(i * arg)) / 2.0

    override fun tan(arg: Complex): Complex {
        val e1 = exp(-i * arg)
        val e2 = exp(i * arg)
        return i * (e1 - e2) / (e1 + e2)
    }

    override fun asin(arg: Complex): Complex = -i * ln(sqrt(1 - (arg * arg)) + i * arg)
    override fun acos(arg: Complex): Complex = PI_DIV_2 + i * ln(sqrt(1 - (arg * arg)) + i * arg)

    override fun atan(arg: Complex): Complex {
        val iArg = i * arg
        return i * (ln(1 - iArg) - ln(1 + iArg)) / 2
    }

    override fun power(arg: Complex, pow: Number): Complex = if (arg.im == 0.0) {
        arg.re.pow(pow.toDouble()).toComplex()
    } else {
        exp(pow * ln(arg))
    }

    public fun power(arg: Complex, pow: Complex): Complex = exp(pow * ln(arg))


    override fun exp(arg: Complex): Complex = exp(arg.re) * (cos(arg.im) + i * sin(arg.im))

    override fun ln(arg: Complex): Complex = ln(arg.r) + i * atan2(arg.im, arg.re)

    /**
     * Adds complex number to real one.
     *
     * @receiver the augend.
     * @param c the addend.
     * @return the sum.
     */
    public operator fun Double.plus(c: Complex): Complex = add(this.toComplex(), c)

    /**
     * Subtracts complex number from real one.
     *
     * @receiver the minuend.
     * @param c the subtrahend.
     * @return the difference.
     */
    public operator fun Double.minus(c: Complex): Complex = add(this.toComplex(), -c)

    /**
     * Adds real number to complex one.
     *
     * @receiver the augend.
     * @param d the addend.
     * @return the sum.
     */
    public operator fun Complex.plus(d: Double): Complex = d + this

    /**
     * Subtracts real number from complex one.
     *
     * @receiver the minuend.
     * @param d the subtrahend.
     * @return the difference.
     */
    public operator fun Complex.minus(d: Double): Complex = add(this, -d.toComplex())

    /**
     * Multiplies real number by complex one.
     *
     * @receiver the multiplier.
     * @param c the multiplicand.
     * @receiver the product.
     */
    public operator fun Double.times(c: Complex): Complex = Complex(c.re * this, c.im * this)

    override fun norm(arg: Complex): Complex = sqrt(arg.conjugate * arg)
}

/**
 * Represents `double`-based complex number.
 *
 * @property re The real part.
 * @property im The imaginary part.
 */
public data class Complex(val re: Double, val im: Double) {
    public constructor(re: Number, im: Number) : this(re.toDouble(), im.toDouble())
    public constructor(re: Number) : this(re.toDouble(), 0.0)

    override fun toString(): String = "($re + i * $im)"

    public companion object : MemorySpec<Complex> {
        override val objectSize: Int
            get() = 16

        override fun MemoryReader.read(offset: Int): Complex =
            Complex(readDouble(offset), readDouble(offset + 8))

        override fun MemoryWriter.write(offset: Int, value: Complex) {
            writeDouble(offset, value.re)
            writeDouble(offset + 8, value.im)
        }
    }
}

public val Complex.Companion.algebra: ComplexField get() = ComplexField

/**
 * Creates a complex number with real part equal to this real.
 *
 * @receiver the real part.
 * @return the new complex number.
 */
public fun Number.toComplex(): Complex = Complex(this)

/**
 * Creates a new buffer of complex numbers with the specified [size], where each element is calculated by calling the
 * specified [init] function.
 */
public inline fun Buffer.Companion.complex(size: Int, init: (Int) -> Complex): Buffer<Complex> =
    MemoryBuffer.create(Complex, size, init)

/**
 * Creates a new buffer of complex numbers with the specified [size], where each element is calculated by calling the
 * specified [init] function.
 */
public inline fun MutableBuffer.Companion.complex(size: Int, init: (Int) -> Complex): MutableBuffer<Complex> =
    MutableMemoryBuffer.create(Complex, size, init)
