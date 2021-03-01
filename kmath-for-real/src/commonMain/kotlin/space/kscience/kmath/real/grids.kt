package space.kscience.kmath.real

import space.kscience.kmath.structures.asBuffer
import kotlin.math.abs

/**
 * Convert double range to sequence.
 *
 * If the step is positive, than the sequence starts with the lower boundary and increments by [step] until current value is lower than upper boundary.
 * The boundary itself is not necessary included.
 *
 * If step is negative, the same goes from upper boundary downwards
 */
public fun ClosedFloatingPointRange<Double>.toSequenceWithStep(step: Double): Sequence<Double> = when {
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

public infix fun ClosedFloatingPointRange<Double>.step(step: Double): RealVector =
    toSequenceWithStep(step).toList().asBuffer()

/**
 * Convert double range to sequence with the fixed number of points
 */
public fun ClosedFloatingPointRange<Double>.toSequenceWithPoints(numPoints: Int): Sequence<Double> {
    require(numPoints > 1) { "The number of points should be more than 2" }
    return toSequenceWithStep(abs(endInclusive - start) / (numPoints - 1))
}
