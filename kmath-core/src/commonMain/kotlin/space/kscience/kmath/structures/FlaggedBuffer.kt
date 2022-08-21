/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import kotlin.experimental.and

/**
 * Represents flags to supply additional info about values of buffer.
 *
 * @property mask bit mask value of this flag.
 */
public enum class ValueFlag(public val mask: Byte) {
    /**
     * Reports the value is NaN.
     */
    NAN(0b0000_0001),

    /**
     * Reports the value doesn't present in the buffer (when the type of value doesn't support `null`).
     */
    MISSING(0b0000_0010),

    /**
     * Reports the value is negative infinity.
     */
    NEGATIVE_INFINITY(0b0000_0100),

    /**
     * Reports the value is positive infinity
     */
    POSITIVE_INFINITY(0b0000_1000)
}

/**
 * A buffer with flagged values.
 */
public interface FlaggedBuffer<out T> : Buffer<T> {
    public fun getFlag(index: Int): Byte
}

/**
 * The value is valid if all flags are down
 */
public fun FlaggedBuffer<*>.isValid(index: Int): Boolean = getFlag(index) != 0.toByte()

public fun FlaggedBuffer<*>.hasFlag(index: Int, flag: ValueFlag): Boolean = (getFlag(index) and flag.mask) != 0.toByte()

public fun FlaggedBuffer<*>.isMissing(index: Int): Boolean = hasFlag(index, ValueFlag.MISSING)

/**
 * A [Double] buffer that supports flags for each value like `NaN` or Missing.
 */
public class FlaggedDoubleBuffer(
    public val values: DoubleArray,
    public val flags: ByteArray
) : FlaggedBuffer<Double?>, Buffer<Double?> {
    init {
        require(values.size == flags.size) { "Values and flags must have the same dimensions" }
    }

    override fun getFlag(index: Int): Byte = flags[index]

    override val size: Int get() = values.size

    override operator fun get(index: Int): Double? = if (isValid(index)) values[index] else null

    override operator fun iterator(): Iterator<Double?> = values.indices.asSequence().map {
        if (isValid(it)) values[it] else null
    }.iterator()

    override fun toString(): String = Buffer.toString(this)
}

public inline fun FlaggedDoubleBuffer.forEachValid(block: (Double) -> Unit) {
    indices
        .asSequence()
        .filter(::isValid)
        .forEach { block(values[it]) }
}
