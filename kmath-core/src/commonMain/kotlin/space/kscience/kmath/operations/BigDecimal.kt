/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.nd.BufferedRingOpsND
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MutableBufferFactory
import kotlin.math.max
import kotlin.math.pow as kpow

private val POWERS_OF_TEN: Array<BigInt> = Array(65) { i ->
    if (i == 0) BigInt.ONE
    else BigIntField.power(10.toBigInt(), i.toUInt())
}

internal fun pow10(n: Int): BigInt {
    require(n >= 0) { "Exponent must be non-negative: $n" }
    return if (n < POWERS_OF_TEN.size) {
        POWERS_OF_TEN[n]
    } else {
        BigIntField.power(10.toBigInt(), n.toUInt())
    }
}

/**
 * Converts this [BigInt] to its decimal [String] representation.
 */
public fun BigInt.toDecimalString(): String {
    if (this == BigInt.ZERO) return "0"
    val isNegative = this < BigInt.ZERO
    var temp = this.abs()
    val chunks = mutableListOf<UInt>()
    while (temp > BigInt.ZERO) {
        val rem = temp % 1_000_000_000
        chunks.add(kotlin.math.abs(rem).toUInt())
        temp /= 1_000_000_000U
    }
    val sb = StringBuilder()
    if (isNegative) sb.append('-')
    for (i in chunks.indices.reversed()) {
        val s = chunks[i].toString()
        if (i == chunks.lastIndex) {
            sb.append(s)
        } else {
            sb.append(s.padStart(9, '0'))
        }
    }
    return sb.toString()
}

/**
 * Converts this [BigInt] to [Long].
 */
public fun BigInt.toLong(): Long = when {
    this == BigInt.ZERO -> 0L
    else -> toDecimalString().toLongOrNull() ?: if (this < BigInt.ZERO) Long.MIN_VALUE else Long.MAX_VALUE
}

/**
 * Converts this [BigInt] to [Int].
 */
public fun BigInt.toInt(): Int = toLong().toInt()

/**
 * Converts this [BigInt] to [Double].
 */
public fun BigInt.toDouble(): Double = toDecimalString().toDouble()

/**
 * Converts this [BigInt] to [BigDecimal].
 */
public fun BigInt.toBigDecimal(): BigDecimal = BigDecimal(this, 0)

/**
 * An abstract field over [BigDecimal].
 *
 * @property decimalPrecision the decimal precision to use for division and powers.
 */
@OptIn(UnstableKMathAPI::class)
public abstract class BigDecimalFieldBase internal constructor(
    public val decimalPrecision: Int = DEFAULT_DECIMAL_PRECISION,
) : ExtendedField<BigDecimal>, NumbersAddOps<BigDecimal>, ScaleOperations<BigDecimal>, Norm<BigDecimal, BigDecimal> {

    override val bufferFactory: MutableBufferFactory<BigDecimal> = MutableBufferFactory()
    override val zero: BigDecimal get() = BigDecimal.ZERO
    override val one: BigDecimal get() = BigDecimal.ONE

    override fun number(value: Number): BigDecimal = when (value) {
        is Long -> value.toBigDecimal()
        is Int -> value.toBigDecimal()
        is Short -> value.toInt().toBigDecimal()
        is Byte -> value.toInt().toBigDecimal()
        is Double -> value.toBigDecimal()
        is Float -> value.toDouble().toBigDecimal()
        else -> value.toDouble().toBigDecimal()
    }

    override fun add(left: BigDecimal, right: BigDecimal): BigDecimal = left + right
    override fun multiply(left: BigDecimal, right: BigDecimal): BigDecimal = left * right
    override fun divide(left: BigDecimal, right: BigDecimal): BigDecimal = left.divide(right, decimalPrecision)

    override fun scale(a: BigDecimal, value: Double): BigDecimal = a * value.toBigDecimal()

    override fun sin(arg: BigDecimal): BigDecimal = kotlin.math.sin(arg.toDouble()).toBigDecimal()
    override fun cos(arg: BigDecimal): BigDecimal = kotlin.math.cos(arg.toDouble()).toBigDecimal()
    override fun asin(arg: BigDecimal): BigDecimal = kotlin.math.asin(arg.toDouble()).toBigDecimal()
    override fun acos(arg: BigDecimal): BigDecimal = kotlin.math.acos(arg.toDouble()).toBigDecimal()
    override fun atan(arg: BigDecimal): BigDecimal = kotlin.math.atan(arg.toDouble()).toBigDecimal()

    override fun exp(arg: BigDecimal): BigDecimal = kotlin.math.exp(arg.toDouble()).toBigDecimal()
    override fun ln(arg: BigDecimal): BigDecimal = kotlin.math.ln(arg.toDouble()).toBigDecimal()

    override fun sqrt(arg: BigDecimal): BigDecimal = when {
        arg < zero -> throw IllegalArgumentException("Square root of negative number: $arg")
        else -> kotlin.math.sqrt(arg.toDouble()).toBigDecimal()
    }

    override fun power(arg: BigDecimal, pow: Number): BigDecimal = when {
        pow.isInteger() -> arg.pow(pow.toInt(), decimalPrecision)
        arg < zero -> throw IllegalArgumentException("Can't raise negative $arg to a fractional power $pow")
        else -> arg.toDouble().kpow(pow.toDouble()).toBigDecimal()
    }

    override fun norm(arg: BigDecimal): BigDecimal = arg.abs()

    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    override fun BigDecimal.unaryMinus(): BigDecimal = -this
    override operator fun BigDecimal.plus(arg: BigDecimal): BigDecimal = this + arg
    override operator fun BigDecimal.minus(arg: BigDecimal): BigDecimal = this - arg
    override operator fun BigDecimal.times(arg: BigDecimal): BigDecimal = this * arg
    override operator fun BigDecimal.div(arg: BigDecimal): BigDecimal = divide(arg, decimalPrecision)

    public operator fun String.unaryPlus(): BigDecimal =
        this.parseBigDecimal() ?: error("Can't parse $this as big decimal")

    public operator fun String.unaryMinus(): BigDecimal =
        -(this.parseBigDecimal() ?: error("Can't parse $this as big decimal"))

    public companion object {
        public const val DEFAULT_DECIMAL_PRECISION: Int = 34
    }
}

/**
 * A field over [BigDecimal].
 */
public open class BigDecimalField(
    decimalPrecision: Int = DEFAULT_DECIMAL_PRECISION,
) : BigDecimalFieldBase(decimalPrecision) {
    public companion object : BigDecimalField()
}

/**
 * Kotlin Multiplatform implementation of Big Decimal numbers.
 */
public class BigDecimal(
    public val value: BigInt,
    public val scale: Int = 0,
) : Comparable<BigDecimal> {

    public constructor(value: Long, scale: Int = 0) : this(value.toBigInt(), scale)
    public constructor(value: Int, scale: Int = 0) : this(value.toBigInt(), scale)

    public val unscaledValue: BigInt get() = value

    public val signum: Int
        get() = when {
            value > BigInt.ZERO -> 1
            value < BigInt.ZERO -> -1
            else -> 0
        }

    public val sign: Byte get() = signum.toByte()

    public fun abs(): BigDecimal = if (value >= BigInt.ZERO) this else BigDecimal(-value, scale)

    public operator fun unaryMinus(): BigDecimal = BigDecimal(-value, scale)

    public operator fun plus(other: BigDecimal): BigDecimal {
        if (this.value == BigInt.ZERO) return other
        if (other.value == BigInt.ZERO) return this
        val diff = this.scale - other.scale
        return when {
            diff == 0 -> BigDecimal(this.value + other.value, this.scale)
            diff > 0 -> BigDecimal(this.value + other.value * pow10(diff), this.scale)
            else -> BigDecimal(this.value * pow10(-diff) + other.value, other.scale)
        }
    }

    public operator fun minus(other: BigDecimal): BigDecimal = this + (-other)

    public operator fun times(other: BigDecimal): BigDecimal =
        BigDecimal(this.value * other.value, this.scale + other.scale)

    public fun divide(
        other: BigDecimal,
        decimalPrecision: Int = BigDecimalFieldBase.DEFAULT_DECIMAL_PRECISION,
    ): BigDecimal {
        if (other.value == BigInt.ZERO) throw ArithmeticException("/ by zero")
        if (this.value == BigInt.ZERO) return ZERO

        val targetScale = max(this.scale - other.scale, 0) + decimalPrecision
        val shift = targetScale + other.scale - this.scale
        val num = if (shift >= 0) this.value * pow10(shift) else this.value
        val den = if (shift < 0) other.value * pow10(-shift) else other.value

        val quotient = num / den
        val remainder = num % den

        val numSign = when {
            num > BigInt.ZERO -> 1; num < BigInt.ZERO -> -1; else -> 0
        }
        val denSign = when {
            den > BigInt.ZERO -> 1; den < BigInt.ZERO -> -1; else -> 0
        }
        val finalQuotient = if (remainder != BigInt.ZERO && remainder.abs() * 2.toBigInt() >= den.abs()) {
            if (numSign * denSign > 0) quotient + BigInt.ONE else quotient - BigInt.ONE
        } else {
            quotient
        }

        return BigDecimal(finalQuotient, targetScale).stripTrailingZeros()
    }

    public operator fun div(other: BigDecimal): BigDecimal = divide(other)

    public fun pow(
        exponent: Int,
        decimalPrecision: Int = BigDecimalFieldBase.DEFAULT_DECIMAL_PRECISION,
    ): BigDecimal = when {
        exponent == 0 -> ONE
        exponent == 1 -> this
        exponent > 0 -> {
            if (value == BigInt.ZERO) ZERO
            else BigDecimal(value.pow(exponent.toUInt()), scale * exponent)
        }

        else -> ONE.divide(pow(-exponent, decimalPrecision), decimalPrecision)
    }

    public fun stripTrailingZeros(): BigDecimal {
        if (value == BigInt.ZERO) return ZERO
        var v = value
        var s = scale
        while (v % 10 == 0) {
            v /= 10
            s -= 1
        }
        return BigDecimal(v, s)
    }

    override fun compareTo(other: BigDecimal): Int {
        if (this.value == BigInt.ZERO && other.value == BigInt.ZERO) return 0
        if (this.value == BigInt.ZERO) return if (other.value > BigInt.ZERO) -1 else 1
        if (other.value == BigInt.ZERO) return if (this.value > BigInt.ZERO) 1 else -1

        val thisSign = this.signum
        val otherSign = other.signum
        if (thisSign != otherSign) return thisSign.compareTo(otherSign)

        val diff = this.scale - other.scale
        return when {
            diff == 0 -> this.value.compareTo(other.value)
            diff > 0 -> this.value.compareTo(other.value * pow10(diff))
            else -> (this.value * pow10(-diff)).compareTo(other.value)
        }
    }

    override fun equals(other: Any?): Boolean = other is BigDecimal && compareTo(other) == 0

    override fun hashCode(): Int {
        if (value == BigInt.ZERO) return 0
        var v = value
        var s = scale
        while (v % 10 == 0) {
            v /= 10
            s -= 1
        }
        return v.hashCode() * 31 + s.hashCode()
    }

    override fun toString(): String {
        if (value == BigInt.ZERO) return "0"
        val isNegative = value < BigInt.ZERO
        val absVal = value.abs()
        val str = absVal.toDecimalString()
        val formatted = when {
            scale == 0 -> str
            scale < 0 -> str + "0".repeat(-scale)
            else -> {
                if (str.length > scale) {
                    val intPart = str.substring(0, str.length - scale)
                    val fracPart = str.substring(str.length - scale)
                    "$intPart.$fracPart"
                } else {
                    val zeros = "0".repeat(scale - str.length)
                    "0.$zeros$str"
                }
            }
        }
        return if (isNegative) "-$formatted" else formatted
    }

    public fun toDouble(): Double = toString().toDouble()

    public fun toFloat(): Float = toDouble().toFloat()

    public fun toBigInt(): BigInt = when {
        scale == 0 -> value
        scale < 0 -> value * pow10(-scale)
        else -> value / pow10(scale)
    }

    public fun toLong(): Long = toBigInt().toLong()

    public fun toInt(): Int = toBigInt().toInt()

    public companion object {
        public val ZERO: BigDecimal = BigDecimal(BigInt.ZERO, 0)
        public val ONE: BigDecimal = BigDecimal(BigInt.ONE, 0)
        public val TEN: BigDecimal = BigDecimal(10.toBigInt(), 0)

        public fun valueOf(value: Long, scale: Int = 0): BigDecimal = BigDecimal(value, scale)
        public fun valueOf(value: Double): BigDecimal = context(BigDecimalField) { value.toBigDecimal() }
    }
}

/**
 * Returns the absolute value of the given value [x].
 */
public fun abs(x: BigDecimal): BigDecimal = x.abs()

/**
 * Convert this [Int] to [BigDecimal].
 */
public fun Int.toBigDecimal(): BigDecimal = BigDecimal(this.toBigInt(), 0)

/**
 * Convert this [Long] to [BigDecimal].
 */
public fun Long.toBigDecimal(): BigDecimal = BigDecimal(this.toBigInt(), 0)

/**
 * Convert this [Double] to [BigDecimal].
 */
context(_: BigDecimalFieldBase)
public fun Double.toBigDecimal(): BigDecimal {
    require(!isNaN() && !isInfinite()) { "Cannot convert $this to BigDecimal" }
    return toString().parseBigDecimal() ?: error("Can't parse Double $this as BigDecimal")
}

/**
 * Convert this [Float] to [BigDecimal].
 */
context(_: BigDecimalFieldBase)
public fun Float.toBigDecimal(): BigDecimal = toDouble().toBigDecimal()

/**
 * Convert this [String] to [BigDecimal].
 */
context(_: BigDecimalFieldBase)
public fun String.toBigDecimal(): BigDecimal =
    parseBigDecimal() ?: error("Can't parse $this as BigDecimal")

/**
 * Returns `null` if a valid number cannot be read from a string.
 */
context(_: BigDecimalFieldBase)
public fun String.parseBigDecimal(): BigDecimal? = context(BigIntField){
    val trimmed = this.trim()
    if (trimmed.isEmpty()) return null

    val (sign, signOffset) = when (trimmed[0]) {
        '+' -> 1 to 1
        '-' -> -1 to 1
        else -> 1 to 0
    }
    if (trimmed.length == signOffset) return null

    val expIdx = trimmed.indexOfAny(charArrayOf('e', 'E'), startIndex = signOffset)
    val expVal = if (expIdx != -1) {
        val expStr = trimmed.substring(expIdx + 1).replace("_", "")
        expStr.toIntOrNull() ?: return null
    } else {
        0
    }

    val mainPart = if (expIdx != -1) trimmed.substring(signOffset, expIdx) else trimmed.substring(signOffset)
    val dotIdx = mainPart.indexOf('.')

    val (unscaledBigInt, fracDigits) = if (dotIdx != -1) {
        val intPartStr = mainPart.substring(0, dotIdx).replace("_", "")
        val fracPartStr = mainPart.substring(dotIdx + 1).replace("_", "")
        if (intPartStr.isEmpty() && fracPartStr.isEmpty()) return null
        if (intPartStr.any { it !in '0'..'9' } || fracPartStr.any { it !in '0'..'9' }) return null
        val combinedStr = (if (intPartStr.isEmpty()) "0" else intPartStr) + fracPartStr
        val unscaled = combinedStr.parseBigInteger() ?: return null
        unscaled to fracPartStr.length
    } else {
        val intPartStr = mainPart.replace("_", "")
        if (intPartStr.isEmpty() || intPartStr.any { it !in '0'..'9' }) return null
        val unscaled = intPartStr.parseBigInteger() ?: return null
        unscaled to 0
    }

    val totalScale = fracDigits - expVal
    val signedUnscaled = if (sign == -1) -unscaledBigInt else unscaledBigInt
    return BigDecimal(signedUnscaled, totalScale)
}

@Suppress("UnusedReceiverParameter")
public val BigDecimal.algebra: BigDecimalField get() = BigDecimalField

public inline fun BigDecimal.Companion.buffer(size: Int, initializer: (Int) -> BigDecimal): Buffer<BigDecimal> =
    Buffer(size, initializer)

public inline fun BigDecimal.Companion.mutableBuffer(size: Int, initializer: (Int) -> BigDecimal): Buffer<BigDecimal> =
    Buffer(size, initializer)

@Suppress("UnusedReceiverParameter")
public val BigDecimalField.nd: BufferedRingOpsND<BigDecimal, BigDecimalField>
    get() = BufferedRingOpsND(BufferRingOps(BigDecimalField))
