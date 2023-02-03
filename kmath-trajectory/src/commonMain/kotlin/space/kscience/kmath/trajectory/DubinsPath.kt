/*
 * Copyright 2018-2022 KMath contributors.
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

internal fun DubinsPose2D.getLeftCircle(radius: Double): Circle2D = getTangentCircles(radius).first

internal fun DubinsPose2D.getRightCircle(radius: Double): Circle2D = getTangentCircles(radius).second

internal fun DubinsPose2D.getTangentCircles(radius: Double): Pair<Circle2D, Circle2D> = with(Euclidean2DSpace) {
    val dX = radius * cos(bearing)
    val dY = radius * sin(bearing)
    return Circle2D(vector(x - dX, y + dY), radius) to Circle2D(vector(x + dX, y - dY), radius)
}

internal fun leftOuterTangent(a: Circle2D, b: Circle2D): StraightTrajectory2D =
    outerTangent(a, b, CircleTrajectory2D.Direction.LEFT)

internal fun rightOuterTangent(a: Circle2D, b: Circle2D): StraightTrajectory2D = outerTangent(
    a, b,
    CircleTrajectory2D.Direction.RIGHT
)

private fun outerTangent(a: Circle2D, b: Circle2D, side: CircleTrajectory2D.Direction): StraightTrajectory2D =
    with(Euclidean2DSpace) {
        val centers = StraightTrajectory2D(a.center, b.center)
        val p1 = when (side) {
            CircleTrajectory2D.Direction.LEFT -> vector(
                a.center.x - a.radius * cos(centers.bearing),
                a.center.y + a.radius * sin(centers.bearing)
            )

            CircleTrajectory2D.Direction.RIGHT -> vector(
                a.center.x + a.radius * cos(centers.bearing),
                a.center.y - a.radius * sin(centers.bearing)
            )
        }
        return StraightTrajectory2D(
            p1,
            vector(p1.x + (centers.end.x - centers.start.x), p1.y + (centers.end.y - centers.start.y))
        )
    }

internal fun leftInnerTangent(base: Circle2D, direction: Circle2D): StraightTrajectory2D? =
    innerTangent(base, direction, CircleTrajectory2D.Direction.LEFT)

internal fun rightInnerTangent(base: Circle2D, direction: Circle2D): StraightTrajectory2D? =
    innerTangent(base, direction, CircleTrajectory2D.Direction.RIGHT)

private fun innerTangent(
    base: Circle2D,
    direction: Circle2D,
    side: CircleTrajectory2D.Direction,
): StraightTrajectory2D? =
    with(Euclidean2DSpace) {
        val centers = StraightTrajectory2D(base.center, direction.center)
        if (centers.length < base.radius * 2) return null
        val angle = theta(
            when (side) {
                CircleTrajectory2D.Direction.LEFT -> centers.bearing + acos(base.radius * 2 / centers.length)
                CircleTrajectory2D.Direction.RIGHT -> centers.bearing - acos(base.radius * 2 / centers.length)
            }
        )
        val dX = base.radius * sin(angle)
        val dY = base.radius * cos(angle)
        val p1 = vector(base.center.x + dX, base.center.y + dY)
        val p2 = vector(direction.center.x - dX, direction.center.y - dY)
        return StraightTrajectory2D(p1, p2)
    }

internal fun theta(theta: Double): Double = (theta + (2 * PI)) % (2 * PI)


@Suppress("DuplicatedCode")
public object DubinsPath {

    public enum class Type {
        RLR, LRL, RSR, LSL, RSL, LSR
    }

    /**
     * Return Dubins trajectory type or null if trajectory is not a Dubins path
     */
    public fun trajectoryTypeOf(trajectory2D: CompositeTrajectory2D): Type?{
        if(trajectory2D.segments.size != 3) return null
        val a = trajectory2D.segments.first() as? CircleTrajectory2D ?: return null
        val b = trajectory2D.segments[1]
        val c = trajectory2D.segments.last() as? CircleTrajectory2D ?: return null
        return Type.valueOf(
            arrayOf(
                a.direction.name[0],
                if (b is CircleTrajectory2D) b.direction.name[0] else 'S',
                c.direction.name[0]
            ).toCharArray().concatToString()
        )
    }

    public fun all(
        start: DubinsPose2D,
        end: DubinsPose2D,
        turningRadius: Double,
    ): List<CompositeTrajectory2D> = listOfNotNull(
        rlr(start, end, turningRadius),
        lrl(start, end, turningRadius),
        rsr(start, end, turningRadius),
        lsl(start, end, turningRadius),
        rsl(start, end, turningRadius),
        lsr(start, end, turningRadius)
    )

    public fun shortest(start: DubinsPose2D, end: DubinsPose2D, turningRadius: Double): CompositeTrajectory2D =
        all(start, end, turningRadius).minBy { it.length }

    public fun rlr(start: DubinsPose2D, end: DubinsPose2D, turningRadius: Double): CompositeTrajectory2D? =
        with(Euclidean2DSpace) {
            val c1 = start.getRightCircle(turningRadius)
            val c2 = end.getRightCircle(turningRadius)
            val centers = StraightTrajectory2D(c1.center, c2.center)
            if (centers.length > turningRadius * 4) return null

            val firstVariant = run {
                var theta = theta(centers.bearing - acos(centers.length / (turningRadius * 4)))
                var dX = turningRadius * sin(theta)
                var dY = turningRadius * cos(theta)
                val p = vector(c1.center.x + dX * 2, c1.center.y + dY * 2)
                val e = Circle2D(p, turningRadius)
                val p1 = vector(c1.center.x + dX, c1.center.y + dY)
                theta = theta(centers.bearing + acos(centers.length / (turningRadius * 4)))
                dX = turningRadius * sin(theta)
                dY = turningRadius * cos(theta)
                val p2 = vector(e.center.x + dX, e.center.y + dY)
                val a1 = CircleTrajectory2D.of(c1.center, start, p1, CircleTrajectory2D.Direction.RIGHT)
                val a2 = CircleTrajectory2D.of(e.center, p1, p2, CircleTrajectory2D.Direction.LEFT)
                val a3 = CircleTrajectory2D.of(c2.center, p2, end, CircleTrajectory2D.Direction.RIGHT)
                CompositeTrajectory2D(a1, a2, a3)
            }

            val secondVariant = run {
                var theta = theta(centers.bearing + acos(centers.length / (turningRadius * 4)))
                var dX = turningRadius * sin(theta)
                var dY = turningRadius * cos(theta)
                val p = vector(c1.center.x + dX * 2, c1.center.y + dY * 2)
                val e = Circle2D(p, turningRadius)
                val p1 = vector(c1.center.x + dX, c1.center.y + dY)
                theta = theta(centers.bearing - acos(centers.length / (turningRadius * 4)))
                dX = turningRadius * sin(theta)
                dY = turningRadius * cos(theta)
                val p2 = vector(e.center.x + dX, e.center.y + dY)
                val a1 = CircleTrajectory2D.of(c1.center, start, p1, CircleTrajectory2D.Direction.RIGHT)
                val a2 = CircleTrajectory2D.of(e.center, p1, p2, CircleTrajectory2D.Direction.LEFT)
                val a3 = CircleTrajectory2D.of(c2.center, p2, end, CircleTrajectory2D.Direction.RIGHT)
                CompositeTrajectory2D(a1, a2, a3)
            }

            return if (firstVariant.length < secondVariant.length) firstVariant else secondVariant
        }

    public fun lrl(start: DubinsPose2D, end: DubinsPose2D, turningRadius: Double): CompositeTrajectory2D? =
        with(Euclidean2DSpace) {
            val c1 = start.getLeftCircle(turningRadius)
            val c2 = end.getLeftCircle(turningRadius)
            val centers = StraightTrajectory2D(c1.center, c2.center)
            if (centers.length > turningRadius * 4) return null

            val firstVariant = run {
                var theta = theta(centers.bearing + acos(centers.length / (turningRadius * 4)))
                var dX = turningRadius * sin(theta)
                var dY = turningRadius * cos(theta)
                val p = vector(c1.center.x + dX * 2, c1.center.y + dY * 2)
                val e = Circle2D(p, turningRadius)
                val p1 = vector(c1.center.x + dX, c1.center.y + dY)
                theta = theta(centers.bearing - acos(centers.length / (turningRadius * 4)))
                dX = turningRadius * sin(theta)
                dY = turningRadius * cos(theta)
                val p2 = vector(e.center.x + dX, e.center.y + dY)
                val a1 = CircleTrajectory2D.of(c1.center, start, p1, CircleTrajectory2D.Direction.LEFT)
                val a2 = CircleTrajectory2D.of(e.center, p1, p2, CircleTrajectory2D.Direction.RIGHT)
                val a3 = CircleTrajectory2D.of(c2.center, p2, end, CircleTrajectory2D.Direction.LEFT)
                CompositeTrajectory2D(a1, a2, a3)
            }

            val secondVariant = run {
                var theta = theta(centers.bearing - acos(centers.length / (turningRadius * 4)))
                var dX = turningRadius * sin(theta)
                var dY = turningRadius * cos(theta)
                val p = vector(c1.center.x + dX * 2, c1.center.y + dY * 2)
                val e = Circle2D(p, turningRadius)
                val p1 = vector(c1.center.x + dX, c1.center.y + dY)
                theta = theta(centers.bearing + acos(centers.length / (turningRadius * 4)))
                dX = turningRadius * sin(theta)
                dY = turningRadius * cos(theta)
                val p2 = vector(e.center.x + dX, e.center.y + dY)
                val a1 = CircleTrajectory2D.of(c1.center, start, p1, CircleTrajectory2D.Direction.LEFT)
                val a2 = CircleTrajectory2D.of(e.center, p1, p2, CircleTrajectory2D.Direction.RIGHT)
                val a3 = CircleTrajectory2D.of(c2.center, p2, end, CircleTrajectory2D.Direction.LEFT)
                CompositeTrajectory2D(a1, a2, a3)
            }

            return if (firstVariant.length < secondVariant.length) firstVariant else secondVariant
        }

    public fun rsr(start: DubinsPose2D, end: DubinsPose2D, turningRadius: Double): CompositeTrajectory2D {
        val c1 = start.getRightCircle(turningRadius)
        val c2 = end.getRightCircle(turningRadius)
        val s = leftOuterTangent(c1, c2)
        val a1 = CircleTrajectory2D.of(c1.center, start, s.start, CircleTrajectory2D.Direction.RIGHT)
        val a3 = CircleTrajectory2D.of(c2.center, s.end, end, CircleTrajectory2D.Direction.RIGHT)
        return CompositeTrajectory2D(a1, s, a3)
    }

    public fun lsl(start: DubinsPose2D, end: DubinsPose2D, turningRadius: Double): CompositeTrajectory2D {
        val c1 = start.getLeftCircle(turningRadius)
        val c2 = end.getLeftCircle(turningRadius)
        val s = rightOuterTangent(c1, c2)
        val a1 = CircleTrajectory2D.of(c1.center, start, s.start, CircleTrajectory2D.Direction.LEFT)
        val a3 = CircleTrajectory2D.of(c2.center, s.end, end, CircleTrajectory2D.Direction.LEFT)
        return CompositeTrajectory2D(a1, s, a3)
    }

    public fun rsl(start: DubinsPose2D, end: DubinsPose2D, turningRadius: Double): CompositeTrajectory2D? {
        val c1 = start.getRightCircle(turningRadius)
        val c2 = end.getLeftCircle(turningRadius)
        val s = rightInnerTangent(c1, c2)
        if (s == null || c1.center.distanceTo(c2.center) < turningRadius * 2) return null

        val a1 = CircleTrajectory2D.of(c1.center, start, s.start, CircleTrajectory2D.Direction.RIGHT)
        val a3 = CircleTrajectory2D.of(c2.center, s.end, end, CircleTrajectory2D.Direction.LEFT)
        return CompositeTrajectory2D(a1, s, a3)
    }

    public fun lsr(start: DubinsPose2D, end: DubinsPose2D, turningRadius: Double): CompositeTrajectory2D? {
        val c1 = start.getLeftCircle(turningRadius)
        val c2 = end.getRightCircle(turningRadius)
        val s = leftInnerTangent(c1, c2)
        if (s == null || c1.center.distanceTo(c2.center) < turningRadius * 2) return null

        val a1 = CircleTrajectory2D.of(c1.center, start, s.start, CircleTrajectory2D.Direction.LEFT)
        val a3 = CircleTrajectory2D.of(c2.center, s.end, end, CircleTrajectory2D.Direction.RIGHT)
        return CompositeTrajectory2D(a1, s, a3)
    }
}

public fun interface MaxCurvature {
    public fun compute(startPoint: PhaseVector2D): Double
}

public fun DubinsPath.shortest(
    start: PhaseVector2D,
    end: PhaseVector2D,
    maxCurvature: MaxCurvature,
): CompositeTrajectory2D = shortest(start, end, maxCurvature.compute(start))

