package scientifik.kmath.operations

import scientifik.kmath.structures.*
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

object JBigIntegerField : Field<BigInteger> {
    override val zero: BigInteger = BigInteger.ZERO
    override val one: BigInteger = BigInteger.ONE

    override fun add(a: BigInteger, b: BigInteger): BigInteger = a.add(b)

    override fun multiply(a: BigInteger, k: Number): BigInteger = a.multiply(k.toInt().toBigInteger())

    override fun multiply(a: BigInteger, b: BigInteger): BigInteger = a.multiply(b)

    override fun divide(a: BigInteger, b: BigInteger): BigInteger = a.div(b)
}

class JBigDecimalField(val mathContext: MathContext = MathContext.DECIMAL64) : Field<BigDecimal> {
    override val zero: BigDecimal = BigDecimal.ZERO
    override val one: BigDecimal = BigDecimal.ONE

    override fun add(a: BigDecimal, b: BigDecimal): BigDecimal = a.add(b)

    override fun multiply(a: BigDecimal, k: Number): BigDecimal =
        a.multiply(k.toDouble().toBigDecimal(mathContext), mathContext)

    override fun multiply(a: BigDecimal, b: BigDecimal): BigDecimal = a.multiply(b, mathContext)
    override fun divide(a: BigDecimal, b: BigDecimal): BigDecimal = a.divide(b, mathContext)
}