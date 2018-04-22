package scientifik.kmath.operations

import kotlin.math.sqrt

/**
 * Field for real values
 */
object RealField : Field<Real> {
    override val zero: Real = Real(0.0)
    override fun add(a: Real, b: Real): Real = Real(a.value + b.value)
    override val one: Real = Real(1.0)
    override fun multiply(a: Real, b: Real): Real = Real(a.value * b.value)
    override fun multiply(a: Real, k: Double): Real = Real(a.value * k)
    override fun divide(a: Real, b: Real): Real = Real(a.value / b.value)
}

/**
 * Real field element wrapping double
 */
class Real(val value: Double) : FieldElement<Real>, Number() {
    override fun toByte(): Byte = value.toByte()
    override fun toChar(): Char = value.toChar()
    override fun toDouble(): Double = value
    override fun toFloat(): Float = value.toFloat()
    override fun toInt(): Int = value.toInt()
    override fun toLong(): Long = value.toLong()
    override fun toShort(): Short = value.toShort()

    //values are dynamically calculated to save memory
    override val self
        get() = this
    override val context
        get() = RealField
}

/**
 * A field for complex numbers
 */
object ComplexField : Field<Complex> {
    override val zero: Complex = Complex(0.0, 0.0)

    override fun add(a: Complex, b: Complex): Complex = Complex(a.re + b.re, a.im + b.im)

    override fun multiply(a: Complex, k: Double): Complex = Complex(a.re * k, a.im * k)

    override val one: Complex = Complex(1.0, 0.0)

    override fun multiply(a: Complex, b: Complex): Complex = Complex(a.re * b.re - a.im * b.im, a.re * b.im + a.im * b.re)

    override fun divide(a: Complex, b: Complex): Complex = Complex(a.re * b.re + a.im * b.im, a.re * b.im - a.im * b.re) / b.square

}

/**
 * Complex number class
 */
data class Complex(val re: Double, val im: Double) : FieldElement<Complex> {
    override val self: Complex
        get() = this
    override val context: Field<Complex>
        get() = ComplexField

    /**
     * A complex conjugate
     */
    val conjugate: Complex
        get() = Complex(re, -im)

    val square: Double
        get() = re * re + im * im

    val module: Double
        get() = sqrt(square)


    //TODO is it convenient?
    operator fun not() = conjugate
}