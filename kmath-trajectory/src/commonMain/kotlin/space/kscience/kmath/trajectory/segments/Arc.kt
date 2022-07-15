package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo
import space.kscience.kmath.geometry.Line2D
import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.trajectory.segments.components.Circle
import space.kscience.kmath.trajectory.segments.components.Pose2D
import kotlin.math.PI

public class Arc(
    center: Vector2D,
    a: Vector2D,
    b: Vector2D,
    internal val direction: Direction
) : Circle(center, center.distanceTo(a)), Segment {

    private val l1 = Line2D(center, a)
    private val l2 = Line2D(center, b)

    internal val pose1 = calculatePose(a, l1.theta)
    internal val pose2 = calculatePose(b, l2.theta)
    private val angle = calculateAngle()
    override val length: Double = calculateLength()

    public enum class Direction {
        LEFT, RIGHT
    }

    private fun calculateAngle() = theta(if (direction == Direction.LEFT) l1.theta - l2.theta else l2.theta - l1.theta)

    private fun calculateLength(): Double {
        val proportion = angle / (2 * PI)
        return circumference * proportion
    }

    private fun calculatePose(vector: Vector2D, theta: Double): Pose2D =
        if (direction == Direction.LEFT) {
            Pose2D(vector.x, vector.y, theta(theta - PI / 2))
        } else {
            Pose2D(vector.x, vector.y, theta(theta + PI / 2))
        }
}
