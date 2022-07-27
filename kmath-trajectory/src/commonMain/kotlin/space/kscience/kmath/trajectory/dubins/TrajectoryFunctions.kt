/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory.dubins

import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.trajectory.segments.Straight
import space.kscience.kmath.trajectory.segments.components.Circle
import space.kscience.kmath.trajectory.segments.components.Pose2D
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

private enum class SIDE {
    LEFT, RIGHT
}

internal fun Pose2D.getLeftCircle(radius: Double): Circle = getTangentCircles(radius).first
internal fun Pose2D.getRightCircle(radius: Double): Circle = getTangentCircles(radius).second
internal fun Pose2D.getTangentCircles(radius: Double): Pair<Circle, Circle> {
    val dX = radius * cos(theta)
    val dY = radius * sin(theta)
    return Circle(Vector2D(x - dX, y + dY), radius) to Circle(Vector2D(x + dX, y - dY), radius)
}

internal fun leftOuterTangent(a: Circle, b: Circle) = outerTangent(a, b, SIDE.LEFT)
internal fun rightOuterTangent(a: Circle, b: Circle) = outerTangent(a, b, SIDE.RIGHT)
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

internal fun leftInnerTangent(base: Circle, direction: Circle) = innerTangent(base, direction, SIDE.LEFT)
internal fun rightInnerTangent(base: Circle, direction: Circle) = innerTangent(base, direction, SIDE.RIGHT)
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
