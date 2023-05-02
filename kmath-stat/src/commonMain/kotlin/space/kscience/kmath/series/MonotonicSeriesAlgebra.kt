/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.operations.BufferAlgebra
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.Buffer
import kotlin.math.ceil
import kotlin.math.floor

/**
 * A [SeriesAlgebra] with reverse label to index transformation.
 *
 * @param [labelToOffset] returns floating point number that is used for index resolution.
 */
public class MonotonicSeriesAlgebra<T, out A : Ring<T>, out BA : BufferAlgebra<T, A>, L : Comparable<L>>(
    bufferAlgebra: BA,
    offsetToLabel: (Int) -> L,
    private val labelToOffset: (L) -> Double,
) : SeriesAlgebra<T, A, BA, L>(bufferAlgebra, offsetToLabel) {

    public val Buffer<T>.labelRange: ClosedRange<L> get() = offsetToLabel(startOffset)..offsetToLabel(startOffset + size)

    /**
     * An offset of the given [label] rounded down
     */
    public fun floorOffset(label: L): Int = floor(labelToOffset(label)).toInt()

    /**
     * An offset of the given [label] rounded up
     */
    public fun ceilOffset(label: L): Int = ceil(labelToOffset(label)).toInt()

    /**
     * Get value by label (rounded down) or return null if the value is outside series boundaries.
     */
    override fun Buffer<T>.getByLabelOrNull(label: L): T? = getByOffsetOrNull(floorOffset(label))
}