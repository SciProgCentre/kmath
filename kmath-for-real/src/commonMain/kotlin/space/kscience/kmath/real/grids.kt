/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.real

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.DoubleBuffer
import kotlin.math.floor

public val ClosedFloatingPointRange<Double>.length: Double get() = endInclusive - start

/**
 * Create a Buffer-based grid with equally distributed [numberOfPoints] points. The range could be increasing or  decreasing.
 * If range has a zero size, then the buffer consisting of [numberOfPoints] equal values is returned.
 */
public fun Buffer.Companion.fromRange(range: ClosedFloatingPointRange<Double>, numberOfPoints: Int): DoubleBuffer {
    require(numberOfPoints >= 2) { "Number of points in grid must be more than 1" }
    val normalizedRange = when {
        range.endInclusive > range.start -> range
        range.endInclusive < range.start -> range.endInclusive..range.start
        else -> return DoubleBuffer(numberOfPoints) { range.start }
    }
    val step = normalizedRange.length / (numberOfPoints - 1)
    return DoubleBuffer(numberOfPoints) { normalizedRange.start + step * it }
}

/**
 * Create a Buffer-based grid with equally distributed points with a fixed [step]. The range could be increasing or  decreasing.
 * If the step is larger than the range size, single point is returned.
 */
public fun Buffer.Companion.withFixedStep(range: ClosedFloatingPointRange<Double>, step: Double): DoubleBuffer {
    require(step > 0) { "The grid step must be positive" }
    val normalizedRange = when {
        range.endInclusive > range.start -> range
        range.endInclusive < range.start -> range.endInclusive..range.start
        else -> return DoubleBuffer(range.start)
    }
    val numberOfPoints = floor(normalizedRange.length / step).toInt() + 1
    return DoubleBuffer(numberOfPoints) { normalizedRange.start + step * it  }
}

/**
 * Convert double range to sequence.
 *
 * If the step is positive, then the sequence starts with the lower boundary and increments by [step] until current value is lower than upper boundary.
 * The boundary itself is unnecessarily included.
 *
 * If step is negative, the same goes from upper boundary downwards
 */
@UnstableKMathAPI
public infix fun ClosedFloatingPointRange<Double>.step(step: Double): DoubleBuffer = Buffer.withFixedStep(this, step)