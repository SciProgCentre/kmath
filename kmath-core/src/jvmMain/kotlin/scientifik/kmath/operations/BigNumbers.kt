package scientifik.kmath.operations

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

/**
 * A field wrapper for Java [BigInteger]
 */
object JBigIntegerField : Field<BigInteger> {
    override val zero: BigInteger
        get() = BigInteger.ZERO

    override val one: BigInteger
        get() = BigInteger.ONE

    override fun number(value: Number): BigInteger = BigInteger.valueOf(value.toLong())
    override fun divide(a: BigInteger, b: BigInteger): BigInteger = a.div(b)
    override fun add(a: BigInteger, b: BigInteger): BigInteger = a.add(b)
    override fun BigInteger.minus(b: BigInteger): BigInteger = this.subtract(b)
    override fun multiply(a: BigInteger, k: Number): BigInteger = a.multiply(k.toInt().toBigInteger())
    override fun multiply(a: BigInteger, b: BigInteger): BigInteger = a.multiply(b)
    override fun BigInteger.unaryMinus(): BigInteger = negate()
}

/**
 * A Field wrapper for Java [BigDecimal]
 */
abstract class JBigDecimalFieldBase internal constructor(val mathContext: MathContext = MathContext.DECIMAL64) :
    Field<BigDecimal>,
    PowerOperations<BigDecimal> {
    override val zero: BigDecimal
        get() = BigDecimal.ZERO

    override val one: BigDecimal
        get() = BigDecimal.ONE

    override fun add(a: BigDecimal, b: BigDecimal): BigDecimal = a.add(b)
    override fun BigDecimal.minus(b: BigDecimal): BigDecimal = subtract(b)
    override fun number(value: Number): BigDecimal = BigDecimal.valueOf(value.toDouble())

    override fun multiply(a: BigDecimal, k: Number): BigDecimal =
        a.multiply(k.toDouble().toBigDecimal(mathContext), mathContext)

    override fun multiply(a: BigDecimal, b: BigDecimal): BigDecimal = a.multiply(b, mathContext)
    override fun divide(a: BigDecimal, b: BigDecimal): BigDecimal = a.divide(b, mathContext)
    override fun power(arg: BigDecimal, pow: Number): BigDecimal = arg.pow(pow.toInt(), mathContext)
    override fun sqrt(arg: BigDecimal): BigDecimal = arg.sqrt(mathContext)
    override fun BigDecimal.unaryMinus(): BigDecimal = negate(mathContext)

}

class JBigDecimalField(mathContext: MathContext = MathContext.DECIMAL64) : JBigDecimalFieldBase(mathContext) {
    companion object : JBigDecimalFieldBase()
}
