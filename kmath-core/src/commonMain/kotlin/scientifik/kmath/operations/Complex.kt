package scientifik.kmath.operations

import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.MemoryBuffer
import scientifik.kmath.structures.MutableBuffer
import scientifik.memory.MemoryReader
import scientifik.memory.MemorySpec
import scientifik.memory.MemoryWriter
import kotlin.math.*

private val PI_DIV_2 = Complex(PI / 2, 0)

/**
 * A field for complex numbers
 */
object ComplexField : ExtendedField<Complex> {
    override val zero: Complex = Complex(0.0, 0.0)

    override val one: Complex = Complex(1.0, 0.0)

    val i = Complex(0.0, 1.0)

    override fun add(a: Complex, b: Complex): Complex = Complex(a.re + b.re, a.im + b.im)

    override fun multiply(a: Complex, k: Number): Complex = Complex(a.re * k.toDouble(), a.im * k.toDouble())

    override fun multiply(a: Complex, b: Complex): Complex =
        Complex(a.re * b.re - a.im * b.im, a.re * b.im + a.im * b.re)

    override fun divide(a: Complex, b: Complex): Complex {
        val norm = b.re * b.re + b.im * b.im
        return Complex((a.re * b.re + a.im * b.im) / norm, (a.re * b.im - a.im * b.re) / norm)
    }

    override fun sin(arg: Complex): Complex = i * (exp(-i * arg) - exp(i * arg)) / 2
    override fun cos(arg: Complex): Complex = (exp(-i * arg) + exp(i * arg)) / 2
    override fun asin(arg: Complex): Complex = -i * ln(sqrt(one - arg pow 2) + i * arg)
    override fun acos(arg: Complex): Complex = PI_DIV_2 + i * ln(sqrt(one - arg pow 2) + i * arg)
    override fun atan(arg: Complex): Complex = i * (ln(one - i * arg) - ln(one + i * arg)) / 2

    override fun power(arg: Complex, pow: Number): Complex =
        arg.r.pow(pow.toDouble()) * (cos(pow.toDouble() * arg.theta) + i * sin(pow.toDouble() * arg.theta))

    override fun exp(arg: Complex): Complex = exp(arg.re) * (cos(arg.im) + i * sin(arg.im))

    override fun ln(arg: Complex): Complex = ln(arg.r) + i * atan2(arg.im, arg.re)

    operator fun Double.plus(c: Complex) = add(this.toComplex(), c)

    operator fun Double.minus(c: Complex) = add(this.toComplex(), -c)

    operator fun Complex.plus(d: Double) = d + this

    operator fun Complex.minus(d: Double) = add(this, -d.toComplex())

    operator fun Double.times(c: Complex) = Complex(c.re * this, c.im * this)

    override fun symbol(value: String): Complex = if (value == "i") {
        i
    } else {
        super.symbol(value)
    }
}

/**
 * Complex number class
 */
data class Complex(val re: Double, val im: Double) : FieldElement<Complex, Complex, ComplexField>, Comparable<Complex> {
    constructor(re: Number, im: Number) : this(re.toDouble(), im.toDouble())

    override fun unwrap(): Complex = this

    override fun Complex.wrap(): Complex = this

    override val context: ComplexField get() = ComplexField

    override fun compareTo(other: Complex): Int = r.compareTo(other.r)

    companion object : MemorySpec<Complex> {
        override val objectSize: Int = 16

        override fun MemoryReader.read(offset: Int): Complex =
            Complex(readDouble(offset), readDouble(offset + 8))

        override fun MemoryWriter.write(offset: Int, value: Complex) {
            writeDouble(offset, value.re)
            writeDouble(offset + 8, value.im)
        }
    }
}

/**
 * A complex conjugate
 */
val Complex.conjugate: Complex get() = Complex(re, -im)

/**
 * Absolute value of complex number
 */
val Complex.r: Double get() = sqrt(re * re + im * im)

/**
 * An angle between vector represented by complex number and X axis
 */
val Complex.theta: Double get() = atan(im / re)

fun Double.toComplex() = Complex(this, 0.0)

inline fun Buffer.Companion.complex(size: Int, crossinline init: (Int) -> Complex): Buffer<Complex> {
    return MemoryBuffer.create(Complex, size, init)
}

inline fun MutableBuffer.Companion.complex(size: Int, crossinline init: (Int) -> Complex): Buffer<Complex> {
    return MemoryBuffer.create(Complex, size, init)
}
