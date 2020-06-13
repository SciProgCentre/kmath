package scientifik.kmath.misc

import kotlin.math.abs

/**
 * Convert double range to sequence.
 *
 * If the step is positive, than the sequence starts with the lower boundary and increments by [step] until current value is lower than upper boundary.
 * The boundary itself is not necessary included.
 *
 * If step is negative, the same goes from upper boundary downwards
 */
fun ClosedFloatingPointRange<Double>.toSequenceWithStep(step: Double): Sequence<Double> = when {
    step == 0.0 -> error("Zero step in double progression")
    step > 0 -> sequence {
        var current = start
        while (current <= endInclusive) {
            yield(current)
            current += step
        }
    }
    else -> sequence {
        var current = endInclusive
        while (current >= start) {
            yield(current)
            current += step
        }
    }
}

/**
 * Convert double range to sequence with the fixed number of points
 */
fun ClosedFloatingPointRange<Double>.toSequenceWithPoints(numPoints: Int): Sequence<Double> {
    require(numPoints > 1) { "The number of points should be more than 2" }
    return toSequenceWithStep(abs(endInclusive - start) / (numPoints - 1))
}

/**
 * Convert double range to array of evenly spaced doubles, where the size of array equals [numPoints]
 */
@Deprecated("Replace by 'toSequenceWithPoints'")
fun ClosedFloatingPointRange<Double>.toGrid(numPoints: Int): DoubleArray {
    if (numPoints < 2) error("Can't create generic grid with less than two points")
    return DoubleArray(numPoints) { i -> start + (endInclusive - start) / (numPoints - 1) * i }
}