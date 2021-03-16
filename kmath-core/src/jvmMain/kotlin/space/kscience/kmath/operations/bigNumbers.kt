/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.operations

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

/**
 * A ring over [BigInteger].
 */
public object JBigIntegerRing : Ring<BigInteger>, NumericAlgebra<BigInteger> {
    override val zero: BigInteger get() = BigInteger.ZERO

    override val one: BigInteger get() = BigInteger.ONE

    override fun number(value: Number): BigInteger = BigInteger.valueOf(value.toLong())
    override fun add(a: BigInteger, b: BigInteger): BigInteger = a.add(b)
    override operator fun BigInteger.minus(b: BigInteger): BigInteger = subtract(b)
    override fun multiply(a: BigInteger, b: BigInteger): BigInteger = a.multiply(b)

    override operator fun BigInteger.unaryMinus(): BigInteger = negate()
}

/**
 * An abstract field over [BigDecimal].
 *
 * @property mathContext the [MathContext] to use.
 */
public abstract class JBigDecimalFieldBase internal constructor(
    private val mathContext: MathContext = MathContext.DECIMAL64,
) : Field<BigDecimal>, PowerOperations<BigDecimal>, NumericAlgebra<BigDecimal>, ScaleOperations<BigDecimal> {
    override val zero: BigDecimal
        get() = BigDecimal.ZERO

    override val one: BigDecimal
        get() = BigDecimal.ONE

    override fun add(a: BigDecimal, b: BigDecimal): BigDecimal = a.add(b)
    override operator fun BigDecimal.minus(b: BigDecimal): BigDecimal = subtract(b)
    override fun number(value: Number): BigDecimal = BigDecimal.valueOf(value.toDouble())

    override fun scale(a: BigDecimal, value: Double): BigDecimal =
        a.multiply(value.toBigDecimal(mathContext), mathContext)

    override fun multiply(a: BigDecimal, b: BigDecimal): BigDecimal = a.multiply(b, mathContext)
    override fun divide(a: BigDecimal, b: BigDecimal): BigDecimal = a.divide(b, mathContext)
    override fun power(arg: BigDecimal, pow: Number): BigDecimal = arg.pow(pow.toInt(), mathContext)
    override fun sqrt(arg: BigDecimal): BigDecimal = arg.sqrt(mathContext)
    override operator fun BigDecimal.unaryMinus(): BigDecimal = negate(mathContext)
}

/**
 * A field over [BigDecimal].
 */
public class JBigDecimalField(mathContext: MathContext = MathContext.DECIMAL64) : JBigDecimalFieldBase(mathContext) {
    public companion object : JBigDecimalFieldBase()
}
