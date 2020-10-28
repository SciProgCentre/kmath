package kscience.kmath.operations

import kscience.kmath.memory.MemoryReader
import kscience.kmath.memory.MemorySpec
import kscience.kmath.memory.MemoryWriter
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.MemoryBuffer
import kscience.kmath.structures.MutableBuffer
import kscience.kmath.structures.MutableMemoryBuffer
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
    get() = atan(im / re)

private val PI_DIV_2 = Complex(PI / 2, 0)

/**
 * A field of [Complex].
 */
public object ComplexField : ExtendedField<Complex>, Norm<Complex, Complex> {
    override val zero: Complex = 0.0.toComplex()
    override val one: Complex = 1.0.toComplex()

    /**
     * The imaginary unit.
     */
    public val i: Complex = Complex(0.0, 1.0)

    override fun add(a: Complex, b: Complex): Complex = Complex(a.re + b.re, a.im + b.im)

    override fun multiply(a: Complex, k: Number): Complex = Complex(a.re * k.toDouble(), a.im * k.toDouble())

    override fun multiply(a: Complex, b: Complex): Complex =
        Complex(a.re * b.re - a.im * b.im, a.re * b.im + a.im * b.re)

    override fun divide(a: Complex, b: Complex): Complex = when {
        b.re.isNaN() || b.im.isNaN() -> Complex(Double.NaN, Double.NaN)

        (if (b.im < 0) -b.im else +b.im) < (if (b.re < 0) -b.re else +b.re) -> {
            val wr = b.im / b.re
            val wd = b.re + wr * b.im

            if (wd.isNaN() || wd == 0.0)
                Complex(Double.NaN, Double.NaN)
            else
                Complex((a.re + a.im * wr) / wd, (a.im - a.re * wr) / wd)
        }

        b.im == 0.0 -> Complex(Double.NaN, Double.NaN)

        else -> {
            val wr = b.re / b.im
            val wd = b.im + wr * b.re

            if (wd.isNaN() || wd == 0.0)
                Complex(Double.NaN, Double.NaN)
            else
                Complex((a.re * wr + a.im) / wd, (a.im * wr - a.re) / wd)
        }
    }

    override fun sin(arg: Complex): Complex = i * (exp(-i * arg) - exp(i * arg)) / 2
    override fun cos(arg: Complex): Complex = (exp(-i * arg) + exp(i * arg)) / 2

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

    override fun power(arg: Complex, pow: Number): Complex = if (arg.im == 0.0)
        arg.re.pow(pow.toDouble()).toComplex()
    else
        exp(pow * ln(arg))

    override fun exp(arg: Complex): Complex = exp(arg.re) * (cos(arg.im) + i * sin(arg.im))

    override fun ln(arg: Complex): Complex = ln(arg.r) + i * atan2(arg.im, arg.re)

    /**
     * Adds complex number to real one.
     *
     * @receiver the addend.
     * @param c the augend.
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
     * @receiver the addend.
     * @param d the augend.
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

    override fun symbol(value: String): Complex = if (value == "i") i else super.symbol(value)
}

/**
 * Represents `double`-based complex number.
 *
 * @property re The real part.
 * @property im The imaginary part.
 */
public data class Complex(val re: Double, val im: Double) : FieldElement<Complex, Complex, ComplexField>,
    Comparable<Complex> {
    public constructor(re: Number, im: Number) : this(re.toDouble(), im.toDouble())

    override val context: ComplexField get() = ComplexField

    override fun unwrap(): Complex = this

    override fun Complex.wrap(): Complex = this

    override fun compareTo(other: Complex): Int = r.compareTo(other.r)

    override fun toString(): String {
        return "($re + i*$im)"
    }


    public companion object : MemorySpec<Complex> {
        override val objectSize: Int
            get() = 16

        override fun MemoryReader.read(offset: Int): Complex = Complex(readDouble(offset), readDouble(offset + 8))

        override fun MemoryWriter.write(offset: Int, value: Complex) {
            writeDouble(offset, value.re)
            writeDouble(offset + 8, value.im)
        }
    }
}


/**
 * Creates a complex number with real part equal to this real.
 *
 * @receiver the real part.
 * @return the new complex number.
 */
public fun Number.toComplex(): Complex = Complex(this, 0.0)

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
