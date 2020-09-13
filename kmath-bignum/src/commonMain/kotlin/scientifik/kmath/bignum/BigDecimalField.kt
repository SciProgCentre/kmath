package scientifik.kmath.bignum

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import scientifik.kmath.operations.Field

abstract class BigDecimalFieldBase internal constructor(val mode: DecimalMode = DEFAULT_MODE) :
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
    override fun BigDecimal.minus(b: BigDecimal): BigDecimal = subtract(b, mode)
    override fun BigDecimal.times(k: Number): BigDecimal = multiply(number(k), mode)
    override fun BigDecimal.plus(b: Number): BigDecimal = add(number(b), mode)
    override fun BigDecimal.minus(b: Number): BigDecimal = subtract(number(b), mode)
    override fun BigDecimal.div(k: Number): BigDecimal = divide(number(k), mode)

    companion object {
        internal val DEFAULT_MODE = DecimalMode(16, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
    }
}

class BigDecimalField(mode: DecimalMode = DEFAULT_MODE) : BigDecimalFieldBase(mode) {
    companion object : BigDecimalFieldBase()
}
