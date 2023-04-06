/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.structures.Buffer



public interface TimeSeries<R, T>: Series<R> {
    public override val origin: Buffer<R>
    public override val position: Int

    // TODO: Specify the types of DateTime that can be contained in timeStamp Buffer
    public val timeStamp: Buffer<T> // Buffer of some datetime instances like: Instant, LocalDate, LocalTime...
}

private class TimeSeriesImpl<R, T>(
    override val origin: Buffer<R>,
    override val timeStamp: Buffer<T>,
    override val position: Int,
    override val size: Int = origin.size,
) : TimeSeries<R, T> by origin { // TODO: manage with delegation

    init {
        require(size > 0) { "Size must be positive" }
        require(size <= origin.size) { "Slice size is larger than the original buffer" }
        require(size <= timeStamp.size) { "Slice size is larger than the timeStamp buffer" }
    }

//    override fun toString(): String = "$origin-->${position}"
}


// TODO: add conversion to Buffer of Pairs ?
