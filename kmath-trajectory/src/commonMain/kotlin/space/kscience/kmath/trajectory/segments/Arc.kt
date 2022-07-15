package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Line2D
import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.trajectory.segments.components.Circle
import space.kscience.kmath.trajectory.segments.components.Pose2D
import kotlin.math.PI

public class Arc(
    center: Vector2D,
    radius: Double,
    a: Vector2D,
    b: Vector2D,
    internal val direction: Direction
) : Circle(center, radius), Segment {

    private val l1 = Line2D(center, a)
    private val l2 = Line2D(center, b)

    internal val pose1 = calculatePose(a, l1.theta)
    internal val pose2 = calculatePose(b, l2.theta)
    private val angle = calculateAngle()
    override val length: Double = calculateLength()

    public enum class Direction {
        LEFT, RIGHT
    }

    private fun calculateAngle() =
        (if (direction == Direction.LEFT) l1.theta - l2.theta else l2.theta - l1.theta).theta

    private fun calculateLength(): Double {
        val proportion = angle / (2 * PI)
        return circumference * proportion
    }

    private fun calculatePose(vector: Vector2D, theta: Double): Pose2D =
        if (direction == Direction.LEFT) {
            Pose2D(vector.x, vector.y, (theta - PI / 2).theta)
        } else {
            Pose2D(vector.x, vector.y, (theta + PI / 2).theta)
        }
}
