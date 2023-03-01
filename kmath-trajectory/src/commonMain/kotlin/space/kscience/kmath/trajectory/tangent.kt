/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.*
import space.kscience.kmath.geometry.Euclidean2DSpace.plus
import space.kscience.kmath.geometry.Euclidean2DSpace.times
import space.kscience.kmath.operations.DoubleField.pow
import kotlin.math.*

/**
 * Create inner and outer tangents between two circles.
 * This method returns a map of segments using [DubinsPath] connection type notation.
 */
public fun Circle2D.tangentsToCircle(
    other: Circle2D,
): Map<DubinsPath.Type, LineSegment2D> = with(Euclidean2DSpace) {
    val line = LineSegment(center, other.center)
    val d = line.begin.distanceTo(line.end)
    val angle1 = atan2(other.center.x - center.x, other.center.y - center.y)
    var r: Double
    var angle2: Double
    val routes = mapOf(
        DubinsPath.Type.RSR to Pair(radius, other.radius),
        DubinsPath.Type.RSL to Pair(radius, -other.radius),
        DubinsPath.Type.LSR to Pair(-radius, other.radius),
        DubinsPath.Type.LSL to Pair(-radius, -other.radius)
    )
    return buildMap {
        for ((route, r1r2) in routes) {
            val r1 = r1r2.first
            val r2 = r1r2.second
            r = if (r1.sign == r2.sign) {
                r1.absoluteValue - r2.absoluteValue
            } else {
                r1.absoluteValue + r2.absoluteValue
            }
            if (d * d > r * r) {
                val l = (d * d - r * r).pow(0.5)
                angle2 = if (r1.absoluteValue > r2.absoluteValue) {
                    angle1 + r1.sign * atan2(r.absoluteValue, l)
                } else {
                    angle1 - r2.sign * atan2(r.absoluteValue, l)
                }
                val w = vector(-cos(angle2), sin(angle2))
                put(
                    route,
                    LineSegment2D(
                        center + w * r1,
                        other.center + w * r2
                    )
                )
            }
            else {
                throw Exception("Circles should not intersect")
            }
        }
    }
}

public fun dubinsTangentsToCircles(
    firstCircle: Circle2D,
    secondCircle: Circle2D,
    firstObstacle: DubinsObstacle,
    secondObstacle: DubinsObstacle
): Map<DubinsPath.Type, DubinsTangent> = with(Euclidean2DSpace) {
    val line = LineSegment(firstCircle.center, secondCircle.center)
    val d = line.begin.distanceTo(line.end)
    val angle1 = atan2(secondCircle.center.x - firstCircle.center.x,
        secondCircle.center.y - firstCircle.center.y)
    var r: Double
    var angle2: Double
    val routes = mapOf(
        DubinsPath.Type.RSR to Pair(firstCircle.radius, secondCircle.radius),
        DubinsPath.Type.RSL to Pair(firstCircle.radius, -secondCircle.radius),
        DubinsPath.Type.LSR to Pair(-firstCircle.radius, secondCircle.radius),
        DubinsPath.Type.LSL to Pair(-firstCircle.radius, -secondCircle.radius)
    )
    return buildMap {
        for ((route, r1r2) in routes) {
            val r1 = r1r2.first
            val r2 = r1r2.second
            r = if (r1.sign == r2.sign) {
                r1.absoluteValue - r2.absoluteValue
            } else {
                r1.absoluteValue + r2.absoluteValue
            }
            if (d * d > r * r) {
                val l = (d * d - r * r).pow(0.5)
                angle2 = if (r1.absoluteValue > r2.absoluteValue) {
                    angle1 + r1.sign * atan2(r.absoluteValue, l)
                } else {
                    angle1 - r2.sign * atan2(r.absoluteValue, l)
                }
                val w = Euclidean2DSpace.vector(-cos(angle2), sin(angle2))
                put(route, DubinsTangent(Circle2D(firstCircle.center, firstCircle.radius),
                    secondCircle,
                    firstObstacle,
                    secondObstacle,
                    LineSegment2D(
                        firstCircle.center + w * r1,
                        secondCircle.center + w * r2
                    ),
                    DubinsPath.toSimpleTypes(route))
                )
            } else {
                throw Exception("Circles should not intersect")
            }
        }
    }
}