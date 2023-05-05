package space.kscience.kmath.series

import space.kscience.kmath.operations.BufferAlgebra
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.RingOps
import space.kscience.kmath.stat.StatisticalAlgebra
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferView
import space.kscience.kmath.structures.getOrNull
import kotlin.math.max
import kotlin.math.min

@PublishedApi
internal fun IntRange.intersect(other: IntRange): IntRange =
    max(first, other.first)..min(last, other.last)

@PublishedApi
internal val IntRange.size: Int
    get() = last - first + 1

@PublishedApi
internal operator fun IntRange.contains(other: IntRange): Boolean = (other.first in this) && (other.last in this)

//TODO add permutation sort
//TODO check rank statistics

/**
 * A [Buffer] with an offset relative to the [SeriesAlgebra] zero.
 */
public interface Series<T> : Buffer<T> {
    public val origin: Buffer<T>

    /**
     * Absolute position of start of this [Series] in [SeriesAlgebra]
     */
    public val position: Int
}

/**
 * A [BufferView] with index offset (both positive and negative) and possible size change
 */
private class SeriesImpl<T>(
    override val origin: Buffer<T>,
    override val position: Int,
    override val size: Int = origin.size,
) : Series<T>, Buffer<T> by origin {

    init {
        require(size > 0) { "Size must be positive" }
        require(size <= origin.size) { "Slice size is larger than the original buffer" }
    }

    override fun toString(): String = "$origin-->${position}"
}

/**
 * A scope to operation on series
 */
public open class SeriesAlgebra<T, out A : Ring<T>, out BA : BufferAlgebra<T, A>, L>(
    override val bufferAlgebra: BA,
    public val offsetToLabel: (Int) -> L,
) : RingOps<Buffer<T>>, StatisticalAlgebra<T, A, BA> {

    /**
     * A range of valid offset indices. In general, does not start with zero.
     */
    public val Buffer<T>.offsetIndices: IntRange
        get() = if (this is Series) {
            position until position + size
        } else {
            0 until size
        }

    /**
     * Get the value by absolute offset in the series algebra or return null if index is out of range
     */
    public fun Buffer<T>.getByOffsetOrNull(index: Int): T? = when {
        index !in offsetIndices -> null
        this is Series -> origin.getOrNull(index - position)
        else -> getOrNull(index)
    }

    /**
     * Get the value by absolute index in the series algebra or throw [IndexOutOfBoundsException] if index is out of range
     */
    public fun Buffer<T>.getByOffset(index: Int): T =
        getByOffsetOrNull(index) ?: throw IndexOutOfBoundsException("Index $index is not in $offsetIndices")

    /**
     *  Zero-copy move [Buffer] or [Series] to given [position] ignoring series offset if it is present.
     */
    public fun Buffer<T>.moveTo(position: Int): Series<T> = if (this is Series) {
        SeriesImpl(origin, position, size)
    } else {
        SeriesImpl(this, position, size)
    }

    /**
     * Zero-copy move [Buffer] or [Series] by given [offset]. If it is [Series], sum intrinsic series position and the [offset].
     */
    public fun Buffer<T>.moveBy(offset: Int): Series<T> = if (this is Series) {
        SeriesImpl(origin, position + offset, size)
    } else {
        SeriesImpl(this, offset, size)
    }

    /**
     * An offset of the buffer start relative to [SeriesAlgebra] zero offset
     */
    public val Buffer<T>.startOffset: Int get() = if (this is Series) position else 0

    public val Buffer<T>.startLabel: L get() = offsetToLabel(startOffset)

    /**
     * Build a new series by offset positioned at [startOffset].
     */
    public inline fun seriesByOffset(
        size: Int,
        startOffset: Int = 0,
        crossinline block: A.(offset: Int) -> T,
    ): Series<T> = elementAlgebra.bufferFactory(size) {
        elementAlgebra.block(it + startOffset)
    }.moveTo(startOffset)

    /**
     * Build a new series by label positioned at [startOffset].
     */
    public inline fun series(size: Int, startOffset: Int = 0, crossinline block: A.(label: L) -> T): Series<T> =
        seriesByOffset(size, startOffset) { offset -> block(offsetToLabel(offset)) }

    /**
     * Get a label buffer for given buffer.
     */
    public val Buffer<T>.labels: List<L> get() = offsetIndices.map(offsetToLabel)

    /**
     * Try to resolve element by label and return null if element with a given label is not found
     */
    public open fun Buffer<T>.getByLabelOrNull(label: L): T? {
        val index = labels.indexOf(label)
        if (index == -1) return null
        return getByOffset(index + startOffset)
    }

    /**
     * Get value by label (rounded down) or throw [IndexOutOfBoundsException] if the value is outside series boundaries.
     */
    public open fun Buffer<T>.getByLabel(label: L): T = getByLabelOrNull(label)
        ?: throw IndexOutOfBoundsException("Label $label is not in ${labels.first()}..${labels.last()}")

    /**
     * Map a series to another series of the same size
     */
    public inline fun Buffer<T>.map(crossinline transform: A.(T) -> T): Series<T> {
        val buf = elementAlgebra.bufferFactory(size) {
            elementAlgebra.transform(get(it))
        }
        return buf.moveTo(offsetIndices.first)
    }

    /**
     * Map series to another series of the same size with label
     */
    public inline fun Buffer<T>.mapWithLabel(crossinline transform: A.(arg: T, label: L) -> T): Series<T> {
        val labels = labels
        val buf = elementAlgebra.bufferFactory(size) {
            elementAlgebra.transform(getByOffset(it), labels[it])
        }
        return buf.moveTo(offsetIndices.first)
    }

    public inline fun <R> Buffer<T>.fold(initial: R, operation: A.(acc: R, T) -> R): R {
        var accumulator = initial
        for (index in this.offsetIndices) accumulator = elementAlgebra.operation(accumulator, getByOffset(index))
        return accumulator
    }

    public inline fun <R> Buffer<T>.foldWithLabel(initial: R, operation: A.(acc: R, arg: T, label: L) -> R): R {
        val labels = labels
        var accumulator = initial
        for (index in this.offsetIndices) accumulator =
            elementAlgebra.operation(accumulator, getByOffset(index), labels[index])
        return accumulator
    }

    /**
     * Zip two buffers in the range whe they overlap
     */
    public inline fun Buffer<T>.zip(
        other: Buffer<T>,
        crossinline operation: A.(left: T, right: T) -> T,
    ): Series<T> {
        val newRange = offsetIndices.intersect(other.offsetIndices)
        return seriesByOffset(startOffset = newRange.first, size = newRange.last + 1 - newRange.first) { offset ->
            elementAlgebra.operation(
                getByOffset(offset),
                other.getByOffset(offset)
            )
        }
    }

    /**
     * Zip buffer with itself, but shifted
     * */
    public inline fun Buffer<T>.zipWithShift(
        shift: Int = 1,
        crossinline operation: A.(left: T, right: T) -> T
    ): Buffer<T> {
        val shifted = this.moveBy(shift)
        return zip(shifted, operation)
    }

    override fun Buffer<T>.unaryMinus(): Buffer<T> = map { -it }

    override fun add(left: Buffer<T>, right: Buffer<T>): Series<T> = left.zip(right) { l, r -> l + r }

    override fun multiply(left: Buffer<T>, right: Buffer<T>): Buffer<T> = left.zip(right) { l, r -> l * r }

    public fun Buffer<T>.difference(shift: Int=1): Buffer<T> = this.zipWithShift(shift) {l, r -> r - l}

    public companion object
}

public fun <T, A : Ring<T>, BA : BufferAlgebra<T, A>, L> BA.seriesAlgebra(labels: Iterable<L>): SeriesAlgebra<T, A, BA, L> {
    val l = labels.toList()
    return SeriesAlgebra(this) {
        if (it in l.indices) l[it] else error("Index $it is outside of labels range ${l.indices}")
    }
}

public fun <T, A : Ring<T>, BA : BufferAlgebra<T, A>, L> BA.seriesAlgebra(labelGenerator: (Int) -> L): SeriesAlgebra<T, A, BA, L> =
    SeriesAlgebra(this, labelGenerator)

/**
 * Create a series algebra using offset as a label
 */
public fun <T, A : Ring<T>, BA : BufferAlgebra<T, A>> BA.seriesAlgebra(): SeriesAlgebra<T, A, BA, Int> =
    SeriesAlgebra(this) { it }