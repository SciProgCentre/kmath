package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo
import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.trajectory.dubins.theta
import kotlin.math.PI
import kotlin.math.atan2

public data class Straight(
    internal val start: Vector2D,
    internal val end: Vector2D
) : Segment {
    override val length: Double
        get() = start.distanceTo(end)

    internal val theta: Double
        get() = theta(atan2(end.x - start.x, end.y - start.y))
}
