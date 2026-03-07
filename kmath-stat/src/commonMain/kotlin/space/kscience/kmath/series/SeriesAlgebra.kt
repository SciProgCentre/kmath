/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.operations.BufferAlgebra
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.RingOps
import space.kscience.kmath.stat.StatisticalAlgebra
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MutableBufferFactory
import space.kscience.kmath.structures.getOrNull
import space.kscience.kmath.structures.slice
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
public class Series<T>(
    public val origin: Buffer<T>,
    public val position: Int
) {
    public val size: Int get() = origin.size

    override fun toString(): String = "$origin-->${position}"
}

/**
 * A range of valid offset indices. In general, does not start with zero.
 */
public val Series<*>.indices: IntRange
    get() = position until (position + size)


/**
 * A scope to operation on series
 */
public open class SeriesAlgebra<T, out A : Ring<T>, out BA : BufferAlgebra<T, A>, L>(
    override val bufferAlgebra: BA,
    public val offsetToLabel: (Int) -> L,
) : RingOps<Series<T>>, StatisticalAlgebra<T, A, BA> {

    override val bufferFactory: MutableBufferFactory<Series<T>> = MutableBufferFactory()

    public fun Buffer<T>.asSeries(position: Int = 0): Series<T> = Series(this, position)

    /**
     * Get the value by absolute offset in the series algebra or return null if index is out of range
     */
    public fun Series<T>.getByOffsetOrNull(index: Int): T? = when {
        index !in indices -> null
        else -> origin.getOrNull(index - position)
    }

    /**
     * Get the value by absolute index in the series algebra or throw [IndexOutOfBoundsException] if index is out of range
     */
    public operator fun Series<T>.get(index: Int): T =
        getByOffsetOrNull(index) ?: throw IndexOutOfBoundsException("Index $index is not in $indices")

    /**
     *  Zero-copy move [Buffer] or [Series] to given [position] ignoring series offset if it is present.
     */
    public fun Series<T>.moveTo(position: Int): Series<T> = Series(origin, position)

    /**
     * Zero-copy move [Buffer] or [Series] by given [offset]. If it is [Series], sum intrinsic series position and the [offset].
     */
    public fun Series<T>.moveBy(offset: Int): Series<T> = Series(origin, position + offset)

    public val Series<T>.startLabel: L get() = offsetToLabel(position)

    /**
     * Build a new series by offset positioned at [startOffset].
     */
    public inline fun seriesByOffset(
        size: Int,
        startOffset: Int = 0,
        crossinline block: A.(offset: Int) -> T,
    ): Series<T> = elementAlgebra.bufferFactory(size) {
        elementAlgebra.block(it + startOffset)
    }.asSeries(startOffset)

    /**
     * Build a new series by label positioned at [startOffset].
     */
    public inline fun series(size: Int, startOffset: Int = 0, crossinline block: A.(label: L) -> T): Series<T> =
        seriesByOffset(size, startOffset) { offset -> block(offsetToLabel(offset)) }

    /**
     * Get a label buffer for the given buffer.
     */
    public val Series<T>.labels: List<L> get() = indices.map(offsetToLabel)

    /**
     * Try to resolve an element by label and return null if an element with a given label is not found
     */
    public open fun Series<T>.getByLabelOrNull(label: L): T? {
        val index = labels.indexOf(label)
        if (index == -1) return null
        return get(index + position)
    }

    /**
     * Get value by label (rounded down) or throw [IndexOutOfBoundsException] if the value is outside series boundaries.
     */
    public open fun Series<T>.getByLabel(label: L): T = getByLabelOrNull(label)
        ?: throw IndexOutOfBoundsException("Label $label is not in ${labels.first()}..${labels.last()}")

    /**
     * Map a series to another series of the same size
     */
    public inline fun Series<T>.map(crossinline transform: A.(T) -> T): Series<T> {
        val buf = elementAlgebra.bufferFactory(size) {
            elementAlgebra.transform(origin[it])
        }
        return buf.asSeries(indices.first)
    }

    /**
     * Map series to another series of the same size with label
     */
    public inline fun Series<T>.mapWithLabel(crossinline transform: A.(arg: T, label: L) -> T): Series<T> {
        val labels = labels
        val buf = elementAlgebra.bufferFactory(size) {
            elementAlgebra.transform(origin[it], labels[it])
        }
        return buf.asSeries(indices.first)
    }

    public inline fun <R> Series<T>.fold(initial: R, operation: A.(acc: R, T) -> R): R {
        var accumulator = initial
        for (index in indices) accumulator = elementAlgebra.operation(accumulator, get(index))
        return accumulator
    }

    public inline fun <R> Series<T>.foldWithLabel(initial: R, operation: A.(acc: R, arg: T, label: L) -> R): R {
        val labels = labels
        var accumulator = initial
        for (index in indices) accumulator = elementAlgebra.operation(accumulator, get(index), labels[index - position])
        return accumulator
    }

    /**
     * Zip two buffers in the range whe they overlap
     */
    public inline fun Series<T>.zip(
        other: Series<T>,
        crossinline operation: A.(left: T, right: T) -> T,
    ): Series<T> {
        val newRange = indices.intersect(other.indices)
        return seriesByOffset(startOffset = newRange.first, size = newRange.last + 1 - newRange.first) { offset ->
            elementAlgebra.operation(
                get(offset),
                other.get(offset)
            )
        }
    }

    /**
     * Zip buffer with itself, but shifted
     * */
    public inline fun Series<T>.zipWithShift(
        shift: Int = 1,
        crossinline operation: A.(left: T, right: T) -> T,
    ): Series<T> {
        val shifted = moveBy(shift)
        return zip(shifted, operation)
    }

    override fun Series<T>.unaryMinus(): Series<T> = map { -it }

    override fun add(left: Series<T>, right: Series<T>): Series<T> = left.zip(right) { l, r -> l + r }

    override fun multiply(left: Series<T>, right: Series<T>): Series<T> = left.zip(right) { l, r -> l * r }

    /**
     * Compute difference serries between a value and value shifted back by [shift] steps
     */
    public fun Series<T>.difference(shift: Int = 1): Series<T> = zipWithShift(shift) { l, r ->
        //subtract offset to the right series from this one
        l - r
    }



    /**
     * Creates a new series by taking a slice of the original series over the specified range of positions.
     *
     * The slicing operation adjusts the series' buffer origin based on the provided range,
     * resulting in a new series that corresponds to the specified range of indices.
     */
    public fun Series<T>.slice(range: IntRange): Series<T> {
        val intersection = indices.intersect(range)
        if (intersection.isEmpty()) return Series(Buffer.EMPTY, intersection.first)
        val newOrigin = origin.slice((intersection.first - position)..(intersection.last - position))
        return Series(newOrigin, intersection.first)
    }

    /**
     * Zero-copy convert [Series] to [Buffer]. Buffer spans the whole [range] (first index of a buffer corrsponds to first in range)
     * and fills gaps with [missingValue].
     */
    public fun Series<T>.asBuffer(
        missingValue: T = elementAlgebra.zero,
        range: IntRange = 0 until (position + size)
    ): Buffer<T> = when (range) {
        indices -> origin
        in indices -> origin.slice((range.first - position)..(range.last - position))
        else -> object : Buffer<T> {

            init {
                require(range.last >= range.first) { "Invalid range $range" }
            }

            override val size: Int = range.last - range.first + 1

            override fun get(index: Int): T = if ((index + range.first) in indices) {
                origin[index - position + range.first]
            } else {
                missingValue
            }

            override fun toString(): String = "SeriesBuffer(${range.first}..${range.last})"
        }
    }

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