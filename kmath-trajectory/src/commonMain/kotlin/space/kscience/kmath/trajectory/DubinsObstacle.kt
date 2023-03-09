/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.*
import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo
import space.kscience.kmath.geometry.Euclidean2DSpace.minus
import space.kscience.kmath.geometry.Euclidean2DSpace.plus
import space.kscience.kmath.geometry.Euclidean2DSpace.times
import space.kscience.kmath.geometry.Euclidean2DSpace.vector
import space.kscience.kmath.geometry.Euclidean2DSpace.norm
import space.kscience.kmath.operations.DoubleField.pow
import kotlin.math.*

public fun LineSegment2D.length(): Double {
    return ((end.y - begin.y).pow(2.0) + (end.x - begin.x).pow(2.0)).pow(0.5)
}
public class DubinsObstacle(
    public val circles: List<Circle2D>
) {
    public val tangents: List<DubinsTangent> = boundaryTangents().first
    public val boundaryRoute: DubinsPath.Type = boundaryTangents().second
    public val center: Vector2D<Double> =
        vector(this.circles.sumOf{it.center.x} / this.circles.size,
               this.circles.sumOf{it.center.y} / this.circles.size)
    private fun boundaryTangents(): Pair<List<DubinsTangent>, DubinsPath.Type> {
        // outer tangents for a polygon circles can be either lsl or rsr

        fun Circle2D.dubinsTangentsToCircles(
            other: Circle2D,
        ): Map<DubinsPath.Type, DubinsTangent> = with(Euclidean2DSpace) {
            val line = LineSegment(center, other.center)
            val d = line.begin.distanceTo(line.end)
            val angle1 = atan2(other.center.x - center.x, other.center.y - center.y)
            var r: Double
            var angle2: Double
            val routes = mapOf(
                DubinsPath.Type.RSR to Pair(radius, other.radius),
//                DubinsPath.Type.RSL to Pair(radius, -other.radius),
//                DubinsPath.Type.LSR to Pair(-radius, other.radius),
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
                    if (d * d >= r * r) {
                        val l = (d * d - r * r).pow(0.5)
                        angle2 = if (r1.absoluteValue > r2.absoluteValue) {
                            angle1 + r1.sign * atan2(r.absoluteValue, l)
                        } else {
                            angle1 - r2.sign * atan2(r.absoluteValue, l)
                        }
                        val w = vector(-cos(angle2), sin(angle2))
                        put(route, DubinsTangent(Circle2D(center, radius),
                            other,
                            this@DubinsObstacle,
                            this@DubinsObstacle,
                            LineSegment2D(
                                center + w * r1,
                                other.center + w * r2
                            ),
                            DubinsPath.toSimpleTypes(route))
                        )
                    } else {
                        throw Exception("Circles should not intersect")
                    }
                }
            }
        }
//        val firstCircles = this.circles.slice(-this.circles.size..-1)
//        val secondCircles = this.circles.slice(-this.circles.size+1..0)
        val firstCircles = this.circles
        val secondCircles = this.circles.slice(1..this.circles.lastIndex) +
                this.circles[0]
        val lslTangents = firstCircles.zip(secondCircles)
        {a, b -> a.dubinsTangentsToCircles(b)[DubinsPath.Type.LSL]!!}
        val rsrTangents = firstCircles.zip(secondCircles)
        {a, b -> a.dubinsTangentsToCircles(b)[DubinsPath.Type.RSR]!!}
        val center = vector(
            this.circles.sumOf { it.center.x } / this.circles.size,
            this.circles.sumOf { it.center.y } / this.circles.size
        )
        val lslToCenter = lslTangents.sumOf { it.lineSegment.begin.distanceTo(center) } +
                lslTangents.sumOf { it.lineSegment.end.distanceTo(center) }
        val rsrToCenter = rsrTangents.sumOf { it.lineSegment.begin.distanceTo(center) } +
                rsrTangents.sumOf { it.lineSegment.end.distanceTo(center) }
        return if (rsrToCenter >= lslToCenter) {
            Pair(rsrTangents, DubinsPath.Type.RSR)
        } else {
            Pair(lslTangents, DubinsPath.Type.LSL)
        }
    }

    public fun nextTangent(circle: Circle2D, route: DubinsPath.Type): DubinsTangent {
        if (route == this.boundaryRoute) {
            for (i in this.circles.indices) {
                if (this.circles[i] == circle) {
                    return this.tangents[i]
                }
            }
        }
        else {
            for (i in this.circles.indices) {
                if (this.circles[i] == circle) {
                    return DubinsTangent(this.circles[i],
                                         this.circles[i-1],
                                         this,
                                         this,
                                         LineSegment2D(this.tangents[i-1].lineSegment.end,
                                                        this.tangents[i-1].lineSegment.begin),
                                         DubinsPath.toSimpleTypes(route))
                }
            }
        }

    error("next tangent not found")
    }

    public fun equals(other: DubinsObstacle): Boolean {
        return this.circles == other.circles
    }
}

public data class DubinsTangent(val startCircle: Circle2D,
                                val endCircle: Circle2D,
                                val startObstacle: DubinsObstacle,
                                val endObstacle: DubinsObstacle,
                                val lineSegment: LineSegment2D,
                                val route: PathTypes)

public fun LineSegment2D.intersectSegment(other: LineSegment2D): Boolean {
    fun crossProduct(v1: DoubleVector2D, v2: DoubleVector2D): Double {
        return v1.x * v2.y - v1.y * v2.x
    }
    if (crossProduct(other.begin - this.begin, other.end - this.begin).sign ==
        crossProduct(other.begin - this.end, other.end - this.end).sign) {
        return false
    }
    if (crossProduct(this.begin - other.begin, this.end - other.begin).sign ==
        crossProduct(this.begin - other.end, this.end - other.end).sign) {
        return false
    }
    return true
}

public fun LineSegment2D.intersectCircle(circle: Circle2D): Boolean {
    val a = (this.begin.x - this.end.x).pow(2.0) + (this.begin.y - this.end.y).pow(2.0)
    val b = 2 * ((this.begin.x - this.end.x) * (this.end.x - circle.center.x) +
            (this.begin.y - this.end.y) * (this.end.y - circle.center.y))
    val c = (this.end.x - circle.center.x).pow(2.0) + (this.end.y - circle.center.y) -
            circle.radius.pow(2.0)
    val d = b.pow(2.0) - 4 * a * c
    if (d < 1e-6) {
        return false
    }
    else {
        val t1 = (-b - d.pow(0.5)) * 0.5 / a
        val t2 = (-b + d.pow(0.5)) * 0.5 / a
        if (((0 < t1) and (t1 < 1)) or ((0 < t2) and (t2 < 1))) {
            return true
        }
    }
    return false
}

public fun DubinsTangent.intersectObstacle(obstacle: DubinsObstacle): Boolean {
    for (tangent in obstacle.tangents) {
        if (this.lineSegment.intersectSegment(tangent.lineSegment)) {
            return true
        }
    }
    for (circle in obstacle.circles) {
        if (this.lineSegment.intersectCircle(circle)) {
            return true
        }
    }
    return false
}

public fun outerTangents(first: DubinsObstacle, second: DubinsObstacle): MutableMap<DubinsPath.Type, DubinsTangent> {
    return buildMap {
        for (circle1 in first.circles) {
            for (circle2 in second.circles) {
                for (tangent in dubinsTangentsToCircles(circle1, circle2, first, second)) {
                    if (!(tangent.value.intersectObstacle(first))
                        and !(tangent.value.intersectObstacle(second))) {
                        put(
                            tangent.key,
                            tangent.value
                        )
                    }
                }
            }
        }
    }.toMutableMap()
}

public fun arcLength(circle: Circle2D,
                     point1: DoubleVector2D,
                     point2: DoubleVector2D,
                     route: DubinsPath.SimpleType): Double {
    val phi1 = atan2(point1.y - circle.center.y, point1.x - circle.center.x)
    val phi2 = atan2(point2.y - circle.center.y, point2.x - circle.center.x)
    var angle = 0.0
    when (route) {
        DubinsPath.SimpleType.L -> {
            angle = if (phi2 >= phi1) {
                phi2 - phi1
            } else {
                2 * PI + phi2 - phi1
            }
        }
        DubinsPath.SimpleType.R -> {
            angle = if (phi2 >= phi1) {
                2 * PI - (phi2 - phi1)
            } else {
                -(phi2 - phi1)
            }
        }
        DubinsPath.SimpleType.S -> {
            error("L or R route is expected")
        }
    }
    return circle.radius * angle
}

public fun normalVectors(v: DoubleVector2D, r: Double): Pair<DoubleVector2D, DoubleVector2D> {
    return Pair(
        r * vector(v.y / norm(v), -v.x / norm(v)),
        r * vector(-v.y / norm(v), v.x / norm(v))
    )
}

public fun constructTangentCircles(point: DoubleVector2D,
                                   direction: DoubleVector2D,
                                   r: Double): Map<DubinsPath.SimpleType, Circle2D> {
    val center1 = point + normalVectors(direction, r).first
    val center2 = point + normalVectors(direction, r).second
    val p1 = center1 - point
    val p2 = center2 - point
    return if (atan2(p1.y, p1.x) - atan2(p2.y, p2.x) in listOf(PI/2, -3*PI/2)) {
        mapOf(DubinsPath.SimpleType.L to Circle2D(center1, r),
            DubinsPath.SimpleType.R to Circle2D(center2, r))
    }
    else {
        mapOf(DubinsPath.SimpleType.L to Circle2D(center2, r),
            DubinsPath.SimpleType.R to Circle2D(center1, r))
    }
}

public fun sortedObstacles(currentObstacle: DubinsObstacle,
                           obstacles: List<DubinsObstacle>): List<DubinsObstacle> {
    return obstacles.sortedBy {norm(it.center - currentObstacle.center)}.reversed()
}

public fun tangentsAlongTheObstacle(initialCircle: Circle2D,
                                    initialRoute: DubinsPath.Type,
                                    finalCircle: Circle2D,
                                    obstacle: DubinsObstacle): MutableList<DubinsTangent> {
    val dubinsTangents = mutableListOf<DubinsTangent>()
    var tangent = obstacle.nextTangent(initialCircle, initialRoute)
    dubinsTangents.add(tangent)
    while (tangent.endCircle != finalCircle) {
        tangent = obstacle.nextTangent(tangent.endCircle, initialRoute)
        dubinsTangents.add(tangent)
    }
    return dubinsTangents
}

public fun allFinished(paths: List<List<DubinsTangent>>,
                       finalObstacle: DubinsObstacle): Boolean {
    for (path in paths) {
        if (path.last().endObstacle != finalObstacle) {
            return false
        }
    }
    return true
}

public fun pathLength(path: List<DubinsTangent>): Double {
    val tangentsLength = path.sumOf{norm(it.lineSegment.end - it.lineSegment.begin)}
    val arcsLength = buildList<Double>{
        for (i in 1..path.size) {
            add(arcLength(path[i].startCircle,
                path[i-1].lineSegment.end,
                path[i].lineSegment.begin,
                path[i].route[0]))
        }
    }.sum()
    return tangentsLength + arcsLength
}

public fun shortestPath(path: List<List<DubinsTangent>>): List<DubinsTangent> {
    return path.sortedBy { pathLength(it) }[0]
}

public typealias Path = List<DubinsTangent>
public fun findAllPaths(
    startingPoint: DoubleVector2D,
    startingDirection: DoubleVector2D,
    startingRadius: Double,
    finalPoint: DoubleVector2D,
    finalDirection: DoubleVector2D,
    finalRadius: Double,
    obstacles: List<DubinsObstacle>
): List<MutableList<DubinsTangent>> {
    val initialCircles = constructTangentCircles(
        startingPoint,
        startingDirection,
        startingRadius)
    val finalCircles = constructTangentCircles(
        finalPoint,
        finalDirection,
        finalRadius)
    var outputTangents = mutableMapOf<PathTypes, MutableList<MutableList<DubinsTangent>>>()
    for (i in listOf(DubinsPath.SimpleType.L, DubinsPath.SimpleType.R)) {
        for (j in listOf(DubinsPath.SimpleType.L, DubinsPath.SimpleType.R)) {
            val finalCircle = finalCircles[j]!!
            val finalObstacle = DubinsObstacle(listOf(finalCircle))
            outputTangents[listOf(i,
                           DubinsPath.SimpleType.S,
                           j)] = mutableListOf(
                                    mutableListOf(DubinsTangent(
                                        initialCircles[i]!!,
                                        initialCircles[i]!!,
                                        DubinsObstacle(listOf(initialCircles[i]!!)),
                                        DubinsObstacle(listOf(initialCircles[i]!!)),
                                        LineSegment2D(startingPoint, startingPoint),
                                        listOf(i, DubinsPath.SimpleType.S, i)
                                    )))
            var currentObstacle = DubinsObstacle(listOf(initialCircles[i]!!))
            while (!allFinished(outputTangents[listOf(i,
                    DubinsPath.SimpleType.S,
                    j)]!!, finalObstacle)) {
                var newOutputTangents = mutableListOf<MutableList<DubinsTangent>>()
                for (line in outputTangents[listOf(i,
                    DubinsPath.SimpleType.S,
                    j)]!!) {
                    var currentCircle = line.last().endCircle
                    var currentDirection = line.last().route.last()
                    var currentObstacle = line.last().endObstacle
                    var nextObstacle: DubinsObstacle? = null
                    if (currentObstacle != finalObstacle) {
                        var tangentToFinal = outerTangents(currentObstacle, finalObstacle)[DubinsPath.toType(listOf(
                            currentDirection,
                            DubinsPath.SimpleType.S,
                            j)
                        )]
                        for (obstacle in sortedObstacles(currentObstacle, obstacles)) {
                            if (tangentToFinal!!.intersectObstacle(obstacle)) {
                                nextObstacle = obstacle
                                break
                            }
                        }
                        if (nextObstacle == null) {
                            nextObstacle = finalObstacle
                        }
                        var nextTangents = outerTangents(currentObstacle, nextObstacle)

                        for (pathType in nextTangents.keys) {
                            for (obstacle in obstacles) {
                                // in Python code here try/except was used, but seems unneeded
                                if (nextTangents[pathType]!!.intersectObstacle(obstacle)) {
                                    nextTangents.remove(pathType)
                                }

                            }
                        }
                        nextTangents = if (nextObstacle == finalObstacle) {
                            nextTangents.filter {(DubinsPath.toSimpleTypes(it.key)[0] == currentDirection) and
                                    (DubinsPath.toSimpleTypes(it.key)[0] == j)}
                                    as MutableMap<DubinsPath.Type, DubinsTangent>
                        } else {
                            nextTangents.filter {(DubinsPath.toSimpleTypes(it.key)[0] == currentDirection)}
                                    as MutableMap<DubinsPath.Type, DubinsTangent>
                        }
                        val tangentsAlong = mutableListOf<DubinsTangent>()
                        for (tangent in nextTangents.values) {
                            if (tangent.startCircle == line.last().endCircle) {
                                val lengthMaxPossible = arcLength(
                                    tangent.startCircle,
                                    line.last().lineSegment.end,
                                    tangent.startObstacle.nextTangent(
                                        tangent.startCircle,
                                        DubinsPath.toType(listOf(currentDirection, DubinsPath.SimpleType.S, currentDirection)),
                                    ).lineSegment.begin,
                                    currentDirection
                                    )
                                val lengthCalculated = arcLength(
                                    tangent.startCircle,
                                    line.last().lineSegment.end,
                                    tangent.lineSegment.begin,
                                    currentDirection)
                                if (lengthCalculated > lengthMaxPossible) {
                                    val tangentsAlong = tangentsAlongTheObstacle(
                                        currentCircle,
                                        DubinsPath.toType(listOf(
                                            currentDirection,
                                            DubinsPath.SimpleType.S,
                                            currentDirection)),
                                        tangent.startCircle,
                                        currentObstacle
                                    )
                                }
                                else {
                                    val tangentsAlong = mutableListOf<DubinsTangent>()
                                }
                            }
                            else {
                                val tangentsAlong = tangentsAlongTheObstacle(
                                    currentCircle,
                                    DubinsPath.toType(listOf(
                                        currentDirection,
                                        DubinsPath.SimpleType.S,
                                        currentDirection)),
                                    tangent.startCircle,
                                    currentObstacle
                                )
                            }
                            newOutputTangents.add((line + tangentsAlong + listOf(tangent)).toMutableList())
                        }
                        outputTangents[listOf(
                            i,
                            DubinsPath.SimpleType.S,
                            j
                        )] = newOutputTangents
                    }
                    else {
                        // minor changes from Python code
                        newOutputTangents.add(line)
                        outputTangents[listOf(
                            i,
                            DubinsPath.SimpleType.S,
                            j
                        )] = newOutputTangents
                    }
                }
            }
            for (lineId in outputTangents[listOf(
                i,
                DubinsPath.SimpleType.S,
                j
            )]!!.indices) {
                val lastDirection = outputTangents[listOf(
                    i,
                    DubinsPath.SimpleType.S,
                    j
                )]!![lineId].last().route[2]
                outputTangents[listOf(
                    i,
                    DubinsPath.SimpleType.S,
                    j
                )]!![lineId].add(DubinsTangent(
                    finalCircles[j]!!,
                    finalCircles[j]!!,
                    DubinsObstacle(
                        listOf(finalCircles[j]!!)
                    ),
                    DubinsObstacle(
                        listOf(finalCircles[j]!!)
                    ),
                    LineSegment2D(finalPoint, finalPoint),
                    listOf(
                        lastDirection,
                        DubinsPath.SimpleType.S,
                        j
                    )
                ))
            }
        }
    }
    return outputTangents[listOf(
        DubinsPath.SimpleType.L,
        DubinsPath.SimpleType.S,
        DubinsPath.SimpleType.L
        )]!! + outputTangents[listOf(
        DubinsPath.SimpleType.L,
        DubinsPath.SimpleType.S,
        DubinsPath.SimpleType.R
        )]!! + outputTangents[listOf(
        DubinsPath.SimpleType.R,
        DubinsPath.SimpleType.S,
        DubinsPath.SimpleType.L
        )]!! + outputTangents[listOf(
        DubinsPath.SimpleType.R,
        DubinsPath.SimpleType.S,
        DubinsPath.SimpleType.L)]!!
}








