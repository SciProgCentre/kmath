/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.*
import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo
import space.kscience.kmath.trajectory.Trajectory2D.*
import kotlin.math.acos

internal fun DubinsPose2D.getLeftCircle(radius: Double): Circle2D = getTangentCircles(radius).first

internal fun DubinsPose2D.getRightCircle(radius: Double): Circle2D = getTangentCircles(radius).second

internal fun DubinsPose2D.getTangentCircles(radius: Double): Pair<Circle2D, Circle2D> = with(Euclidean2DSpace) {
    val dX = radius * cos(bearing)
    val dY = radius * sin(bearing)
    return Circle2D(vector(x - dX, y + dY), radius) to Circle2D(vector(x + dX, y - dY), radius)
}

private fun outerTangent(from: Circle2D, to: Circle2D, direction: Direction): StraightTrajectory2D =
    with(Euclidean2DSpace) {
        val centers = StraightTrajectory2D(from.center, to.center)
        val p1 = when (direction) {
            L -> vector(
                from.center.x - from.radius * cos(centers.bearing),
                from.center.y + from.radius * sin(centers.bearing)
            )

            R -> vector(
                from.center.x + from.radius * cos(centers.bearing),
                from.center.y - from.radius * sin(centers.bearing)
            )
        }
        return StraightTrajectory2D(
            p1,
            vector(p1.x + (centers.end.x - centers.begin.x), p1.y + (centers.end.y - centers.begin.y))
        )
    }


private fun innerTangent(
    from: Circle2D,
    to: Circle2D,
    direction: Direction,
): StraightTrajectory2D? =
    with(Euclidean2DSpace) {
        val centers = StraightTrajectory2D(from.center, to.center)
        if (centers.length < from.radius * 2) return null
        val angle = when (direction) {
            L -> centers.bearing + acos(from.radius * 2 / centers.length).radians
            R -> centers.bearing - acos(from.radius * 2 / centers.length).radians
        }.normalized()

        val dX = from.radius * sin(angle)
        val dY = from.radius * cos(angle)
        val p1 = vector(from.center.x + dX, from.center.y + dY)
        val p2 = vector(to.center.x - dX, to.center.y - dY)
        return StraightTrajectory2D(p1, p2)
    }


@Suppress("DuplicatedCode")
public object DubinsPath {

    public data class Type(
        public val first: Direction,
        public val second: Trajectory2D.Type,
        public val third: Direction,
    ) {
        public fun toList(): List<Trajectory2D.Type> = listOf(first, second, third)

        override fun toString(): String = "${first}${second}${third}"

        public companion object {
            public val RLR: Type = Type(R, L, R)
            public val LRL: Type = Type(L, R, L)
            public val RSR: Type = Type(R, S, R)
            public val LSL: Type = Type(L, S, L)
            public val RSL: Type = Type(R, S, L)
            public val LSR: Type = Type(L, S, R)
        }
    }

    /**
     * Return Dubins trajectory type or null if trajectory is not a Dubins path
     */
    public fun trajectoryTypeOf(trajectory2D: CompositeTrajectory2D): Type? {
        if (trajectory2D.segments.size != 3) return null
        val a = trajectory2D.segments.first() as? CircleTrajectory2D ?: return null
        val b = trajectory2D.segments[1]
        val c = trajectory2D.segments.last() as? CircleTrajectory2D ?: return null
        return Type(
            a.direction,
            if (b is CircleTrajectory2D) b.direction else S,
            c.direction
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
                var theta = (centers.bearing - acos(centers.length / (turningRadius * 4)).radians).normalized()
                var dX = turningRadius * sin(theta)
                var dY = turningRadius * cos(theta)
                val p = vector(c1.center.x + dX * 2, c1.center.y + dY * 2)
                val e = Circle2D(p, turningRadius)
                val p1 = vector(c1.center.x + dX, c1.center.y + dY)
                theta = (centers.bearing + acos(centers.length / (turningRadius * 4)).radians).normalized()
                dX = turningRadius * sin(theta)
                dY = turningRadius * cos(theta)
                val p2 = vector(e.center.x + dX, e.center.y + dY)
                val a1 = CircleTrajectory2D.of(c1.center, start, p1, R)
                val a2 = CircleTrajectory2D.of(e.center, p1, p2, L)
                val a3 = CircleTrajectory2D.of(c2.center, p2, end, R)
                CompositeTrajectory2D(a1, a2, a3)
            }

            val secondVariant = run {
                var theta = (centers.bearing + acos(centers.length / (turningRadius * 4)).radians).normalized()
                var dX = turningRadius * sin(theta)
                var dY = turningRadius * cos(theta)
                val p = vector(c1.center.x + dX * 2, c1.center.y + dY * 2)
                val e = Circle2D(p, turningRadius)
                val p1 = vector(c1.center.x + dX, c1.center.y + dY)
                theta = (centers.bearing - acos(centers.length / (turningRadius * 4)).radians).normalized()
                dX = turningRadius * sin(theta)
                dY = turningRadius * cos(theta)
                val p2 = vector(e.center.x + dX, e.center.y + dY)
                val a1 = CircleTrajectory2D.of(c1.center, start, p1, R)
                val a2 = CircleTrajectory2D.of(e.center, p1, p2, L)
                val a3 = CircleTrajectory2D.of(c2.center, p2, end, R)
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
                var theta = (centers.bearing + acos(centers.length / (turningRadius * 4)).radians).normalized()
                var dX = turningRadius * sin(theta)
                var dY = turningRadius * cos(theta)
                val p = vector(c1.center.x + dX * 2, c1.center.y + dY * 2)
                val e = Circle2D(p, turningRadius)
                val p1 = vector(c1.center.x + dX, c1.center.y + dY)
                theta = (centers.bearing - acos(centers.length / (turningRadius * 4)).radians).normalized()
                dX = turningRadius * sin(theta)
                dY = turningRadius * cos(theta)
                val p2 = vector(e.center.x + dX, e.center.y + dY)
                val a1 = CircleTrajectory2D.of(c1.center, start, p1, L)
                val a2 = CircleTrajectory2D.of(e.center, p1, p2, R)
                val a3 = CircleTrajectory2D.of(c2.center, p2, end, L)
                CompositeTrajectory2D(a1, a2, a3)
            }

            val secondVariant = run {
                var theta = (centers.bearing - acos(centers.length / (turningRadius * 4)).radians).normalized()
                var dX = turningRadius * sin(theta)
                var dY = turningRadius * cos(theta)
                val p = vector(c1.center.x + dX * 2, c1.center.y + dY * 2)
                val e = Circle2D(p, turningRadius)
                val p1 = vector(c1.center.x + dX, c1.center.y + dY)
                theta = (centers.bearing + acos(centers.length / (turningRadius * 4)).radians).normalized()
                dX = turningRadius * sin(theta)
                dY = turningRadius * cos(theta)
                val p2 = vector(e.center.x + dX, e.center.y + dY)
                val a1 = CircleTrajectory2D.of(c1.center, start, p1, L)
                val a2 = CircleTrajectory2D.of(e.center, p1, p2, R)
                val a3 = CircleTrajectory2D.of(c2.center, p2, end, L)
                CompositeTrajectory2D(a1, a2, a3)
            }

            return if (firstVariant.length < secondVariant.length) firstVariant else secondVariant
        }

    public fun rsr(start: DubinsPose2D, end: DubinsPose2D, turningRadius: Double): CompositeTrajectory2D {
        val c1 = start.getRightCircle(turningRadius)
        val c2 = end.getRightCircle(turningRadius)
        val s = outerTangent(c1, c2, L)
        val a1 = CircleTrajectory2D.of(c1.center, start, s.begin, R)
        val a3 = CircleTrajectory2D.of(c2.center, s.end, end, R)
        return CompositeTrajectory2D(a1, s, a3)
    }

    public fun lsl(start: DubinsPose2D, end: DubinsPose2D, turningRadius: Double): CompositeTrajectory2D {
        val c1 = start.getLeftCircle(turningRadius)
        val c2 = end.getLeftCircle(turningRadius)
        val s = outerTangent(c1, c2, R)
        val a1 = CircleTrajectory2D.of(c1.center, start, s.begin, L)
        val a3 = CircleTrajectory2D.of(c2.center, s.end, end, L)
        return CompositeTrajectory2D(a1, s, a3)
    }

    public fun rsl(start: DubinsPose2D, end: DubinsPose2D, turningRadius: Double): CompositeTrajectory2D? {
        val c1 = start.getRightCircle(turningRadius)
        val c2 = end.getLeftCircle(turningRadius)
        val s = innerTangent(c1, c2, R)
        if (s == null || c1.center.distanceTo(c2.center) < turningRadius * 2) return null

        val a1 = CircleTrajectory2D.of(c1.center, start, s.begin, R)
        val a3 = CircleTrajectory2D.of(c2.center, s.end, end, L)
        return CompositeTrajectory2D(a1, s, a3)
    }

    public fun lsr(start: DubinsPose2D, end: DubinsPose2D, turningRadius: Double): CompositeTrajectory2D? {
        val c1 = start.getLeftCircle(turningRadius)
        val c2 = end.getRightCircle(turningRadius)
        val s = innerTangent(c1, c2, L)
        if (s == null || c1.center.distanceTo(c2.center) < turningRadius * 2) return null

        val a1 = CircleTrajectory2D.of(c1.center, start, s.begin, L)
        val a3 = CircleTrajectory2D.of(c2.center, s.end, end, R)
        return CompositeTrajectory2D(a1, s, a3)
    }
}

public typealias PathTypes = List<Type>

public fun interface MaxCurvature {
    public fun compute(startPoint: PhaseVector2D): Double
}

public fun DubinsPath.shortest(
    start: PhaseVector2D,
    end: PhaseVector2D,
    maxCurvature: MaxCurvature,
): CompositeTrajectory2D = shortest(start, end, maxCurvature.compute(start))

