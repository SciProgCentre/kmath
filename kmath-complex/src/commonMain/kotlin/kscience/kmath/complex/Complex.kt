package kscience.kmath.complex

import kscience.kmath.memory.MemoryReader
import kscience.kmath.memory.MemorySpec
import kscience.kmath.memory.MemoryWriter
import kscience.kmath.operations.ExtendedField
import kscience.kmath.operations.FieldElement
import kscience.kmath.operations.Norm
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
    override val zero: Complex by lazy { 0.toComplex() }
    override val one: Complex by lazy { 1.toComplex() }

    /**
     * The imaginary unit.
     */
    public val i: Complex by lazy { Complex(0, 1) }

    public override fun add(a: Complex, b: Complex): Complex = Complex(a.re + b.re, a.im + b.im)
    public override fun multiply(a: Complex, k: Number): Complex = Complex(a.re * k.toDouble(), a.im * k.toDouble())

    public override fun multiply(a: Complex, b: Complex): Complex =
        Complex(a.re * b.re - a.im * b.im, a.re * b.im + a.im * b.re)

    public override fun divide(a: Complex, b: Complex): Complex = when {
        abs(b.im) < abs(b.re) -> {
            val wr = b.im / b.re
            val wd = b.re + wr * b.im

            if (wd.isNaN() || wd == 0.0)
                throw ArithmeticException("Division by zero or infinity")
            else
                Complex((a.re + a.im * wr) / wd, (a.im - a.re * wr) / wd)
        }

        b.im == 0.0 -> throw ArithmeticException("Division by zero")

        else -> {
            val wr = b.re / b.im
            val wd = b.im + wr * b.re

            if (wd.isNaN() || wd == 0.0)
                throw ArithmeticException("Division by zero or infinity")
            else
                Complex((a.re * wr + a.im) / wd, (a.im * wr - a.re) / wd)
        }
    }

    public override fun sin(arg: Complex): Complex = i * (exp(-i * arg) - exp(i * arg)) / 2
    public override fun cos(arg: Complex): Complex = (exp(-i * arg) + exp(i * arg)) / 2

    public override fun tan(arg: Complex): Complex {
        val e1 = exp(-i * arg)
        val e2 = exp(i * arg)
        return i * (e1 - e2) / (e1 + e2)
    }

    public override fun asin(arg: Complex): Complex = -i * ln(sqrt(one - (arg * arg)) + i * arg)
    public override fun acos(arg: Complex): Complex = PI_DIV_2 + i * ln(sqrt(one - (arg * arg)) + i * arg)

    public override fun atan(arg: Complex): Complex {
        val iArg = i * arg
        return i * (ln(1 - iArg) - ln(1 + iArg)) / 2
    }

    public override fun power(arg: Complex, pow: Number): Complex = if (arg.im == 0.0)
        arg.re.pow(pow.toDouble()).toComplex()
    else
        exp(pow * ln(arg))

    public override fun exp(arg: Complex): Complex = exp(arg.re) * (cos(arg.im) + i * sin(arg.im))
    public override fun ln(arg: Complex): Complex = ln(arg.r) + i * atan2(arg.im, arg.re)
    public override operator fun Number.plus(b: Complex): Complex = add(toComplex(), b)
    public override operator fun Number.minus(b: Complex): Complex = add(toComplex(), -b)
    public override operator fun Complex.plus(b: Number): Complex = b + this
    public override operator fun Complex.minus(b: Number): Complex = add(this, -b.toComplex())
    public override operator fun Number.times(b: Complex): Complex = Complex(b.re * toDouble(), b.im * toDouble())
    public override operator fun Complex.unaryMinus(): Complex = Complex(-re, -im)
    public override fun norm(arg: Complex): Complex = sqrt(arg.conjugate * arg)
    public override fun symbol(value: String): Complex = if (value == "i") i else super.symbol(value)
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
    public constructor(re: Number) : this(re.toDouble(), 0.0)

    public override val context: ComplexField
        get() = ComplexField

    init {
        require(!re.isNaN()) { "Real component of complex is not-a-number" }
        require(!im.isNaN()) { "Imaginary component of complex is not-a-number" }
    }

    public override fun unwrap(): Complex = this
    public override fun Complex.wrap(): Complex = this
    public override fun compareTo(other: Complex): Int = r.compareTo(other.r)
    public override fun toString(): String = "($re + $im * i)"
    public override fun minus(b: Complex): Complex = Complex(re - b.re, im - b.im)

    public companion object : MemorySpec<Complex> {
        public override val objectSize: Int
            get() = 16

        public override fun MemoryReader.read(offset: Int): Complex =
            Complex(readDouble(offset), readDouble(offset + 8))

        public override fun MemoryWriter.write(offset: Int, value: Complex) {
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
