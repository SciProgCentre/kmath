package scientifik.kmath.operations

import kotlin.math.*

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
        val norm = b.square
        return Complex((a.re * b.re + a.im * b.im) / norm, (a.re * b.im - a.im * b.re) / norm)
    }

    override fun sin(arg: Complex): Complex = i / 2 * (exp(-i * arg) - exp(i * arg))

    override fun cos(arg: Complex): Complex = (exp(-i * arg) + exp(i * arg)) / 2

    override fun power(arg: Complex, pow: Number): Complex =
        arg.abs.pow(pow.toDouble()) * (cos(pow.toDouble() * arg.theta) + i * sin(pow.toDouble() * arg.theta))

    override fun exp(arg: Complex): Complex = exp(arg.re) * (cos(arg.im) + i * sin(arg.im))

    override fun ln(arg: Complex): Complex = ln(arg.abs) + i * atan2(arg.im, arg.re)

    operator fun Double.plus(c: Complex) = add(this.toComplex(), c)

    operator fun Double.minus(c: Complex) = add(this.toComplex(), -c)

    operator fun Complex.plus(d: Double) = d + this

    operator fun Complex.minus(d: Double) = add(this, -d.toComplex())

    operator fun Double.times(c: Complex) = Complex(c.re * this, c.im * this)
}

/**
 * Complex number class
 */
data class Complex(val re: Double, val im: Double) : FieldElement<Complex, Complex, ComplexField> {
    override fun unwrap(): Complex = this

    override fun Complex.wrap(): Complex = this

    override val context: ComplexField get() = ComplexField

    /**
     * A complex conjugate
     */
    val conjugate: Complex get() = Complex(re, -im)

    val square: Double get() = re * re + im * im

    val abs: Double get() = sqrt(square)

    val theta: Double get() = atan(im / re)

    companion object
}

fun Double.toComplex() = Complex(this, 0.0)