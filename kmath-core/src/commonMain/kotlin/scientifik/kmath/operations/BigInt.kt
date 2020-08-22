package scientifik.kmath.operations

import scientifik.kmath.operations.BigInt.Companion.BASE
import scientifik.kmath.operations.BigInt.Companion.BASE_SIZE
import scientifik.kmath.structures.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

typealias Magnitude = UIntArray
typealias TBase = ULong

/**
 * Kotlin Multiplatform implementation of Big Integer numbers (KBigInteger).
 *
 * @author Robert Drynkin (https://github.com/robdrynkin) and Peter Klimai (https://github.com/pklimai)
 */
object BigIntField : Field<BigInt> {
    override val zero: BigInt = BigInt.ZERO
    override val one: BigInt = BigInt.ONE

    override fun add(a: BigInt, b: BigInt): BigInt = a.plus(b)
    override fun number(value: Number): BigInt = value.toLong().toBigInt()

    override fun multiply(a: BigInt, k: Number): BigInt = a.times(number(k))

    override fun multiply(a: BigInt, b: BigInt): BigInt = a.times(b)

    operator fun String.unaryPlus(): BigInt = this.parseBigInteger() ?: error("Can't parse $this as big integer")

    operator fun String.unaryMinus(): BigInt =
        -(this.parseBigInteger() ?: error("Can't parse $this as big integer"))

    override fun divide(a: BigInt, b: BigInt): BigInt = a.div(b)
}

class BigInt internal constructor(
    private val sign: Byte,
    private val magnitude: Magnitude
) : Comparable<BigInt> {

    override fun compareTo(other: BigInt): Int {
        return when {
            (this.sign == 0.toByte()) and (other.sign == 0.toByte()) -> 0
            this.sign < other.sign -> -1
            this.sign > other.sign -> 1
            else -> this.sign * compareMagnitudes(this.magnitude, other.magnitude)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is BigInt) {
            return this.compareTo(other) == 0
        } else error("Can't compare KBigInteger to a different type")
    }

    override fun hashCode(): Int {
        return magnitude.hashCode() + this.sign
    }

    fun abs(): BigInt = if (sign == 0.toByte()) this else BigInt(1, magnitude)

    operator fun unaryMinus(): BigInt {
        return if (this.sign == 0.toByte()) this else BigInt((-this.sign).toByte(), this.magnitude)
    }

    operator fun plus(b: BigInt): BigInt {
        return when {
            b.sign == 0.toByte() -> this
            this.sign == 0.toByte() -> b
            this == -b -> ZERO
            this.sign == b.sign -> BigInt(this.sign, addMagnitudes(this.magnitude, b.magnitude))
            else -> {
                val comp: Int = compareMagnitudes(this.magnitude, b.magnitude)

                if (comp == 1) {
                    BigInt(this.sign, subtractMagnitudes(this.magnitude, b.magnitude))
                } else {
                    BigInt((-this.sign).toByte(), subtractMagnitudes(b.magnitude, this.magnitude))
                }
            }
        }
    }

    operator fun minus(b: BigInt): BigInt {
        return this + (-b)
    }

    operator fun times(b: BigInt): BigInt {
        return when {
            this.sign == 0.toByte() -> ZERO
            b.sign == 0.toByte() -> ZERO
//          TODO: Karatsuba
            else -> BigInt((this.sign * b.sign).toByte(), multiplyMagnitudes(this.magnitude, b.magnitude))
        }
    }

    operator fun times(other: UInt): BigInt {
        return when {
            this.sign == 0.toByte() -> ZERO
            other == 0U -> ZERO
            else -> BigInt(this.sign, multiplyMagnitudeByUInt(this.magnitude, other))
        }
    }

    operator fun times(other: Int): BigInt {
        return if (other > 0)
            this * kotlin.math.abs(other).toUInt()
        else
            -this * kotlin.math.abs(other).toUInt()
    }

    operator fun div(other: UInt): BigInt {
        return BigInt(this.sign, divideMagnitudeByUInt(this.magnitude, other))
    }

    operator fun div(other: Int): BigInt {
        return BigInt(
            (this.sign * other.sign).toByte(),
            divideMagnitudeByUInt(this.magnitude, kotlin.math.abs(other).toUInt())
        )
    }

    private fun division(other: BigInt): Pair<BigInt, BigInt> {
        // Long division algorithm:
        //     https://en.wikipedia.org/wiki/Division_algorithm#Integer_division_(unsigned)_with_remainder
        // TODO: Implement more effective algorithm
        var q: BigInt = ZERO
        var r: BigInt = ZERO

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

    operator fun div(other: BigInt): BigInt {
        return this.division(other).first
    }

    infix fun shl(i: Int): BigInt {
        if (this == ZERO) return ZERO
        if (i == 0) return this

        val fullShifts = i / BASE_SIZE + 1
        val relShift = i % BASE_SIZE
        val shiftLeft = { x: UInt -> if (relShift >= 32) 0U else x shl relShift }
        val shiftRight = { x: UInt -> if (BASE_SIZE - relShift >= 32) 0U else x shr (BASE_SIZE - relShift) }

        val newMagnitude: Magnitude = Magnitude(this.magnitude.size + fullShifts)

        for (j in this.magnitude.indices) {
            newMagnitude[j + fullShifts - 1] = shiftLeft(this.magnitude[j])
            if (j != 0) {
                newMagnitude[j + fullShifts - 1] = newMagnitude[j + fullShifts - 1] or shiftRight(this.magnitude[j - 1])
            }
        }

        newMagnitude[this.magnitude.size + fullShifts - 1] = shiftRight(this.magnitude.last())

        return BigInt(this.sign, stripLeadingZeros(newMagnitude))
    }

    infix fun shr(i: Int): BigInt {
        if (this == ZERO) return ZERO
        if (i == 0) return this

        val fullShifts = i / BASE_SIZE
        val relShift = i % BASE_SIZE
        val shiftRight = { x: UInt -> if (relShift >= 32) 0U else x shr relShift }
        val shiftLeft = { x: UInt -> if (BASE_SIZE - relShift >= 32) 0U else x shl (BASE_SIZE - relShift) }
        if (this.magnitude.size - fullShifts <= 0) {
            return ZERO
        }
        val newMagnitude: Magnitude = Magnitude(this.magnitude.size - fullShifts)

        for (j in fullShifts until this.magnitude.size) {
            newMagnitude[j - fullShifts] = shiftRight(this.magnitude[j])
            if (j != this.magnitude.size - 1) {
                newMagnitude[j - fullShifts] = newMagnitude[j - fullShifts] or shiftLeft(this.magnitude[j + 1])
            }
        }

        return BigInt(this.sign, stripLeadingZeros(newMagnitude))
    }

    infix fun or(other: BigInt): BigInt {
        if (this == ZERO) return other
        if (other == ZERO) return this
        val resSize = max(this.magnitude.size, other.magnitude.size)
        val newMagnitude: Magnitude = Magnitude(resSize)
        for (i in 0 until resSize) {
            if (i < this.magnitude.size) {
                newMagnitude[i] = newMagnitude[i] or this.magnitude[i]
            }
            if (i < other.magnitude.size) {
                newMagnitude[i] = newMagnitude[i] or other.magnitude[i]
            }
        }
        return BigInt(1, stripLeadingZeros(newMagnitude))
    }

    infix fun and(other: BigInt): BigInt {
        if ((this == ZERO) or (other == ZERO)) return ZERO
        val resSize = min(this.magnitude.size, other.magnitude.size)
        val newMagnitude: Magnitude = Magnitude(resSize)
        for (i in 0 until resSize) {
            newMagnitude[i] = this.magnitude[i] and other.magnitude[i]
        }
        return BigInt(1, stripLeadingZeros(newMagnitude))
    }

    operator fun rem(other: Int): Int {
        val res = this - (this / other) * other
        return if (res == ZERO) 0 else res.sign * res.magnitude[0].toInt()
    }

    operator fun rem(other: BigInt): BigInt {
        return this - (this / other) * other
    }

    fun modPow(exponent: BigInt, m: BigInt): BigInt {
        return when {
            exponent == ZERO -> ONE
            exponent % 2 == 1 -> (this * modPow(exponent - ONE, m)) % m
            else -> {
                val sqRoot = modPow(exponent / 2, m)
                (sqRoot * sqRoot) % m
            }
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

    companion object {
        const val BASE: ULong = 0xffffffffUL
        const val BASE_SIZE: Int = 32
        val ZERO: BigInt = BigInt(0, uintArrayOf())
        val ONE: BigInt = BigInt(1, uintArrayOf(1u))

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
            val resultLength: Int = max(mag1.size, mag2.size) + 1
            val result = Magnitude(resultLength)
            var carry: TBase = 0UL

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
            val resultLength: Int = mag1.size
            val result = Magnitude(resultLength)
            var carry = 0L

            for (i in 0 until resultLength) {
                var res: Long =
                    if (i < mag2.size) mag1[i].toLong() - mag2[i].toLong() - carry
                    else mag1[i].toLong() - carry

                carry = if (res < 0) 1 else 0
                res += carry * (BASE + 1UL).toLong()

                result[i] = res.toUInt()
            }

            return stripLeadingZeros(result)
        }

        private fun multiplyMagnitudeByUInt(mag: Magnitude, x: UInt): Magnitude {
            val resultLength: Int = mag.size + 1
            val result = Magnitude(resultLength)
            var carry: ULong = 0UL

            for (i in mag.indices) {
                val cur: ULong = carry + mag[i].toULong() * x.toULong()
                result[i] = (cur and BASE.toULong()).toUInt()
                carry = cur shr BASE_SIZE
            }
            result[resultLength - 1] = (carry and BASE).toUInt()

            return stripLeadingZeros(result)
        }

        private fun multiplyMagnitudes(mag1: Magnitude, mag2: Magnitude): Magnitude {
            val resultLength: Int = mag1.size + mag2.size
            val result = Magnitude(resultLength)

            for (i in mag1.indices) {
                var carry: ULong = 0UL
                for (j in mag2.indices) {
                    val cur: ULong = result[i + j].toULong() + mag1[i].toULong() * mag2[j].toULong() + carry
                    result[i + j] = (cur and BASE.toULong()).toUInt()
                    carry = cur shr BASE_SIZE
                }
                result[i + mag2.size] = (carry and BASE).toUInt()
            }

            return stripLeadingZeros(result)
        }

        private fun divideMagnitudeByUInt(mag: Magnitude, x: UInt): Magnitude {
            val resultLength: Int = mag.size
            val result = Magnitude(resultLength)
            var carry: ULong = 0UL

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
    if (mag.isEmpty() || mag.last() != 0U) {
        return mag
    }
    var resSize: Int = mag.size - 1
    while (mag[resSize] == 0U) {
        if (resSize == 0)
            break
        resSize -= 1
    }
    return mag.sliceArray(IntRange(0, resSize))
}

fun abs(x: BigInt): BigInt = x.abs()

/**
 * Convert this [Int] to [BigInt]
 */
fun Int.toBigInt(): BigInt = BigInt(sign.toByte(), uintArrayOf(kotlin.math.abs(this).toUInt()))

/**
 * Convert this [Long] to [BigInt]
 */
fun Long.toBigInt(): BigInt = BigInt(
    sign.toByte(), stripLeadingZeros(
        uintArrayOf(
            (kotlin.math.abs(this).toULong() and BASE).toUInt(),
            ((kotlin.math.abs(this).toULong() shr BASE_SIZE) and BASE).toUInt()
        )
    )
)

/**
 * Convert UInt to [BigInt]
 */
fun UInt.toBigInt(): BigInt = BigInt(1, uintArrayOf(this))

/**
 * Convert ULong to [BigInt]
 */
fun ULong.toBigInt(): BigInt = BigInt(
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
fun UIntArray.toBigInt(sign: Byte): BigInt {
    require(sign != 0.toByte() || !isNotEmpty())
    return BigInt(sign, copyOf())
}

val hexChToInt: MutableMap<Char, Int> = hashMapOf(
    '0' to 0, '1' to 1, '2' to 2, '3' to 3,
    '4' to 4, '5' to 5, '6' to 6, '7' to 7,
    '8' to 8, '9' to 9, 'A' to 10, 'B' to 11,
    'C' to 12, 'D' to 13, 'E' to 14, 'F' to 15
)

/**
 * Returns null if a valid number can not be read from a string
 */
fun String.parseBigInteger(): BigInt? {
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
    } else { // decimal representation
        for (ch in sPositiveUpper.reversed()) {
            if (ch == '_') continue
            if (ch !in '0'..'9') {
                return null
            }
            res += digitValue * (ch.toInt() - '0'.toInt())
            digitValue *= 10.toBigInt()
        }
    }
    return res * sign
}

inline fun Buffer.Companion.bigInt(size: Int, initializer: (Int) -> BigInt): Buffer<BigInt> {
    contract { callsInPlace(initializer) }
    return boxing(size, initializer)
}

inline fun MutableBuffer.Companion.bigInt(size: Int, initializer: (Int) -> BigInt): MutableBuffer<BigInt> {
    contract { callsInPlace(initializer) }
    return boxing(size, initializer)
}

fun NDAlgebra.Companion.bigInt(vararg shape: Int): BoxingNDRing<BigInt, BigIntField> =
    BoxingNDRing(shape, BigIntField, Buffer.Companion::bigInt)

fun NDElement.Companion.bigInt(
    vararg shape: Int,
    initializer: BigIntField.(IntArray) -> BigInt
): BufferedNDRingElement<BigInt, BigIntField> = NDAlgebra.bigInt(*shape).produce(initializer)
