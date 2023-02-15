/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.cos
import space.kscience.kmath.geometry.Euclidean2DSpace.vector
import space.kscience.kmath.geometry.Euclidean2DSpace.scale
import space.kscience.kmath.geometry.Euclidean2DSpace.add
import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo


//public class Segment(
//    public val startPoint: DoubleVector2D,
//    public val terminalPoint: DoubleVector2D,
//    public val length: Double = startPoint.distanceTo(terminalPoint)
//) {
//    public override operator fun equals(other: Any?): Boolean {
//        return if (other is Segment) {
//            startPoint.x.equalFloat(other.startPoint.x) && startPoint.y.equalFloat(other.startPoint.y) &&
//                    terminalPoint.x.equalFloat(other.terminalPoint.x) && terminalPoint.y.equalFloat(other.terminalPoint.y)
//        } else {
//            false
//        }
//    }
//}

//public const val maxFloatDelta: Double = 0.000001
//public fun Double.equalFloat(other: Double): Boolean = kotlin.math.abs(this - other) < maxFloatDelta



//public fun tangentsToCircles(
//    startCircle: Circle2D,
//    terminalCircle: Circle2D
//): kotlin.collections.MutableMap<String, LineSegment<DoubleVector2D>> {
//    val R1 = startCircle.radius
//    val R2 = terminalCircle.radius
//    val line = LineSegment(startCircle.center, terminalCircle.center)
//    val d = line.begin.distanceTo(line.end)
//    val angle1 = atan2(terminalCircle.center.x - startCircle.center.x, terminalCircle.center.y - startCircle.center.y)
//    var r: Double
//    var angle2: Double
//    val routes = mapOf("RSR" to Pair(R1, R2),
//        "RSL" to Pair(R1, -R2),
//        "LSR" to Pair(-R1, R2),
//        "LSL" to Pair(-R1, -R2))
//    val segments = mutableMapOf<String, LineSegment<DoubleVector2D>>()
//    for ((route, r1r2) in routes) {
//        val r1 = r1r2.first
//        val r2 = r1r2.second
//        r = if (r1.sign == r2.sign) {
//            r1.absoluteValue - r2.absoluteValue
//        } else {
//            r1.absoluteValue + r2.absoluteValue
//        }
//        val L = (d * d - r * r).pow(0.5)
//        angle2 = if (r1.absoluteValue > r2.absoluteValue) {
//            angle1 + r1.sign * atan2(r.absoluteValue, L)
//        } else {
//            angle1 - r2.sign * atan2(r.absoluteValue, L)
//        }
//        val W = vector(-cos(angle2), sin(angle2))
//        segments[route] = LineSegment(add(startCircle.center, scale(W, r1)),
//            add(terminalCircle.center, scale(W, r2)))
//    }
//    return segments
//}