package space.kscience.kmath.series

import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.getOrNull
import kotlin.math.max
import kotlin.math.min

private fun IntRange.intersect(other: IntRange): IntRange =
    max(first, other.first)..min(last, other.last)

private val IntRange.size get() = last - first + 1

private class BufferView<T>(val buffer: Buffer<T>, val offset: Int, override val size: Int) : Buffer<T> {
    init {
        require(offset >= 0) { " Range offset must be positive" }
        require(offset < buffer.size) { "Range offset is beyond the buffer size" }
        require(size > 0) { "Size must be positive" }
        require(size < buffer.size) { "Slice size is larger than the buffer" }
    }

    override fun get(index: Int): T = buffer[index - offset]

    override fun iterator(): Iterator<T> = buffer.asSequence().drop(offset).take(size).iterator()

    override fun toString(): String = "$buffer[${offset}:${offset + size - 1}]"

    override val indices: IntRange = offset until offset + size
}

/**
 * A scope to operation on series
 */
public class SeriesAlgebra<T, A : Ring<T>, L>(
    public val bufferAlgebra: BufferRingOps<T, A>,
    private val labelResolver: (Int) -> L,
) : RingOps<Buffer<T>> {

    public val elementAlgebra: A get() = bufferAlgebra.elementAlgebra
    public val bufferFactory: BufferFactory<T> get() = bufferAlgebra.bufferFactory

    public val Buffer<T>.offset: UInt get() = indices.first.toUInt()

    /**
     * Build a new series
     */
    public fun series(size: Int, fromIndex: Int = 0, block: A.(label: L) -> T): Buffer<T> {
        return bufferFactory(size) {
            val index = it + fromIndex
            elementAlgebra.block(labelResolver(index))
        }.moveTo(fromIndex)
    }

    /**
     * Move a series starting to start at a given index
     */
    public fun Buffer<T>.moveTo(index: Int): Buffer<T> = if (index == 0) {
        this
    } else if (this is BufferView) {
        BufferView(buffer, index.toInt(), size)
    } else {
        BufferView(this, index.toInt(), size)
    }

    /**
     * Create a buffer view using given range
     */
    public fun Buffer<T>.get(range: IntRange): Buffer<T> {
        val size = range.size
        return if (this is BufferView) {
            BufferView(this, indices.first + range.first, size)
        } else {
            BufferView(this, range.first, size)
        }
    }

    /**
     * Get a label buffer for given buffer.
     */
    public val Buffer<T>.labels: List<L> get() = indices.map(labelResolver)


    /**
     * Try to resolve element by label and return null if element with a given label is not found
     */
    public operator fun Buffer<T>.get(label: L): T? {
        val index = labels.indexOf(label)
        if (index == -1) return null
        return get(index + offset.toInt())
    }

    override fun add(left: Buffer<T>, right: Buffer<T>): Buffer<T> = elementAlgebra.invoke {
        val newRange = left.indices.intersect(right.indices)
        //TODO optimize copy at BufferAlgebra level
        bufferFactory(newRange.size) {
            val offset = it + newRange.first
            left[offset] + right[offset]
        }.moveTo(newRange.first)
    }

    override fun Buffer<T>.unaryMinus(): Buffer<T> = map { -it }

    override fun multiply(left: Buffer<T>, right: Buffer<T>): Buffer<T> = elementAlgebra.invoke {
        val newRange = left.indices.intersect(right.indices)
        bufferFactory(newRange.size) {
            val offset = it + newRange.first
            left[offset] * right[offset]
        }
    }

    /**
     * Map a series to another series of the same size
     */
    public inline fun Buffer<T>.map(crossinline transform: A.(T) -> T): Buffer<T> {
        val buf = bufferFactory(size) {
            elementAlgebra.transform(get(it))
        }
        return buf.moveTo(indices.first)
    }

    /**
     * Map series to another series of the same size with label
     */
    public inline fun Buffer<T>.mapWithLabel(crossinline transform: A.(arg: T, label: L) -> T): Buffer<T> {
        val labels = labels
        val buf = bufferFactory(size) {
            elementAlgebra.transform(get(it), labels[it])
        }
        return buf.moveTo(indices.first)
    }

    public inline fun <R> Buffer<T>.fold(initial: R, operation: A.(acc: R, T) -> R): R {
        var accumulator = initial
        for (index in this.indices) accumulator = elementAlgebra.operation(accumulator, get(index))
        return accumulator
    }

    public inline fun <R> Buffer<T>.foldWithLabel(initial: R, operation: A.(acc: R, arg: T, label: L) -> R): R {
        val labels = labels
        var accumulator = initial
        for (index in this.indices) accumulator = elementAlgebra.operation(accumulator, get(index), labels[index])
        return accumulator
    }

    /**
     * Zip two buffers replacing missing values with [defaultValue]
     */
    public inline fun Buffer<T>.zip(
        other: Buffer<T>,
        defaultValue: T,
        crossinline operation: A.(left: T?, right: T?) -> T?,
    ): Buffer<T> {
        val start = min(indices.first, other.indices.first)
        val size = max(indices.last, other.indices.last) - start
        return bufferFactory(size) {
            elementAlgebra.operation(
                getOrNull(it) ?: defaultValue,
                other.getOrNull(it) ?: defaultValue
            ) ?: defaultValue
        }
    }
}

public fun <T, A : Ring<T>, L> BufferRingOps<T, A>.seriesAlgebra(labels: Iterable<L>): SeriesAlgebra<T, A, L> {
    val l = labels.toList()
    return SeriesAlgebra(this) {
        if (it in l.indices) l[it] else error("Index $it is outside of labels range ${l.indices}")
    }
}