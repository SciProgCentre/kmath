package space.kscience.kmath.structures

import space.kscience.kmath.misc.UnstableKMathAPI

/**
 * A buffer that wraps an original buffer
 */
public interface BufferView<T> : Buffer<T> {
    public val origin: Buffer<T>

    /**
     * Get the index in [origin] buffer from index in this buffer.
     * Return -1 if element not present in the original buffer
     * This method should be used internally to optimize non-boxing access.
     */
    @UnstableKMathAPI
    public fun originIndex(index: Int): Int
}

/**
 * A zero-copy buffer that "sees" only part of original buffer. Slice can't go beyond original buffer borders.
 */
public class BufferSlice<T>(
    override val origin: Buffer<T>,
    public val offset: UInt = 0U,
    override val size: Int,
) : BufferView<T> {

    init {
        require(size > 0) { "Size must be positive" }
        require(offset + size.toUInt() <= origin.size.toUInt()) {
            "End of buffer ${offset + size.toUInt()} is beyond the end of origin buffer size ${origin.size}"
        }
    }

    override fun get(index: Int): T = if (index >= size) {
        throw IndexOutOfBoundsException("$index is out of ${0 until size} rage")
    } else {
        origin[index.toUInt() + offset]
    }

    override fun iterator(): Iterator<T> =
        (offset until (offset + size.toUInt())).asSequence().map { origin[it] }.iterator()

    @UnstableKMathAPI
    override fun originIndex(index: Int): Int = if (index >= size) -1 else index - offset.toInt()

    override fun toString(): String = "$origin[$offset..${offset + size.toUInt()}"
}

/**
 * An expanded buffer that could include the whole initial buffer ot its part and fills all space beyond it borders with [defaultValue].
 *
 * The [offset] parameter shows the shift of expanded buffer start relative to origin start and could be both positive and negative.
 */
public class BufferExpanded<T>(
    override val origin: Buffer<T>,
    public val defaultValue: T,
    public val offset: Int = 0,
    override val size: Int = origin.size,
) : BufferView<T> {

    init {
        require(size > 0) { "Size must be positive" }
    }

    override fun get(index: Int): T = when (index) {
        !in 0 until size -> throw IndexOutOfBoundsException("Index $index is not in $indices")
        in -offset until origin.size - offset -> origin[index + offset]
        else -> defaultValue
    }

    @UnstableKMathAPI
    override fun originIndex(index: Int): Int = if (index in -offset until origin.size - offset) index + offset else -1

    override fun toString(): String = "$origin[$offset..${offset + size}]"
}

/**
 * Zero-copy select a slice inside the original buffer
 */
public fun <T> Buffer<T>.slice(range: UIntRange): BufferView<T> = BufferSlice(
    this,
    range.first,
    (range.last - range.first).toInt() + 1
)

/**
 * Resize original buffer to a given range using given [range], filling additional segments with [defaultValue].
 * Range left border could be negative to designate adding new blank segment to the beginning of the buffer
 */
public fun <T> Buffer<T>.expand(
    range: IntRange,
    defaultValue: T,
): BufferView<T> = if (range.first >= 0 && range.last < size) {
    BufferSlice(
        this,
        range.first.toUInt(),
        (range.last - range.first) + 1
    )
} else {
    BufferExpanded(
        this,
        defaultValue,
        range.first,
        (range.last - range.first) + 1
    )
}

/**
 * A [BufferView] that overrides indexing of the original buffer
 */
public class PermutatedBuffer<T>(
    override val origin: Buffer<T>,
    private val permutations: IntArray,
) : BufferView<T> {
    init {
        permutations.forEach { index ->
            if (index !in origin.indices) {
                throw IndexOutOfBoundsException("Index $index is not in ${origin.indices}")
            }
        }
    }

    override val size: Int get() = permutations.size

    override fun get(index: Int): T = origin[permutations[index]]

    override fun iterator(): Iterator<T> = permutations.asSequence().map { origin[it] }.iterator()

    @UnstableKMathAPI
    override fun originIndex(index: Int): Int = if (index in permutations.indices) permutations[index] else -1

    override fun toString(): String = Buffer.toString(this)
}

/**
 * Created a permuted view of given buffer using provided [indices]
 */
public fun <T> Buffer<T>.view(indices: IntArray): PermutatedBuffer<T> = PermutatedBuffer(this, indices)