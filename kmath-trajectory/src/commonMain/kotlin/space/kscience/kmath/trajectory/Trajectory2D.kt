/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
@file:UseSerializers(Euclidean2DSpace.VectorSerializer::class)

package space.kscience.kmath.trajectory

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import space.kscience.kmath.geometry.*
import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo
import kotlin.math.atan2

@Serializable
public sealed interface Trajectory2D {
    public val length: Double

    public enum class Type {
        R,
        S,
        L
    }
}

/**
 * Straight path segment. The order of start and end defines the direction
 */
@Serializable
@SerialName("straight")
public data class StraightTrajectory2D(
    public val start: DoubleVector2D,
    public val end: DoubleVector2D,
) : Trajectory2D {

    override val length: Double get() = start.distanceTo(end)

    public val bearing: Angle get() = (atan2(end.x - start.x, end.y - start.y).radians).normalized()
}

public fun StraightTrajectory2D.toSegment(): LineSegment<Vector2D<Double>> = LineSegment2D(start, end)

/**
 * An arc segment
 */
@Serializable
@SerialName("arc")
public data class CircleTrajectory2D(
    public val circle: Circle2D,
    public val start: DubinsPose2D,
    public val end: DubinsPose2D,
) : Trajectory2D {

    /**
     * Arc length in radians
     */
    val arcLength: Angle
        get() = if (direction == Trajectory2D.Type.L) {
            start.bearing - end.bearing
        } else {
            end.bearing - start.bearing
        }.normalized()


    override val length: Double by lazy {
        circle.radius * arcLength.radians
    }

    public val direction: Trajectory2D.Type by lazy {
        if (start.y < circle.center.y) {
            if (start.bearing > Angle.pi) Trajectory2D.Type.R else Trajectory2D.Type.L
        } else if (start.y > circle.center.y) {
            if (start.bearing < Angle.pi) Trajectory2D.Type.R else Trajectory2D.Type.L
        } else {
            if (start.bearing == Angle.zero) {
                if (start.x < circle.center.x) Trajectory2D.Type.R else Trajectory2D.Type.L
            } else {
                if (start.x > circle.center.x) Trajectory2D.Type.R else Trajectory2D.Type.L
            }
        }
    }

    public companion object {
        public fun of(
            center: DoubleVector2D,
            start: DoubleVector2D,
            end: DoubleVector2D,
            direction: Trajectory2D.Type,
        ): CircleTrajectory2D {
            fun calculatePose(
                vector: DoubleVector2D,
                theta: Angle,
                direction: Trajectory2D.Type,
            ): DubinsPose2D = DubinsPose2D(
                vector,
                when (direction) {
                    Trajectory2D.Type.L -> (theta - Angle.piDiv2).normalized()
                    Trajectory2D.Type.R -> (theta + Angle.piDiv2).normalized()
                    else -> error("S trajectory type is not allowed in circle constructor")
                }
            )

            val s1 = StraightTrajectory2D(center, start)
            val s2 = StraightTrajectory2D(center, end)
            val pose1 = calculatePose(start, s1.bearing, direction)
            val pose2 = calculatePose(end, s2.bearing, direction)
            val trajectory = CircleTrajectory2D(Circle2D(center, s1.length), pose1, pose2)
            if (trajectory.direction != direction) error("Trajectory direction mismatch")
            return trajectory
        }
    }
}

@Serializable
@SerialName("composite")
public class CompositeTrajectory2D(public val segments: List<Trajectory2D>) : Trajectory2D {
    override val length: Double get() = segments.sumOf { it.length }
}

public fun CompositeTrajectory2D(vararg segments: Trajectory2D): CompositeTrajectory2D =
    CompositeTrajectory2D(segments.toList())

