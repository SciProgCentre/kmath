package scientifik.kmath.bignum

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import scientifik.kmath.operations.Field

abstract class BigDecimalFieldBase internal constructor(val mode: DecimalMode = DecimalMode.DEFAULT) :
    Field<BigDecimal> {
    override val zero: BigDecimal
        get() = BigDecimal.ZERO

    override val one: BigDecimal
        get() = BigDecimal.ONE

    override fun number(value: Number): BigDecimal = BigDecimal.fromDouble(value.toDouble(), mode)
    override fun add(a: BigDecimal, b: BigDecimal): BigDecimal = a.add(b, mode)
    override fun multiply(a: BigDecimal, k: Number): BigDecimal = a.times(number(k))
    override fun multiply(a: BigDecimal, b: BigDecimal): BigDecimal = a.times(b)
    override fun divide(a: BigDecimal, b: BigDecimal): BigDecimal = a.divide(b)
    override fun BigDecimal.times(k: Number): BigDecimal = times(number(k))
    override fun BigDecimal.plus(b: Number): BigDecimal = plus(number(b))
    override fun BigDecimal.minus(b: Number): BigDecimal = minus(number(b))
    override fun BigDecimal.div(k: Number): BigDecimal = div(number(k))
}

class BigDecimalField(mode: DecimalMode = DecimalMode.DEFAULT) : BigDecimalFieldBase(mode) {
    companion object : BigDecimalFieldBase()
}
