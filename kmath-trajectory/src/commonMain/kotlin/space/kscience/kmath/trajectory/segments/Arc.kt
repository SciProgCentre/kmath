package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo
import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.trajectory.dubins.theta
import space.kscience.kmath.trajectory.segments.components.Circle
import space.kscience.kmath.trajectory.segments.components.Pose2D
import kotlin.math.PI

public class Arc(
    center: Vector2D,
    a: Vector2D,
    b: Vector2D,
    internal val direction: Direction
) : Circle(center, center.distanceTo(a)), Segment {

    private val s1 = Straight(center, a)
    private val s2 = Straight(center, b)

    internal val pose1 = calculatePose(a, s1.theta)
    internal val pose2 = calculatePose(b, s2.theta)
    private val angle = calculateAngle()
    override val length: Double = calculateLength()

    public enum class Direction {
        LEFT, RIGHT
    }

    private fun calculateAngle() = theta(if (direction == Direction.LEFT) s1.theta - s2.theta else s2.theta - s1.theta)

    private fun calculateLength(): Double {
        val proportion = angle / (2 * PI)
        return circumference * proportion
    }

    private fun calculatePose(vector: Vector2D, theta: Double): Pose2D =
        Pose2D.of(
            vector,
            when (direction) {
                Direction.LEFT -> theta(theta - PI / 2)
                Direction.RIGHT -> theta(theta + PI / 2)
            }
        )
}
