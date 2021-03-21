package space.kscience.kmath.operations

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.AlgebraND
import space.kscience.kmath.nd.BufferedRingND
import space.kscience.kmath.operations.BigInt.Companion.BASE
import space.kscience.kmath.operations.BigInt.Companion.BASE_SIZE
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MutableBuffer
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

public typealias Magnitude = UIntArray
public typealias TBase = ULong

/**
 * Kotlin Multiplatform implementation of Big Integer numbers (KBigInteger).
 *
 * @author Robert Drynkin (https://github.com/robdrynkin) and Peter Klimai (https://github.com/pklimai)
 */
@OptIn(UnstableKMathAPI::class)
public object BigIntField : Field<BigInt>, NumbersAddOperations<BigInt>, ScaleOperations<BigInt> {
    override val zero: BigInt = BigInt.ZERO
    override val one: BigInt = BigInt.ONE

    override fun number(value: Number): BigInt = value.toLong().toBigInt()

    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    override fun BigInt.unaryMinus(): BigInt = -this
    override fun add(a: BigInt, b: BigInt): BigInt = a.plus(b)
    override fun scale(a: BigInt, value: Double): BigInt = a.times(number(value))
    override fun multiply(a: BigInt, b: BigInt): BigInt = a.times(b)
    override fun divide(a: BigInt, b: BigInt): BigInt = a.div(b)

    public operator fun String.unaryPlus(): BigInt = this.parseBigInteger() ?: error("Can't parse $this as big integer")
    public operator fun String.unaryMinus(): BigInt =
        -(this.parseBigInteger() ?: error("Can't parse $this as big integer"))
}

public class BigInt internal constructor(
    private val sign: Byte,
    private val magnitude: Magnitude,
) : Comparable<BigInt> {
    public override fun compareTo(other: BigInt): Int = when {
        (sign == 0.toByte()) and (other.sign == 0.toByte()) -> 0
        sign < other.sign -> -1
        sign > other.sign -> 1
        else -> sign * compareMagnitudes(magnitude, other.magnitude)
    }

    public override fun equals(other: Any?): Boolean =
        if (other is BigInt) compareTo(other) == 0 else error("Can't compare KBigInteger to a different type")

    public override fun hashCode(): Int = magnitude.hashCode() + sign

    public fun abs(): BigInt = if (sign == 0.toByte()) this else BigInt(1, magnitude)

    public operator fun unaryMinus(): BigInt =
        if (this.sign == 0.toByte()) this else BigInt((-this.sign).toByte(), this.magnitude)

    public operator fun plus(b: BigInt): BigInt = when {
        b.sign == 0.toByte() -> this
        sign == 0.toByte() -> b
        this == -b -> ZERO
        sign == b.sign -> BigInt(sign, addMagnitudes(magnitude, b.magnitude))

        else -> {
            val comp = compareMagnitudes(magnitude, b.magnitude)

            if (comp == 1)
                BigInt(sign, subtractMagnitudes(magnitude, b.magnitude))
            else
                BigInt((-sign).toByte(), subtractMagnitudes(b.magnitude, magnitude))
        }
    }

    public operator fun minus(b: BigInt): BigInt = this + (-b)

    public operator fun times(b: BigInt): BigInt = when {
        this.sign == 0.toByte() -> ZERO
        b.sign == 0.toByte() -> ZERO
//          TODO: Karatsuba
        else -> BigInt((this.sign * b.sign).toByte(), multiplyMagnitudes(this.magnitude, b.magnitude))
    }

    public operator fun times(other: UInt): BigInt = when {
        sign == 0.toByte() -> ZERO
        other == 0U -> ZERO
        else -> BigInt(sign, multiplyMagnitudeByUInt(magnitude, other))
    }

    public operator fun times(other: Int): BigInt = if (other > 0)
        this * kotlin.math.abs(other).toUInt()
    else
        -this * kotlin.math.abs(other).toUInt()

    public operator fun div(other: UInt): BigInt = BigInt(this.sign, divideMagnitudeByUInt(this.magnitude, other))

    public operator fun div(other: Int): BigInt = BigInt(
        (this.sign * other.sign).toByte(),
        divideMagnitudeByUInt(this.magnitude, kotlin.math.abs(other).toUInt())
    )

    private fun division(other: BigInt): Pair<BigInt, BigInt> {
        // Long division algorithm:
        //     https://en.wikipedia.org/wiki/Division_algorithm#Integer_division_(unsigned)_with_remainder
        // TODO: Implement more effective algorithm
        var q = ZERO
        var r = ZERO

        val bitSize =
            (BASE_SIZE * (this.magnitude.size - 1) + log2(this.magnitude.lastOrNull()?.toFloat() ?: 0f + 1)).toInt()

        for (i in bitSize downTo 0) {
            r = r shl 1
            r = r or ((abs(this) shr i) and ONE)

            if (r >= abs(other)) {
                r -= abs(other)
                q += (ONE shl i)
            }
        }

        return Pair(BigInt((this.sign * other.sign).toByte(), q.magnitude), r)
    }

    public operator fun div(other: BigInt): BigInt = division(other).first

    public infix fun shl(i: Int): BigInt {
        if (this == ZERO) return ZERO
        if (i == 0) return this
        val fullShifts = i / BASE_SIZE + 1
        val relShift = i % BASE_SIZE
        val shiftLeft = { x: UInt -> if (relShift >= 32) 0U else x shl relShift }
        val shiftRight = { x: UInt -> if (BASE_SIZE - relShift >= 32) 0U else x shr (BASE_SIZE - relShift) }
        val newMagnitude = Magnitude(magnitude.size + fullShifts)

        for (j in magnitude.indices) {
            newMagnitude[j + fullShifts - 1] = shiftLeft(this.magnitude[j])

            if (j != 0)
                newMagnitude[j + fullShifts - 1] = newMagnitude[j + fullShifts - 1] or shiftRight(this.magnitude[j - 1])
        }

        newMagnitude[magnitude.size + fullShifts - 1] = shiftRight(magnitude.last())
        return BigInt(this.sign, stripLeadingZeros(newMagnitude))
    }

    public infix fun shr(i: Int): BigInt {
        if (this == ZERO) return ZERO
        if (i == 0) return this
        val fullShifts = i / BASE_SIZE
        val relShift = i % BASE_SIZE
        val shiftRight = { x: UInt -> if (relShift >= 32) 0U else x shr relShift }
        val shiftLeft = { x: UInt -> if (BASE_SIZE - relShift >= 32) 0U else x shl (BASE_SIZE - relShift) }
        if (this.magnitude.size - fullShifts <= 0) return ZERO
        val newMagnitude: Magnitude = Magnitude(magnitude.size - fullShifts)

        for (j in fullShifts until magnitude.size) {
            newMagnitude[j - fullShifts] = shiftRight(magnitude[j])

            if (j != magnitude.size - 1)
                newMagnitude[j - fullShifts] = newMagnitude[j - fullShifts] or shiftLeft(magnitude[j + 1])
        }

        return BigInt(this.sign, stripLeadingZeros(newMagnitude))
    }

    public infix fun or(other: BigInt): BigInt {
        if (this == ZERO) return other
        if (other == ZERO) return this
        val resSize = max(magnitude.size, other.magnitude.size)
        val newMagnitude: Magnitude = Magnitude(resSize)

        for (i in 0 until resSize) {
            if (i < magnitude.size) newMagnitude[i] = newMagnitude[i] or magnitude[i]
            if (i < other.magnitude.size) newMagnitude[i] = newMagnitude[i] or other.magnitude[i]
        }

        return BigInt(1, stripLeadingZeros(newMagnitude))
    }

    public infix fun and(other: BigInt): BigInt {
        if ((this == ZERO) or (other == ZERO)) return ZERO
        val resSize = min(this.magnitude.size, other.magnitude.size)
        val newMagnitude: Magnitude = Magnitude(resSize)
        for (i in 0 until resSize) newMagnitude[i] = this.magnitude[i] and other.magnitude[i]
        return BigInt(1, stripLeadingZeros(newMagnitude))
    }

    public operator fun rem(other: Int): Int {
        val res = this - (this / other) * other
        return if (res == ZERO) 0 else res.sign * res.magnitude[0].toInt()
    }

    public operator fun rem(other: BigInt): BigInt = this - (this / other) * other

    public fun modPow(exponent: BigInt, m: BigInt): BigInt = when {
        exponent == ZERO -> ONE
        exponent % 2 == 1 -> (this * modPow(exponent - ONE, m)) % m

        else -> {
            val sqRoot = modPow(exponent / 2, m)
            (sqRoot * sqRoot) % m
        }
    }

    override fun toString(): String {
        if (this.sign == 0.toByte()) {
            return "0x0"
        }
        var res: String = if (this.sign == (-1).toByte()) "-0x" else "0x"
        var numberStarted = false

        for (i in this.magnitude.size - 1 downTo 0) {
            for (j in BASE_SIZE / 4 - 1 downTo 0) {
                val curByte = (this.magnitude[i] shr 4 * j) and 0xfU
                if (numberStarted or (curByte != 0U)) {
                    numberStarted = true
                    res += hexMapping[curByte]
                }
            }
        }

        return res
    }

    public companion object {
        public const val BASE: ULong = 0xffffffffUL
        public const val BASE_SIZE: Int = 32
        public val ZERO: BigInt = BigInt(0, uintArrayOf())
        public val ONE: BigInt = BigInt(1, uintArrayOf(1u))

        private val hexMapping: HashMap<UInt, String> = hashMapOf(
            0U to "0", 1U to "1", 2U to "2", 3U to "3",
            4U to "4", 5U to "5", 6U to "6", 7U to "7",
            8U to "8", 9U to "9", 10U to "a", 11U to "b",
            12U to "c", 13U to "d", 14U to "e", 15U to "f"
        )

        private fun compareMagnitudes(mag1: Magnitude, mag2: Magnitude): Int {
            when {
                mag1.size > mag2.size -> return 1
                mag1.size < mag2.size -> return -1
                else -> {
                    for (i in mag1.size - 1 downTo 0) {
                        if (mag1[i] > mag2[i]) {
                            return 1
                        } else if (mag1[i] < mag2[i]) {
                            return -1
                        }
                    }
                    return 0
                }
            }
        }

        private fun addMagnitudes(mag1: Magnitude, mag2: Magnitude): Magnitude {
            val resultLength = max(mag1.size, mag2.size) + 1
            val result = Magnitude(resultLength)
            var carry = 0uL

            for (i in 0 until resultLength - 1) {
                val res = when {
                    i >= mag1.size -> mag2[i].toULong() + carry
                    i >= mag2.size -> mag1[i].toULong() + carry
                    else -> mag1[i].toULong() + mag2[i].toULong() + carry
                }

                result[i] = (res and BASE).toUInt()
                carry = (res shr BASE_SIZE)
            }

            result[resultLength - 1] = carry.toUInt()
            return stripLeadingZeros(result)
        }

        private fun subtractMagnitudes(mag1: Magnitude, mag2: Magnitude): Magnitude {
            val resultLength = mag1.size
            val result = Magnitude(resultLength)
            var carry = 0L

            for (i in 0 until resultLength) {
                var res =
                    if (i < mag2.size) mag1[i].toLong() - mag2[i].toLong() - carry
                    else mag1[i].toLong() - carry

                carry = if (res < 0) 1 else 0
                res += carry * (BASE + 1UL).toLong()

                result[i] = res.toUInt()
            }

            return stripLeadingZeros(result)
        }

        private fun multiplyMagnitudeByUInt(mag: Magnitude, x: UInt): Magnitude {
            val resultLength = mag.size + 1
            val result = Magnitude(resultLength)
            var carry = 0uL

            for (i in mag.indices) {
                val cur: ULong = carry + mag[i].toULong() * x.toULong()
                result[i] = (cur and BASE).toUInt()
                carry = cur shr BASE_SIZE
            }
            result[resultLength - 1] = (carry and BASE).toUInt()

            return stripLeadingZeros(result)
        }

        private fun multiplyMagnitudes(mag1: Magnitude, mag2: Magnitude): Magnitude {
            val resultLength = mag1.size + mag2.size
            val result = Magnitude(resultLength)

            for (i in mag1.indices) {
                var carry = 0uL

                for (j in mag2.indices) {
                    val cur: ULong = result[i + j].toULong() + mag1[i].toULong() * mag2[j].toULong() + carry
                    result[i + j] = (cur and BASE).toUInt()
                    carry = cur shr BASE_SIZE
                }

                result[i + mag2.size] = (carry and BASE).toUInt()
            }

            return stripLeadingZeros(result)
        }

        private fun divideMagnitudeByUInt(mag: Magnitude, x: UInt): Magnitude {
            val resultLength = mag.size
            val result = Magnitude(resultLength)
            var carry = 0uL

            for (i in mag.size - 1 downTo 0) {
                val cur: ULong = mag[i].toULong() + (carry shl BASE_SIZE)
                result[i] = (cur / x).toUInt()
                carry = cur % x
            }

            return stripLeadingZeros(result)
        }
    }
}

private fun stripLeadingZeros(mag: Magnitude): Magnitude {
    if (mag.isEmpty() || mag.last() != 0U) return mag
    var resSize = mag.size - 1

    while (mag[resSize] == 0U) {
        if (resSize == 0) break
        resSize -= 1
    }

    return mag.sliceArray(IntRange(0, resSize))
}

public fun abs(x: BigInt): BigInt = x.abs()

/**
 * Convert this [Int] to [BigInt]
 */
public fun Int.toBigInt(): BigInt = BigInt(sign.toByte(), uintArrayOf(kotlin.math.abs(this).toUInt()))

/**
 * Convert this [Long] to [BigInt]
 */
public fun Long.toBigInt(): BigInt = BigInt(
    sign.toByte(),
    stripLeadingZeros(
        uintArrayOf(
            (kotlin.math.abs(this).toULong() and BASE).toUInt(),
            ((kotlin.math.abs(this).toULong() shr BASE_SIZE) and BASE).toUInt()
        )
    )
)

/**
 * Convert UInt to [BigInt]
 */
public fun UInt.toBigInt(): BigInt = BigInt(1, uintArrayOf(this))

/**
 * Convert ULong to [BigInt]
 */
public fun ULong.toBigInt(): BigInt = BigInt(
    1,
    stripLeadingZeros(
        uintArrayOf(
            (this and BASE).toUInt(),
            ((this shr BASE_SIZE) and BASE).toUInt()
        )
    )
)

/**
 * Create a [BigInt] with this array of magnitudes with protective copy
 */
public fun UIntArray.toBigInt(sign: Byte): BigInt {
    require(sign != 0.toByte() || !isNotEmpty())
    return BigInt(sign, copyOf())
}

private val hexChToInt: MutableMap<Char, Int> = hashMapOf(
    '0' to 0, '1' to 1, '2' to 2, '3' to 3,
    '4' to 4, '5' to 5, '6' to 6, '7' to 7,
    '8' to 8, '9' to 9, 'A' to 10, 'B' to 11,
    'C' to 12, 'D' to 13, 'E' to 14, 'F' to 15
)

/**
 * Returns null if a valid number can not be read from a string
 */
public fun String.parseBigInteger(): BigInt? {
    val sign: Int
    val sPositive: String

    when {
        this[0] == '+' -> {
            sign = +1
            sPositive = this.substring(1)
        }
        this[0] == '-' -> {
            sign = -1
            sPositive = this.substring(1)
        }
        else -> {
            sPositive = this
            sign = +1
        }
    }

    var res = BigInt.ZERO
    var digitValue = BigInt.ONE
    val sPositiveUpper = sPositive.toUpperCase()

    if (sPositiveUpper.startsWith("0X")) {  // hex representation
        val sHex = sPositiveUpper.substring(2)

        for (ch in sHex.reversed()) {
            if (ch == '_') continue
            res += digitValue * (hexChToInt[ch] ?: return null)
            digitValue *= 16.toBigInt()
        }
    } else for (ch in sPositiveUpper.reversed()) {
        // decimal representation
        if (ch == '_') continue
        if (ch !in '0'..'9') {
            return null
        }
        res += digitValue * (ch.toInt() - '0'.toInt())
        digitValue *= 10.toBigInt()
    }

    return res * sign
}

public inline fun Buffer.Companion.bigInt(size: Int, initializer: (Int) -> BigInt): Buffer<BigInt> =
    boxing(size, initializer)

public inline fun MutableBuffer.Companion.bigInt(size: Int, initializer: (Int) -> BigInt): MutableBuffer<BigInt> =
    boxing(size, initializer)

public fun AlgebraND.Companion.bigInt(vararg shape: Int): BufferedRingND<BigInt, BigIntField> =
    BufferedRingND(shape, BigIntField, Buffer.Companion::bigInt)
