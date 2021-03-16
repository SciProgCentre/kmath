/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.complex

import space.kscience.kmath.operations.*
import kotlin.js.JsName
import kotlin.jvm.JvmName

/**
 * Represents generic complex value consisting of real and imaginary part.
 *
 * @param T the type of components.
 * @property re The real component.
 * @property im The imaginary component.
 */
public data class Complex<out T : Any>(public val re: T, public val im: T) {
    /**
     * Converts this complex number to string formatted like `[re] + i * [im]`.
     */
    override fun toString(): String = "$re + i * $im"
}

/**
 * The algebra of [Complex].
 *
 * @param T the type of components.
 * @property algebra the algebra over [T].
 */
public open class ComplexAlgebra<T : Any>(public open val algebra: NumericAlgebra<T>) : NumericAlgebra<Complex<T>> {
    /**
     * The imaginary unit.
     */
    public open val i: Complex<T> by lazy {
        algebra { Complex(number(0), number(1)) }
    }

    override fun number(value: Number): Complex<T> =
        algebra { Complex(algebra.number(value), algebra.number(0)) }

    override fun bindSymbol(value: String): Complex<T> = if (value == "i") i else super.bindSymbol(value)
}

/**
 * The group of [Complex].
 *
 * @param T the type of components.
 */
public open class ComplexGroup<T : Any, out A>(override val algebra: A) : ComplexAlgebra<T>(algebra),
    Group<Complex<T>> where A : NumericAlgebra<T>, A : Group<T> {
    override val zero: Complex<T> by lazy {
        algebra { Complex(zero, zero) }
    }

    /**
     * This complex's conjugate.
     */
    public val Complex<T>.conjugate: Complex<T>
        get() = Complex(re, algebra { -im })

    override fun add(a: Complex<T>, b: Complex<T>): Complex<T> = algebra { Complex(a.re + b.re, a.im + b.im) }

    override fun Complex<T>.unaryMinus(): Complex<T> = algebra { Complex(-re, -im) }

    @JsName("unaryMinus_T")
    public operator fun T.unaryMinus(): Complex<T> = algebra { Complex(-this@unaryMinus, zero) }

    @JsName("unaryPlus_T")
    public operator fun T.unaryPlus(): Complex<T> = algebra { Complex(this@unaryPlus, zero) }

    public operator fun T.plus(b: Complex<T>): Complex<T> = add(+this, b)
    public operator fun Complex<T>.plus(b: T): Complex<T> = add(this, +b)
    public operator fun T.minus(b: Complex<T>): Complex<T> = add(+this, -b)
    public operator fun Complex<T>.minus(b: T): Complex<T> = add(this, -b)
}

/**
 * The ring of [Complex].
 *
 * @param T the type of components.
 */
public open class ComplexRing<T : Any, out A>(override val algebra: A) : ComplexGroup<T, A>(algebra),
    Ring<Complex<T>> where A : NumericAlgebra<T>, A : Ring<T> {
    override val one: Complex<T> by lazy {
        algebra { Complex(one, zero) }
    }

    override val i: Complex<T> by lazy {
        algebra { Complex(zero, one) }
    }

    override fun multiply(a: Complex<T>, b: Complex<T>): Complex<T> =
        algebra { Complex(a.re * b.re - a.im * b.im, a.im * b.re + a.re * b.im) }

    public operator fun T.times(b: Complex<T>): Complex<T> = multiply(+this, b)
    public operator fun Complex<T>.times(b: T): Complex<T> = multiply(this, +b)
}

/**
 * [ComplexRing] instance for [ByteRing].
 */
public val ComplexByteRing: ComplexRing<Byte, ByteRing> = ComplexRing(ByteRing)

/**
 * [ComplexRing] instance for [ShortRing].
 */
public val ComplexShortRing: ComplexRing<Short, ShortRing> = ComplexRing(ShortRing)

/**
 * [ComplexRing] instance for [IntRing].
 */
public val ComplexIntRing: ComplexRing<Int, IntRing> = ComplexRing(IntRing)

/**
 * [ComplexRing] instance for [LongRing].
 */
public val ComplexLongRing: ComplexRing<Long, LongRing> = ComplexRing(LongRing)

/**
 * The field of [Complex].
 */
public open class ComplexField<T : Any, out A>(override val algebra: A) : ComplexRing<T, A>(algebra),
    Field<Complex<T>> where A : Field<T> {
    /**
     * This complex's reciprocal.
     */
    public val Complex<T>.reciprocal: Complex<T>
        get() = algebra {
            val scale = re * re + im * im
            Complex(re / scale, -im / scale)
        }

    override fun divide(a: Complex<T>, b: Complex<T>): Complex<T> = a * b.reciprocal

    override fun number(value: Number): Complex<T> = super<ComplexRing>.number(value)

    override fun scale(a: Complex<T>, value: Double): Complex<T> =
        algebra { Complex(a.re * value, a.im * value) }

    override operator fun Complex<T>.div(k: Number): Complex<T> =
        algebra { Complex(re / k.toDouble(), im / k.toDouble()) }

    public operator fun T.div(b: Complex<T>): Complex<T> = divide(+this, b)
    public operator fun Complex<T>.div(b: T): Complex<T> = divide(this, +b)

    @JsName("scale_T")
    public fun scale(a: T, value: Double): Complex<T> = scale(+a, value)
}


/**
 * [ComplexRing] instance for [BigIntField].
 */
public val ComplexBigIntField: ComplexField<BigInt, BigIntField> = ComplexField(BigIntField)


/**
 * The extended field of Complex.
 */
public open class ComplexExtendedField<T : Any, out A>(override val algebra: A) : ComplexField<T, A>(algebra),
    ExtendedField<Complex<T>>, Norm<Complex<T>, T> where A : ExtendedField<T> {
    private val two by lazy { one + one }

    /**
     * The *r* component of the polar form of this number.
     */
    public val Complex<T>.r: T
        get() = norm(this)

    /**
     * The *&theta;* component of the polar form of this number.
     */
    public val Complex<T>.theta: T
        get() = algebra { atan(im / re) }

    override fun bindSymbol(value: String): Complex<T> =
        if (value == "i") i else super<ExtendedField>.bindSymbol(value)

    override fun sin(arg: Complex<T>): Complex<T> = i * (exp(-i * arg) - exp(i * arg)) / two
    override fun cos(arg: Complex<T>): Complex<T> = (exp(-i * arg) + exp(i * arg)) / two

    override fun tan(arg: Complex<T>): Complex<T> {
        val e1 = exp(-i * arg)
        val e2 = exp(i * arg)
        return i * (e1 - e2) / (e1 + e2)
    }

    override fun asin(arg: Complex<T>): Complex<T> = -i * ln(sqrt(one - (arg * arg)) + i * arg)
    override fun acos(arg: Complex<T>): Complex<T> =
        (pi / two) + i * ln(sqrt(one - (arg * arg)) + i * arg)

    override fun atan(arg: Complex<T>): Complex<T> = algebra {
        val iArg = i * arg
        return i * (ln(this@ComplexExtendedField.one - iArg) - ln(this@ComplexExtendedField.one + iArg)) / 2
    }

    override fun power(arg: Complex<T>, pow: Number): Complex<T> = algebra {
        if (arg.im == 0.0)
            Complex(arg.re.pow(pow.toDouble()), algebra.zero)
        else
            exp(pow * ln(arg))
    }

    override fun exp(arg: Complex<T>): Complex<T> =
        Complex(algebra.exp(arg.re), algebra.zero) * Complex(algebra.cos(arg.im), algebra.sin(arg.im))

    override fun ln(arg: Complex<T>): Complex<T> = algebra { Complex(ln(norm(arg)), atan(arg.im / arg.re)) }
    override fun norm(arg: Complex<T>): T = algebra { sqrt(arg.re * arg.re + arg.im * arg.im) }

    @JsName("norm_T_3")
    @JvmName("norm\$T")
    public fun norm(arg: T): T = algebra { sqrt(arg * arg) }

    @JsName("sin_T")
    public fun sin(arg: T): Complex<T> = sin(+arg)

    @JsName("cos_T")
    public fun cos(arg: T): Complex<T> = cos(+arg)

    @JsName("tan_T")
    public fun tan(arg: T): Complex<T> = tan(+arg)

    @JsName("asin_T")
    public fun asin(arg: T): Complex<T> = asin(+arg)

    @JsName("acos_T")
    public fun acos(arg: T): Complex<T> = acos(+arg)

    @JsName("atan_T")
    public fun atan(arg: T): Complex<T> = atan(+arg)

    @JsName("power_T")
    public fun power(arg: T, pow: Number): Complex<T> = power(+arg, pow)

    @JsName("exp_T")
    public fun exp(arg: T): Complex<T> = exp(+arg)

    @JsName("ln_T")
    public fun ln(arg: T): Complex<T> = ln(+arg)
}

/**
 * [ComplexRing] instance for [DoubleField].
 */
public val ComplexDoubleField: ComplexExtendedField<Double, DoubleField> = ComplexExtendedField(DoubleField)

/**
 * [ComplexRing] instance for [FloatField].
 */
public val ComplexFloatField: ComplexExtendedField<Double, DoubleField> = ComplexExtendedField(DoubleField)
