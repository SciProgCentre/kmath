package space.kscience.kmath.series

import space.kscience.kmath.operations.BufferAlgebra
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.RingOps
import space.kscience.kmath.stat.StatisticalAlgebra
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferView
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


public interface Series<T> : Buffer<T> {
    public val origin: Buffer<T>

    /**
     * Absolute position of start of this [Series] in [SeriesAlgebra]
     */
    public val position: Int
}

public val <T> Series<T>.absoluteIndices: IntRange get() = position until position + size

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
public class SeriesAlgebra<T, out A : Ring<T>, out BA : BufferAlgebra<T, A>, L>(
    override val bufferAlgebra: BA,
    private val labelResolver: (Int) -> L,
) : RingOps<Buffer<T>>, StatisticalAlgebra<T, A, BA> {

    public val Buffer<T>.indices: IntRange
        get() = if (this is Series) {
            absoluteIndices
        } else {
            0 until size
        }

    /**
     * Get the value by absolute index in the series algebra or return null if index is out of range
     */
    public fun Buffer<T>.getAbsoluteOrNull(index: Int): T? = when {
        index !in indices -> null
        this is Series -> origin[index - position]
        else -> get(index)
    }

    /**
     * Get the value by absolute index in the series algebra or throw [IndexOutOfBoundsException] if index is out of range
     */
    public fun Buffer<T>.getAbsolute(index: Int): T =
        getAbsoluteOrNull(index) ?: throw IndexOutOfBoundsException("Index $index is not in $indices")

    /**
     * Create an offset series with index starting point at [index]
     */
    public fun Buffer<T>.moveTo(index: Int): Series<T> = if (this is Series) {
        SeriesImpl(origin, index, size)
    } else {
        SeriesImpl(this, index, size)
    }

    public val Buffer<T>.offset: Int get() = if (this is Series) position else 0

    /**
     * Build a new series
     */
    public fun series(size: Int, fromIndex: Int = 0, block: A.(label: L) -> T): Series<T> {
        return elementAlgebra.bufferFactory(size) {
            val index = it + fromIndex
            elementAlgebra.block(labelResolver(index))
        }.moveTo(fromIndex)
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
        return getAbsolute(index + offset)
    }

    /**
     * Map a series to another series of the same size
     */
    public inline fun Buffer<T>.map(crossinline transform: A.(T) -> T): Series<T> {
        val buf = elementAlgebra.bufferFactory(size) {
            elementAlgebra.transform(getAbsolute(it))
        }
        return buf.moveTo(indices.first)
    }

    /**
     * Map series to another series of the same size with label
     */
    public inline fun Buffer<T>.mapWithLabel(crossinline transform: A.(arg: T, label: L) -> T): Series<T> {
        val labels = labels
        val buf = elementAlgebra.bufferFactory(size) {
            elementAlgebra.transform(getAbsolute(it), labels[it])
        }
        return buf.moveTo(indices.first)
    }

    public inline fun <R> Buffer<T>.fold(initial: R, operation: A.(acc: R, T) -> R): R {
        var accumulator = initial
        for (index in this.indices) accumulator = elementAlgebra.operation(accumulator, getAbsolute(index))
        return accumulator
    }

    public inline fun <R> Buffer<T>.foldWithLabel(initial: R, operation: A.(acc: R, arg: T, label: L) -> R): R {
        val labels = labels
        var accumulator = initial
        for (index in this.indices) accumulator =
            elementAlgebra.operation(accumulator, getAbsolute(index), labels[index])
        return accumulator
    }

    /**
     * Zip two buffers in the range whe they overlap
     */
    public inline fun Buffer<T>.zip(
        other: Buffer<T>,
        crossinline operation: A.(left: T, right: T) -> T,
    ): Series<T> {
        val newRange = indices.intersect(other.indices)
        return elementAlgebra.bufferFactory(newRange.size) {
            elementAlgebra.operation(
                getAbsolute(it),
                other.getAbsolute(it)
            )
        }.moveTo(newRange.first)
    }

    override fun Buffer<T>.unaryMinus(): Buffer<T> = map { -it }

    override fun add(left: Buffer<T>, right: Buffer<T>): Series<T> = left.zip(right) { l, r -> l + r }

    override fun multiply(left: Buffer<T>, right: Buffer<T>): Buffer<T> = left.zip(right) { l, r -> l * r }
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