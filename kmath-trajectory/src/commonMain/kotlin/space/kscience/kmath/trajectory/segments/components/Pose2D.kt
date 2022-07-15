package space.kscience.kmath.trajectory.segments.components

import space.kscience.kmath.geometry.Vector2D
import kotlin.math.cos
import kotlin.math.sin

public class Pose2D(
    override val x: Double,
    override val y: Double,
    public val theta: Double
) : Vector2D {

    internal constructor(vector: Vector2D, theta: Double) : this(vector.x, vector.y, theta)

    override fun toString(): String {
        return "Pose2D(x=$x, y=$y, theta=$theta)"
    }
}
