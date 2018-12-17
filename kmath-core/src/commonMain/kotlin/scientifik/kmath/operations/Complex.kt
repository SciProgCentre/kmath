package scientifik.kmath.operations

/**
 * A field for complex numbers
 */
object ComplexField : Field<Complex> {
    override val zero: Complex = Complex(0.0, 0.0)

    override val one: Complex = Complex(1.0, 0.0)

    val i = Complex(0.0, 1.0)

    override fun add(a: Complex, b: Complex): Complex = Complex(a.re + b.re, a.im + b.im)

    override fun multiply(a: Complex, k: Double): Complex = Complex(a.re * k, a.im * k)

    override fun multiply(a: Complex, b: Complex): Complex = Complex(a.re * b.re - a.im * b.im, a.re * b.im + a.im * b.re)

    override fun divide(a: Complex, b: Complex): Complex = Complex(a.re * b.re + a.im * b.im, a.re * b.im - a.im * b.re) / b.square

}

/**
 * Complex number class
 */
data class Complex(val re: Double, val im: Double) : FieldElement<Complex, ComplexField> {
    override val self: Complex get() = this
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

    companion object {

    }
}

fun Double.toComplex() = Complex(this, 0.0)

operator fun Double.plus(c: Complex) = this.toComplex() + c

operator fun Double.minus(c: Complex) = this.toComplex() - c

operator fun Complex.plus(d: Double) = d + this

operator fun Complex.minus(d: Double) = this - d.toComplex()

operator fun Double.times(c: Complex) = Complex(c.re * this, c.im * this)