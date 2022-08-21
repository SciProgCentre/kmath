/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.Circle2D
import space.kscience.kmath.geometry.Euclidean2DSpace
import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

internal fun Pose2D.getLeftCircle(radius: Double): Circle2D = getTangentCircles(radius).first

internal fun Pose2D.getRightCircle(radius: Double): Circle2D = getTangentCircles(radius).second

internal fun Pose2D.getTangentCircles(radius: Double): Pair<Circle2D, Circle2D> = with(Euclidean2DSpace) {
    val dX = radius * cos(theta)
    val dY = radius * sin(theta)
    return Circle2D(vector(x - dX, y + dY), radius) to Circle2D(vector(x + dX, y - dY), radius)
}

internal fun leftOuterTangent(a: Circle2D, b: Circle2D): StraightSegment = outerTangent(a, b, ArcSegment.Direction.LEFT)

internal fun rightOuterTangent(a: Circle2D, b: Circle2D): StraightSegment = outerTangent(a, b,
    ArcSegment.Direction.RIGHT
)

private fun outerTangent(a: Circle2D, b: Circle2D, side: ArcSegment.Direction): StraightSegment = with(Euclidean2DSpace){
    val centers = StraightSegment(a.center, b.center)
    val p1 = when (side) {
        ArcSegment.Direction.LEFT -> vector(
            a.center.x - a.radius * cos(centers.theta),
            a.center.y + a.radius * sin(centers.theta)
        )
        ArcSegment.Direction.RIGHT -> vector(
            a.center.x + a.radius * cos(centers.theta),
            a.center.y - a.radius * sin(centers.theta)
        )
    }
    return StraightSegment(
        p1,
        vector(p1.x + (centers.end.x - centers.start.x), p1.y + (centers.end.y - centers.start.y))
    )
}

internal fun leftInnerTangent(base: Circle2D, direction: Circle2D): StraightSegment? =
    innerTangent(base, direction, ArcSegment.Direction.LEFT)

internal fun rightInnerTangent(base: Circle2D, direction: Circle2D): StraightSegment? =
    innerTangent(base, direction, ArcSegment.Direction.RIGHT)

private fun innerTangent(base: Circle2D, direction: Circle2D, side: ArcSegment.Direction): StraightSegment? = with(Euclidean2DSpace){
    val centers = StraightSegment(base.center, direction.center)
    if (centers.length < base.radius * 2) return null
    val angle = theta(
        when (side) {
            ArcSegment.Direction.LEFT -> centers.theta + acos(base.radius * 2 / centers.length)
            ArcSegment.Direction.RIGHT -> centers.theta - acos(base.radius * 2 / centers.length)
        }
    )
    val dX = base.radius * sin(angle)
    val dY = base.radius * cos(angle)
    val p1 = vector(base.center.x + dX, base.center.y + dY)
    val p2 = vector(direction.center.x - dX, direction.center.y - dY)
    return StraightSegment(p1, p2)
}

internal fun theta(theta: Double): Double = (theta + (2 * PI)) % (2 * PI)

public class DubinsPath(
    public val a: ArcSegment,
    public val b: Trajectory,
    public val c: ArcSegment,
) : CompositeTrajectory(listOf(a,b,c)) {

    public val type: TYPE = TYPE.valueOf(
        arrayOf(
            a.direction.name[0],
            if (b is ArcSegment) b.direction.name[0] else 'S',
            c.direction.name[0]
        ).toCharArray().concatToString()
    )

    public enum class TYPE {
        RLR, LRL, RSR, LSL, RSL, LSR
    }

    public companion object {
        public fun all(
            start: Pose2D,
            end: Pose2D,
            turningRadius: Double,
        ): List<DubinsPath> = listOfNotNull(
            rlr(start, end, turningRadius),
            lrl(start, end, turningRadius),
            rsr(start, end, turningRadius),
            lsl(start, end, turningRadius),
            rsl(start, end, turningRadius),
            lsr(start, end, turningRadius)
        )

        public fun shortest(start: Pose2D, end: Pose2D, turningRadius: Double): DubinsPath =
            all(start, end, turningRadius).minBy { it.length }

        public fun rlr(start: Pose2D, end: Pose2D, turningRadius: Double): DubinsPath? = with(Euclidean2DSpace){
            val c1 = start.getRightCircle(turningRadius)
            val c2 = end.getRightCircle(turningRadius)
            val centers = StraightSegment(c1.center, c2.center)
            if (centers.length > turningRadius * 4) return null

            var theta = theta(centers.theta - acos(centers.length / (turningRadius * 4)))
            var dX = turningRadius * sin(theta)
            var dY = turningRadius * cos(theta)
            val p = vector(c1.center.x + dX * 2, c1.center.y + dY * 2)
            val e = Circle2D(p, turningRadius)
            val p1 = vector(c1.center.x + dX, c1.center.y + dY)
            theta = theta(centers.theta + acos(centers.length / (turningRadius * 4)))
            dX = turningRadius * sin(theta)
            dY = turningRadius * cos(theta)
            val p2 = vector(e.center.x + dX, e.center.y + dY)
            val a1 = ArcSegment.of(c1.center, start, p1, ArcSegment.Direction.RIGHT)
            val a2 = ArcSegment.of(e.center, p1, p2, ArcSegment.Direction.LEFT)
            val a3 = ArcSegment.of(c2.center, p2, end, ArcSegment.Direction.RIGHT)
            return DubinsPath(a1, a2, a3)
        }

        public fun lrl(start: Pose2D, end: Pose2D, turningRadius: Double): DubinsPath?= with(Euclidean2DSpace) {
            val c1 = start.getLeftCircle(turningRadius)
            val c2 = end.getLeftCircle(turningRadius)
            val centers = StraightSegment(c1.center, c2.center)
            if (centers.length > turningRadius * 4) return null

            var theta = theta(centers.theta + acos(centers.length / (turningRadius * 4)))
            var dX = turningRadius * sin(theta)
            var dY = turningRadius * cos(theta)
            val p = vector(c1.center.x + dX * 2, c1.center.y + dY * 2)
            val e = Circle2D(p, turningRadius)
            val p1 = vector(c1.center.x + dX, c1.center.y + dY)
            theta = theta(centers.theta - acos(centers.length / (turningRadius * 4)))
            dX = turningRadius * sin(theta)
            dY = turningRadius * cos(theta)
            val p2 = vector(e.center.x + dX, e.center.y + dY)
            val a1 = ArcSegment.of(c1.center, start, p1, ArcSegment.Direction.LEFT)
            val a2 = ArcSegment.of(e.center, p1, p2, ArcSegment.Direction.RIGHT)
            val a3 = ArcSegment.of(c2.center, p2, end, ArcSegment.Direction.LEFT)
            return DubinsPath(a1, a2, a3)
        }

        public fun rsr(start: Pose2D, end: Pose2D, turningRadius: Double): DubinsPath {
            val c1 = start.getRightCircle(turningRadius)
            val c2 = end.getRightCircle(turningRadius)
            val s = leftOuterTangent(c1, c2)
            val a1 = ArcSegment.of(c1.center, start, s.start, ArcSegment.Direction.RIGHT)
            val a3 = ArcSegment.of(c2.center, s.end, end, ArcSegment.Direction.RIGHT)
            return DubinsPath(a1, s, a3)
        }

        public fun lsl(start: Pose2D, end: Pose2D, turningRadius: Double): DubinsPath {
            val c1 = start.getLeftCircle(turningRadius)
            val c2 = end.getLeftCircle(turningRadius)
            val s = rightOuterTangent(c1, c2)
            val a1 = ArcSegment.of(c1.center, start, s.start, ArcSegment.Direction.LEFT)
            val a3 = ArcSegment.of(c2.center, s.end, end, ArcSegment.Direction.LEFT)
            return DubinsPath(a1, s, a3)
        }

        public fun rsl(start: Pose2D, end: Pose2D, turningRadius: Double): DubinsPath? {
            val c1 = start.getRightCircle(turningRadius)
            val c2 = end.getLeftCircle(turningRadius)
            val s = rightInnerTangent(c1, c2)
            if (s == null || c1.center.distanceTo(c2.center) < turningRadius * 2) return null

            val a1 = ArcSegment.of(c1.center, start, s.start, ArcSegment.Direction.RIGHT)
            val a3 = ArcSegment.of(c2.center, s.end, end, ArcSegment.Direction.LEFT)
            return DubinsPath(a1, s, a3)
        }

        public fun lsr(start: Pose2D, end: Pose2D, turningRadius: Double): DubinsPath? {
            val c1 = start.getLeftCircle(turningRadius)
            val c2 = end.getRightCircle(turningRadius)
            val s = leftInnerTangent(c1, c2)
            if (s == null || c1.center.distanceTo(c2.center) < turningRadius * 2) return null

            val a1 = ArcSegment.of(c1.center, start, s.start, ArcSegment.Direction.LEFT)
            val a3 = ArcSegment.of(c2.center, s.end, end, ArcSegment.Direction.RIGHT)
            return DubinsPath(a1, s, a3)
        }
    }
}