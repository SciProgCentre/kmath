/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.Circle2D
import space.kscience.kmath.geometry.DoubleVector2D
import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo
import space.kscience.kmath.geometry.circumference
import kotlin.math.PI
import kotlin.math.atan2

public sealed interface Trajectory {
    public val length: Double
}

/**
 * Straight path segment. The order of start and end defines the direction
 */
public data class StraightTrajectory(
    internal val start: DoubleVector2D,
    internal val end: DoubleVector2D,
) : Trajectory {
    override val length: Double get() = start.distanceTo(end)

    internal val theta: Double get() = theta(atan2(end.x - start.x, end.y - start.y))
}

/**
 * An arc segment
 */
public data class CircleTrajectory(
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
        public fun of(center: DoubleVector2D, start: DoubleVector2D, end: DoubleVector2D, direction: Direction): CircleTrajectory {
            fun calculatePose(
                vector: DoubleVector2D,
                theta: Double,
                direction: Direction,
            ): Pose2D = Pose2D(
                vector,
                when (direction) {
                    Direction.LEFT -> theta(theta - PI / 2)
                    Direction.RIGHT -> theta(theta + PI / 2)
                }
            )

            val s1 = StraightTrajectory(center, start)
            val s2 = StraightTrajectory(center, end)
            val pose1 = calculatePose(start, s1.theta, direction)
            val pose2 = calculatePose(end, s2.theta, direction)
            return CircleTrajectory(Circle2D(center, s1.length), pose1, pose2)
        }
    }
}

public open class CompositeTrajectory(public val segments: Collection<Trajectory>) : Trajectory {
    override val length: Double get() = segments.sumOf { it.length }
}

