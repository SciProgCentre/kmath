/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlinx.serialization.Serializable
import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo
import kotlin.math.*

/**
 * A circle in 2D space
 */
@Serializable
public data class Circle2D(
    @Serializable(Euclidean2DSpace.VectorSerializer::class) public val center: DoubleVector2D,
    public val radius: Double
)

public enum class DubinsRoutes {
    RSR, RSL, LSR, LSL
}

public fun Circle2D.tangentsToCircle(other: Circle2D): Map<DubinsRoutes, LineSegment<DoubleVector2D>> {
    val R1 = this.radius
    val R2 = other.radius
    val line = LineSegment(this.center, other.center)
    val d = line.begin.distanceTo(line.end)
    val angle1 = atan2(other.center.x - this.center.x, other.center.y - this.center.y)
    var r: Double
    var angle2: Double
    val routes = mapOf(
        DubinsRoutes.RSR to Pair(R1, R2),
        DubinsRoutes.RSL to Pair(R1, -R2),
        DubinsRoutes.LSR to Pair(-R1, R2),
        DubinsRoutes.LSL to Pair(-R1, -R2))
    val segments = mutableMapOf<DubinsRoutes, LineSegment<DoubleVector2D>>()
    for ((route, r1r2) in routes) {
        val r1 = r1r2.first
        val r2 = r1r2.second
        r = if (r1.sign == r2.sign) {
            r1.absoluteValue - r2.absoluteValue
        } else {
            r1.absoluteValue + r2.absoluteValue
        }
        val L = (d * d - r * r).pow(0.5)
        angle2 = if (r1.absoluteValue > r2.absoluteValue) {
            angle1 + r1.sign * atan2(r.absoluteValue, L)
        } else {
            angle1 - r2.sign * atan2(r.absoluteValue, L)
        }
        val W = Euclidean2DSpace.vector(-cos(angle2), sin(angle2))
        segments[route] = LineSegment(
            Euclidean2DSpace.add(this.center, Euclidean2DSpace.scale(W, r1)),
            Euclidean2DSpace.add(other.center, Euclidean2DSpace.scale(W, r2))
        )
    }
    return segments
}

public val Circle2D.circumference: Double get() = radius * 2 * PI
