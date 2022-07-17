/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory.dubins

import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo
import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.trajectory.segments.*
import space.kscience.kmath.trajectory.segments.components.Circle
import space.kscience.kmath.trajectory.segments.components.Pose2D
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

public class DubinsPathFactory(
    private val start: Pose2D,
    private val end: Pose2D,
    private val turningRadius: Double
) {

    public val all: List<DubinsPath> get() = listOfNotNull(rlr, lrl, rsr, lsl, rsl, lsr)
    public val shortest: DubinsPath get() = all.minByOrNull { it.length }!!
    public operator fun get(type: DubinsPath.TYPE): DubinsPath? = all.find { it.type == type }

    public val rlr: DubinsPath?
        get() {
            val c1 = start.getRightCircle(turningRadius)
            val c2 = end.getRightCircle(turningRadius)
            val centers = Straight(c1.center, c2.center)
            if (centers.length > turningRadius * 4) return null

            var theta = theta(centers.theta - acos(centers.length / (turningRadius * 4)))
            var dX = turningRadius * sin(theta)
            var dY = turningRadius * cos(theta)
            val p = Vector2D(c1.center.x + dX * 2, c1.center.y + dY * 2)
            val e = Circle(p, turningRadius)
            val p1 = Vector2D(c1.center.x + dX, c1.center.y + dY)
            theta = theta(centers.theta + acos(centers.length / (turningRadius * 4)))
            dX = turningRadius * sin(theta)
            dY = turningRadius * cos(theta)
            val p2 = Vector2D(e.center.x + dX, e.center.y + dY)
            val a1 = Arc(c1.center, start, p1, Arc.Direction.RIGHT)
            val a2 = Arc(e.center, p1, p2, Arc.Direction.LEFT)
            val a3 = Arc(c2.center, p2, end, Arc.Direction.RIGHT)
            return DubinsPath(a1, a2, a3)
        }

    private val lrl: DubinsPath?
        get() {
            val c1 = start.getLeftCircle(turningRadius)
            val c2 = end.getLeftCircle(turningRadius)
            val centers = Straight(c1.center, c2.center)
            if (centers.length > turningRadius * 4) return null

            var theta = theta(centers.theta + acos(centers.length / (turningRadius * 4)))
            var dX = turningRadius * sin(theta)
            var dY = turningRadius * cos(theta)
            val p = Vector2D(c1.center.x + dX * 2, c1.center.y + dY * 2)
            val e = Circle(p, turningRadius)
            val p1 = Vector2D(c1.center.x + dX, c1.center.y + dY)
            theta = theta(centers.theta - acos(centers.length / (turningRadius * 4)))
            dX = turningRadius * sin(theta)
            dY = turningRadius * cos(theta)
            val p2 = Vector2D(e.center.x + dX, e.center.y + dY)
            val a1 = Arc(c1.center, start, p1, Arc.Direction.LEFT)
            val a2 = Arc(e.center, p1, p2, Arc.Direction.RIGHT)
            val a3 = Arc(c2.center, p2, end, Arc.Direction.LEFT)
            return DubinsPath(a1, a2, a3)
        }

    public val rsr: DubinsPath
        get() {
            val c1 = start.getRightCircle(turningRadius)
            val c2 = end.getRightCircle(turningRadius)
            val s = leftOuterTangent(c1, c2)
            val a1 = Arc(c1.center, start, s.start, Arc.Direction.RIGHT)
            val a3 = Arc(c2.center, s.end, end, Arc.Direction.RIGHT)
            return DubinsPath(a1, s, a3)
        }

    public val lsl: DubinsPath
        get() {
            val c1 = start.getLeftCircle(turningRadius)
            val c2 = end.getLeftCircle(turningRadius)
            val s = rightOuterTangent(c1, c2)
            val a1 = Arc(c1.center, start, s.start, Arc.Direction.LEFT)
            val a3 = Arc(c2.center, s.end, end, Arc.Direction.LEFT)
            return DubinsPath(a1, s, a3)
        }

    public val rsl: DubinsPath?
        get() {
            val c1 = start.getRightCircle(turningRadius)
            val c2 = end.getLeftCircle(turningRadius)
            val s = rightInnerTangent(c1, c2)
            if (c1.center.distanceTo(c2.center) < turningRadius * 2 || s == null) return null

            val a1 = Arc(c1.center, start, s.start, Arc.Direction.RIGHT)
            val a3 = Arc(c2.center, s.end, end, Arc.Direction.LEFT)
            return DubinsPath(a1, s, a3)
        }

    public val lsr: DubinsPath?
        get() {
            val c1 = start.getLeftCircle(turningRadius)
            val c2 = end.getRightCircle(turningRadius)
            val s = leftInnerTangent(c1, c2)
            if (c1.center.distanceTo(c2.center) < turningRadius * 2 || s == null) return null

            val a1 = Arc(c1.center, start, s.start, Arc.Direction.LEFT)
            val a3 = Arc(c2.center, s.end, end, Arc.Direction.RIGHT)
            return DubinsPath(a1, s, a3)
        }
}

private enum class SIDE {
    LEFT, RIGHT
}

private fun Pose2D.getLeftCircle(radius: Double): Circle = getTangentCircles(radius).first
private fun Pose2D.getRightCircle(radius: Double): Circle = getTangentCircles(radius).second
private fun Pose2D.getTangentCircles(radius: Double): Pair<Circle, Circle> {
    val dX = radius * cos(theta)
    val dY = radius * sin(theta)
    return Circle(Vector2D(x - dX, y + dY), radius) to Circle(Vector2D(x + dX, y - dY), radius)
}

private fun leftOuterTangent(a: Circle, b: Circle) = outerTangent(a, b, SIDE.LEFT)
private fun rightOuterTangent(a: Circle, b: Circle) = outerTangent(a, b, SIDE.RIGHT)
private fun outerTangent(a: Circle, b: Circle, side: SIDE): Straight {
    val centers = Straight(a.center, b.center)
    val p1 = when (side) {
        SIDE.LEFT -> Vector2D(
            a.center.x - a.radius * cos(centers.theta),
            a.center.y + a.radius * sin(centers.theta)
        )
        SIDE.RIGHT -> Vector2D(
            a.center.x + a.radius * cos(centers.theta),
            a.center.y - a.radius * sin(centers.theta)
        )
    }
    return Straight(
        p1,
        Vector2D(p1.x + (centers.end.x - centers.start.x), p1.y + (centers.end.y - centers.start.y))
    )
}

private fun leftInnerTangent(base: Circle, direction: Circle) = innerTangent(base, direction, SIDE.LEFT)
private fun rightInnerTangent(base: Circle, direction: Circle) = innerTangent(base, direction, SIDE.RIGHT)
private fun innerTangent(base: Circle, direction: Circle, side: SIDE): Straight? {
    val centers = Straight(base.center, direction.center)
    if (centers.length < base.radius * 2) return null
    val angle = theta(
        when (side) {
            SIDE.LEFT -> centers.theta + acos(base.radius * 2 / centers.length)
            SIDE.RIGHT -> centers.theta - acos(base.radius * 2 / centers.length)
        }
    )
    val dX = base.radius * sin(angle)
    val dY = base.radius * cos(angle)
    val p1 = Vector2D(base.center.x + dX, base.center.y + dY)
    val p2 = Vector2D(direction.center.x - dX, direction.center.y - dY)
    return Straight(p1, p2)
}

internal fun theta(theta: Double) = (theta + (2 * PI)) % (2 * PI)
