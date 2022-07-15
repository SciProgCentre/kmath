package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Line2D
import space.kscience.kmath.operations.DoubleField.pow
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

public class LineSegment(
    internal val line: Line2D
) : Segment {
    override val length: Double
        get() = line.length
}

internal val Line2D.theta: Double
    get() = atan2(direction.x - base.x, direction.y - base.y).theta

internal val Line2D.length: Double
    get() = sqrt((direction.x - base.x).pow(2) + (direction.y - base.y).pow(2))

internal val Double.theta: Double
    get() = (this + (2 * PI)) % (2 * PI)
