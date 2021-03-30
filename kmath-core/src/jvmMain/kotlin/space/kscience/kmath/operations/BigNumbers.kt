package space.kscience.kmath.operations

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

/**
 * A field over [BigInteger].
 */
public object JBigIntegerField : Ring<BigInteger>, NumericAlgebra<BigInteger> {
    public override val zero: BigInteger get() = BigInteger.ZERO

    public override val one: BigInteger get() = BigInteger.ONE

    public override fun number(value: Number): BigInteger = BigInteger.valueOf(value.toLong())
    public override fun add(a: BigInteger, b: BigInteger): BigInteger = a.add(b)
    public override operator fun BigInteger.minus(b: BigInteger): BigInteger = subtract(b)
    public override fun multiply(a: BigInteger, b: BigInteger): BigInteger = a.multiply(b)

    public override operator fun BigInteger.unaryMinus(): BigInteger = negate()
}

/**
 * An abstract field over [BigDecimal].
 *
 * @property mathContext the [MathContext] to use.
 */
public abstract class JBigDecimalFieldBase internal constructor(
    private val mathContext: MathContext = MathContext.DECIMAL64,
) : Field<BigDecimal>, PowerOperations<BigDecimal>, NumericAlgebra<BigDecimal>, ScaleOperations<BigDecimal> {
    public override val zero: BigDecimal
        get() = BigDecimal.ZERO

    public override val one: BigDecimal
        get() = BigDecimal.ONE

    public override fun add(a: BigDecimal, b: BigDecimal): BigDecimal = a.add(b)
    public override operator fun BigDecimal.minus(b: BigDecimal): BigDecimal = subtract(b)
    public override fun number(value: Number): BigDecimal = BigDecimal.valueOf(value.toDouble())

    public override fun scale(a: BigDecimal, value: Double): BigDecimal =
        a.multiply(value.toBigDecimal(mathContext), mathContext)

    public override fun multiply(a: BigDecimal, b: BigDecimal): BigDecimal = a.multiply(b, mathContext)
    public override fun divide(a: BigDecimal, b: BigDecimal): BigDecimal = a.divide(b, mathContext)
    public override fun power(arg: BigDecimal, pow: Number): BigDecimal = arg.pow(pow.toInt(), mathContext)
    public override fun sqrt(arg: BigDecimal): BigDecimal = arg.sqrt(mathContext)
    public override operator fun BigDecimal.unaryMinus(): BigDecimal = negate(mathContext)
}

/**
 * A field over [BigDecimal].
 */
public class JBigDecimalField(mathContext: MathContext = MathContext.DECIMAL64) : JBigDecimalFieldBase(mathContext) {
    public companion object : JBigDecimalFieldBase()
}
