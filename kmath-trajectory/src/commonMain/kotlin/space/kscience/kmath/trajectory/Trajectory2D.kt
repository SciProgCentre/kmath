/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.Circle2D
import space.kscience.kmath.geometry.DoubleVector2D
import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo
import kotlin.math.PI
import kotlin.math.atan2

public sealed interface Trajectory2D {
    public val length: Double
}

/**
 * Straight path segment. The order of start and end defines the direction
 */
public data class StraightTrajectory2D(
    internal val start: DoubleVector2D,
    internal val end: DoubleVector2D,
) : Trajectory2D {
    override val length: Double get() = start.distanceTo(end)

    internal val bearing: Double get() = theta(atan2(end.x - start.x, end.y - start.y))
}

/**
 * An arc segment
 */
public data class CircleTrajectory2D(
    public val circle: Circle2D,
    public val start: DubinsPose2D,
    public val end: DubinsPose2D,
) : Trajectory2D {

    public enum class Direction {
        LEFT, RIGHT
    }

    /**
     * Arc length in radians
     */
    val arcLength: Double
        get() = theta(
            if (direction == Direction.LEFT) {
                start.bearing - end.bearing
            } else {
                end.bearing - start.bearing
            }
        )

    override val length: Double by lazy {
        circle.radius * arcLength
    }

    internal val direction: Direction by lazy {
        if (start.y < circle.center.y) {
            if (start.bearing > PI) Direction.RIGHT else Direction.LEFT
        } else if (start.y > circle.center.y) {
            if (start.bearing < PI) Direction.RIGHT else Direction.LEFT
        } else {
            if (start.bearing == 0.0) {
                if (start.x < circle.center.x) Direction.RIGHT else Direction.LEFT
            } else {
                if (start.x > circle.center.x) Direction.RIGHT else Direction.LEFT
            }
        }
    }

    public companion object {
        public fun of(
            center: DoubleVector2D,
            start: DoubleVector2D,
            end: DoubleVector2D,
            direction: Direction,
        ): CircleTrajectory2D {
            fun calculatePose(
                vector: DoubleVector2D,
                theta: Double,
                direction: Direction,
            ): DubinsPose2D = Pose2D(
                vector,
                when (direction) {
                    Direction.LEFT -> theta(theta - PI / 2)
                    Direction.RIGHT -> theta(theta + PI / 2)
                }
            )

            val s1 = StraightTrajectory2D(center, start)
            val s2 = StraightTrajectory2D(center, end)
            val pose1 = calculatePose(start, s1.bearing, direction)
            val pose2 = calculatePose(end, s2.bearing, direction)
            val trajectory = CircleTrajectory2D(Circle2D(center, s1.length), pose1, pose2)
            if(trajectory.direction != direction){
                error("Trajectory direction mismatch")
            }
            return trajectory
        }
    }
}

public open class CompositeTrajectory2D(public val segments: Collection<Trajectory2D>) : Trajectory2D {
    override val length: Double get() = segments.sumOf { it.length }
}

