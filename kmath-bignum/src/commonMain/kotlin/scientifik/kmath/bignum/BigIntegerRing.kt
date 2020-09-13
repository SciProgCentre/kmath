package scientifik.kmath.bignum

import com.ionspin.kotlin.bignum.integer.BigInteger
import scientifik.kmath.operations.Ring

object BigIntegerRing : Ring<BigInteger> {
    override val zero: BigInteger
        get() = BigInteger.ZERO

    override val one: BigInteger
        get() = BigInteger.ONE

    override fun number(value: Number): BigInteger = BigInteger.fromLong(value.toLong())
    override fun add(a: BigInteger, b: BigInteger): BigInteger = a + b
    override fun multiply(a: BigInteger, k: Number): BigInteger = a * k.toLong()
    override fun multiply(a: BigInteger, b: BigInteger): BigInteger = a * b
    override fun BigInteger.plus(b: Number): BigInteger = plus(b.toLong())
    override fun BigInteger.minus(b: Number): BigInteger = minus(b.toLong())
    override fun BigInteger.div(k: Number): BigInteger = div(k.toLong())
    override fun BigInteger.times(k: Number): BigInteger = times(k.toLong())
}
