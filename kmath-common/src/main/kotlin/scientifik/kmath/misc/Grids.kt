package scientifik.kmath.misc

import kotlin.coroutines.experimental.buildSequence


/**
 * Convert double range to sequence.
 *
 * If the step is positive, than the sequence starts with the lower boundary and increments by [step] until current value is lower than upper boundary.
 * The boundary itself is not necessary included.
 *
 * If step is negative, the same goes from upper boundary downwards
 */
fun ClosedFloatingPointRange<Double>.toSequence(step: Double): Sequence<Double> {
    return when {
        step == 0.0 -> error("Zero step in double progression")
        step > 0 -> buildSequence {
            var current = start
            while (current <= endInclusive) {
                yield(current)
                current += step
            }
        }
        else -> buildSequence {
            var current = endInclusive
            while (current >= start) {
                yield(current)
                current += step
            }
        }
    }
}

/**
 * Convert double range to array of evenly spaced doubles, where the size of array equals [numPoints]
 */
fun ClosedFloatingPointRange<Double>.toGrid(numPoints: Int): DoubleArray {
    if (numPoints < 2) error("Can't create grid with less than two points")
    return DoubleArray(numPoints) { i -> start + (endInclusive - start) / (numPoints - 1) * i }
}