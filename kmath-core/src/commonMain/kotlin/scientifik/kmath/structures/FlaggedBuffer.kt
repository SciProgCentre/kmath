package scientifik.kmath.structures

import kotlin.experimental.and

enum class ValueFlag(val mask: Byte) {
    NAN(0b0000_0001),
    MISSING(0b0000_0010),
    NEGATIVE_INFINITY(0b0000_0100),
    POSITIVE_INFINITY(0b0000_1000)
}

/**
 * A buffer with flagged values
 */
interface FlaggedBuffer<T> : Buffer<T> {
    fun getFlag(index: Int): Byte
}

/**
 * The value is valid if all flags are down
 */
fun FlaggedBuffer<*>.isValid(index: Int) = getFlag(index) != 0.toByte()

fun FlaggedBuffer<*>.hasFlag(index: Int, flag: ValueFlag) = (getFlag(index) and flag.mask) != 0.toByte()

fun FlaggedBuffer<*>.isMissing(index: Int) = hasFlag(index, ValueFlag.MISSING)

/**
 * A real buffer which supports flags for each value like NaN or Missing
 */
class FlaggedRealBuffer(val values: DoubleArray, val flags: ByteArray) : FlaggedBuffer<Double?>, Buffer<Double?> {
    init {
        require(values.size == flags.size) { "Values and flags must have the same dimensions" }
    }

    override fun getFlag(index: Int): Byte = flags[index]

    override val size: Int get() = values.size

    override fun get(index: Int): Double? = if (isValid(index)) values[index] else null

    override fun iterator(): Iterator<Double?> = values.indices.asSequence().map {
        if (isValid(it)) values[it] else null
    }.iterator()
}

inline fun FlaggedRealBuffer.forEachValid(block: (Double) -> Unit) {
    for(i in indices){
        if(isValid(i)){
            block(values[i])
        }
    }
}