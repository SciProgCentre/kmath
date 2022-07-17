package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.trajectory.dubins.theta
import space.kscience.kmath.trajectory.segments.components.Circle
import space.kscience.kmath.trajectory.segments.components.Pose2D
import kotlin.math.PI

public data class Arc(
    public val circle: Circle,
    public val start: Pose2D,
    public val end: Pose2D
) : Segment {

    internal companion object {
        fun of(center: Vector2D, start: Vector2D, end: Vector2D, direction: Direction): Arc {
            val s1 = Straight(center, start)
            val s2 = Straight(center, end)
            val pose1 = calculatePose(start, s1.theta, direction)
            val pose2 = calculatePose(end, s2.theta, direction)
            return Arc(Circle(center, s1.length), pose1, pose2)
        }

        private fun calculatePose(vector: Vector2D, theta: Double, direction: Direction): Pose2D =
            Pose2D.of(
                vector,
                when (direction) {
                    Direction.LEFT -> theta(theta - PI / 2)
                    Direction.RIGHT -> theta(theta + PI / 2)
                }
            )
    }

    internal enum class Direction {
        LEFT, RIGHT
    }

    override val length: Double
        get() {
            val angle: Double =
                theta(if (direction == Direction.LEFT) start.theta - end.theta else end.theta - start.theta)
            val proportion = angle / (2 * PI)
            return circle.circumference * proportion
        }

    internal val direction: Direction
        get() = if (start.y < circle.center.y) {
            if (start.theta > PI) Direction.RIGHT else Direction.LEFT
        } else if (start.y > circle.center.y) {
            if (start.theta < PI) Direction.RIGHT else Direction.LEFT
        } else {
            if (start.theta == 0.0) {
                if (start.x < circle.center.x) Direction.RIGHT else Direction.LEFT
            } else {
                if (start.x > circle.center.x) Direction.RIGHT else Direction.LEFT
            }
        }

}
