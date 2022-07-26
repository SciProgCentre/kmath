package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Circle2D
import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo
import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.geometry.circumference
import space.kscience.kmath.trajectory.dubins.theta
import kotlin.math.PI
import kotlin.math.atan2

public interface Trajectory {
    public val length: Double
}

/**
 * Straight path segment. The order of start and end defines the direction
 */
public data class StraightSegment(
    internal val start: Vector2D,
    internal val end: Vector2D,
) : Trajectory {
    override val length: Double get() = start.distanceTo(end)

    internal val theta: Double get() = theta(atan2(end.x - start.x, end.y - start.y))
}

/**
 * An arc segment
 */
public data class ArcSegment(
    public val circle: Circle2D,
    public val start: Pose2D,
    public val end: Pose2D,
) : Trajectory {

    public enum class Direction {
        LEFT, RIGHT
    }

    override val length: Double by lazy {
        val angle: Double = theta(
            if (direction == Direction.LEFT) {
                start.theta - end.theta
            } else {
                end.theta - start.theta
            }
        )
        val proportion = angle / (2 * PI)
        circle.circumference * proportion
    }

    internal val direction: Direction by lazy {
        if (start.y < circle.center.y) {
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

    public companion object {
        public fun of(center: Vector2D, start: Vector2D, end: Vector2D, direction: Direction): ArcSegment {
            fun calculatePose(
                vector: Vector2D,
                theta: Double,
                direction: Direction,
            ): Pose2D = Pose2D.of(
                vector,
                when (direction) {
                    Direction.LEFT -> theta(theta - PI / 2)
                    Direction.RIGHT -> theta(theta + PI / 2)
                }
            )

            val s1 = StraightSegment(center, start)
            val s2 = StraightSegment(center, end)
            val pose1 = calculatePose(start, s1.theta, direction)
            val pose2 = calculatePose(end, s2.theta, direction)
            return ArcSegment(Circle2D(center, s1.length), pose1, pose2)
        }
    }

}

