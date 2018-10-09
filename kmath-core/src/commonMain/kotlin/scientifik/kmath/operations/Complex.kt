package scientifik.kmath.operations

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

}