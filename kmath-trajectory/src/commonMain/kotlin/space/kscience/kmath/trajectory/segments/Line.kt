package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo
import space.kscience.kmath.geometry.Line2D
import space.kscience.kmath.operations.DoubleField.pow
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

public data class LineSegment(
    internal val line: Line2D
) : Segment {
    override val length: Double
        get() = line.length
}

internal val Line2D.theta: Double
    get() = theta(atan2(direction.x - base.x, direction.y - base.y))

internal val Line2D.length: Double
    get() = base.distanceTo(direction)

internal fun theta(theta: Double) = (theta + (2 * PI)) % (2 * PI)
