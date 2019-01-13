package scientifik.kmath.operations

/**
 * A field for complex numbers
 */
object ComplexField : Field<Complex> {
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

    override val context: ComplexField
        get() = ComplexField

    /**
     * A complex conjugate
     */
    val conjugate: Complex
        get() = Complex(re, -im)

    val square: Double
        get() = re * re + im * im

    val abs: Double
        get() = kotlin.math.sqrt(square)

    companion object
}

fun Double.toComplex() = Complex(this, 0.0)