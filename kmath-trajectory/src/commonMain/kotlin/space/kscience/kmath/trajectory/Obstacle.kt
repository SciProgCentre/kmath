/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.*
import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo
import space.kscience.kmath.geometry.Euclidean2DSpace.minus
import space.kscience.kmath.geometry.Euclidean2DSpace.norm
import space.kscience.kmath.geometry.Euclidean2DSpace.plus
import space.kscience.kmath.geometry.Euclidean2DSpace.times
import space.kscience.kmath.geometry.Euclidean2DSpace.vector
import space.kscience.kmath.operations.DoubleField.pow
import kotlin.math.*

internal data class Tangent(
    val startCircle: Circle2D,
    val endCircle: Circle2D,
    val startObstacle: Obstacle,
    val endObstacle: Obstacle,
    val lineSegment: LineSegment2D,
    val startDirection: Trajectory2D.Direction,
    val endDirection: Trajectory2D.Direction = startDirection,
) : LineSegment2D by lineSegment

private class TangentPath(val tangents: List<Tangent>) {
    fun last() = tangents.last()
}

private fun TangentPath(vararg tangents: Tangent) = TangentPath(listOf(*tangents))

/**
 * Create inner and outer tangents between two circles.
 * This method returns a map of segments using [DubinsPath] connection type notation.
 */
internal fun Circle2D.tangentsToCircle(
    other: Circle2D,
): Map<DubinsPath.Type, LineSegment2D> = with(Euclidean2DSpace) {
    //return empty map for concentric circles
    if (center.equalsVector(other.center)) return emptyMap()

    // A line connecting centers
    val line = LineSegment(center, other.center)
    // Distance between centers
    val distance = line.begin.distanceTo(line.end)
    val angle1 = atan2(other.center.x - center.x, other.center.y - center.y)
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
            val r = if (r1.sign == r2.sign) {
                r1.absoluteValue - r2.absoluteValue
            } else {
                r1.absoluteValue + r2.absoluteValue
            }
            if (distance * distance >= r * r) {
                val l = sqrt(distance * distance - r * r)
                angle2 = if (r1.absoluteValue > r2.absoluteValue) {
                    angle1 + r1.sign * atan2(r.absoluteValue, l)
                } else {
                    angle1 - r2.sign * atan2(r.absoluteValue, l)
                }
                val w = vector(-cos(angle2), sin(angle2))
                put(
                    route,
                    LineSegment(
                        center + w * r1,
                        other.center + w * r2
                    )
                )
            } else {
                throw Exception("Circles should not intersect")
            }
        }
    }
}

private fun dubinsTangentsToCircles(
    firstCircle: Circle2D,
    secondCircle: Circle2D,
    firstObstacle: Obstacle,
    secondObstacle: Obstacle,
): Map<DubinsPath.Type, Tangent> = with(Euclidean2DSpace) {
    val line = LineSegment(firstCircle.center, secondCircle.center)
    val distance = line.begin.distanceTo(line.end)
    val angle1 = atan2(
        secondCircle.center.x - firstCircle.center.x,
        secondCircle.center.y - firstCircle.center.y
    )
    var r: Double
    var angle2: Double
    val routes = mapOf(
        DubinsPath.Type.RSR to Pair(firstCircle.radius, secondCircle.radius),
        DubinsPath.Type.RSL to Pair(firstCircle.radius, -secondCircle.radius),
        DubinsPath.Type.LSR to Pair(-firstCircle.radius, secondCircle.radius),
        DubinsPath.Type.LSL to Pair(-firstCircle.radius, -secondCircle.radius)
    )
    return buildMap {
        for ((route: DubinsPath.Type, r1r2) in routes) {
            val r1 = r1r2.first
            val r2 = r1r2.second
            r = if (r1.sign == r2.sign) {
                r1.absoluteValue - r2.absoluteValue
            } else {
                r1.absoluteValue + r2.absoluteValue
            }
            if (distance * distance >= r * r) {
                val l = sqrt(distance * distance - r * r)
                angle2 = if (r1.absoluteValue > r2.absoluteValue) {
                    angle1 + r1.sign * atan2(r.absoluteValue, l)
                } else {
                    angle1 - r2.sign * atan2(r.absoluteValue, l)
                }
                val w = vector(-cos(angle2), sin(angle2))
                put(
                    route,
                    Tangent(
                        startCircle = Circle2D(firstCircle.center, firstCircle.radius),
                        endCircle = secondCircle,
                        startObstacle = firstObstacle,
                        endObstacle = secondObstacle,
                        lineSegment = LineSegment(
                            firstCircle.center + w * r1,
                            secondCircle.center + w * r2
                        ),
                        startDirection = route.first,
                        endDirection = route.third
                    )
                )
            } else {
                throw Exception("Circles should not intersect")
            }
        }
    }
}

internal class Obstacle(
    public val circles: List<Circle2D>,
) {
    internal val tangents: List<Tangent> = boundaryTangents().first
    public val boundaryRoute: DubinsPath.Type = boundaryTangents().second

    public val center: Vector2D<Double> = vector(
        circles.sumOf { it.center.x } / circles.size,
        circles.sumOf { it.center.y } / circles.size
    )

    private fun boundaryTangents(): Pair<List<Tangent>, DubinsPath.Type> {
        // outer tangents for a polygon circles can be either lsl or rsr

        fun Circle2D.dubinsTangentsToCircles(
            other: Circle2D,
        ): Map<DubinsPath.Type, Tangent> = with(Euclidean2DSpace) {
            val line = LineSegment(center, other.center)
            val d = line.begin.distanceTo(line.end)
            val angle1 = atan2(other.center.x - center.x, other.center.y - center.y)
            var r: Double
            var angle2: Double
            val routes = mapOf(
                DubinsPath.Type.RSR to Pair(radius, other.radius),
                DubinsPath.Type.LSL to Pair(-radius, -other.radius)
            )
            return buildMap {
                for ((routeType, r1r2) in routes) {
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
                        put(
                            routeType, Tangent(
                                Circle2D(center, radius),
                                other,
                                this@Obstacle,
                                this@Obstacle,
                                LineSegment(
                                    center + w * r1,
                                    other.center + w * r2
                                ),
                                startDirection = routeType.first,
                                endDirection = routeType.third
                            )
                        )
                    } else {
                        throw Exception("Circles should not intersect")
                    }
                }
            }
        }

        val firstCircles = circles
        val secondCircles = circles.slice(1..circles.lastIndex) +
                circles[0]
        val lslTangents = firstCircles.zip(secondCircles)
        { a, b -> a.dubinsTangentsToCircles(b)[DubinsPath.Type.LSL]!! }
        val rsrTangents = firstCircles.zip(secondCircles)
        { a, b -> a.dubinsTangentsToCircles(b)[DubinsPath.Type.RSR]!! }
        val center = vector(
            circles.sumOf { it.center.x } / circles.size,
            circles.sumOf { it.center.y } / circles.size
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

    internal fun nextTangent(circle: Circle2D, direction: Trajectory2D.Direction): Tangent {
        if (direction == boundaryRoute.first) {
            for (i in circles.indices) {
                if (circles[i] == circle) {
                    return tangents[i]
                }
            }
        } else {
            for (i in circles.indices) {
                if (circles[i] == circle) {
                    if (i > 0) {
                        return Tangent(
                            circles[i],
                            circles[i - 1],
                            this,
                            this,
                            LineSegment(
                                tangents[i - 1].lineSegment.end,
                                tangents[i - 1].lineSegment.begin
                            ),
                            direction
                        )
                    } else {
                        return Tangent(
                            circles[0],
                            circles.last(),
                            this,
                            this,
                            LineSegment(
                                tangents.last().lineSegment.end,
                                tangents.last().lineSegment.begin
                            ),
                            direction
                        )
                    }
                }
            }
        }

        error("next tangent not found")
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Obstacle) return false
        return circles == other.circles
    }

    override fun hashCode(): Int {
        return circles.hashCode()
    }
}

internal fun Obstacle(vararg circles: Circle2D): Obstacle = Obstacle(listOf(*circles))

private fun LineSegment2D.intersectSegment(other: LineSegment2D): Boolean {
    fun crossProduct(v1: DoubleVector2D, v2: DoubleVector2D): Double {
        return v1.x * v2.y - v1.y * v2.x
    }
    return if (crossProduct(other.begin - begin, other.end - begin).sign ==
        crossProduct(other.begin - end, other.end - end).sign
    ) {
        false
    } else {
        crossProduct(begin - other.begin, end - other.begin).sign != crossProduct(
            begin - other.end,
            end - other.end
        ).sign
    }
}

private fun LineSegment2D.intersectCircle(circle: Circle2D): Boolean {
    val a = (begin.x - end.x).pow(2.0) + (begin.y - end.y).pow(2.0)
    val b = 2 * ((begin.x - end.x) * (end.x - circle.center.x) +
            (begin.y - end.y) * (end.y - circle.center.y))
    val c = (end.x - circle.center.x).pow(2.0) + (end.y - circle.center.y).pow(2.0) -
            circle.radius.pow(2.0)
    val d = b.pow(2.0) - 4 * a * c
    if (d < 1e-6) {
        return false
    } else {
        val t1 = (-b - d.pow(0.5)) * 0.5 / a
        val t2 = (-b + d.pow(0.5)) * 0.5 / a
        if (((0 < t1) and (t1 < 1)) or ((0 < t2) and (t2 < 1))) {
            return true
        }
    }
    return false
}

private fun Tangent.intersectObstacle(obstacle: Obstacle): Boolean {
    for (tangent in obstacle.tangents) {
        if (lineSegment.intersectSegment(tangent.lineSegment)) {
            return true
        }
    }
    for (circle in obstacle.circles) {
        if (lineSegment.intersectCircle(circle)) {
            return true
        }
    }
    return false
}

private fun outerTangents(first: Obstacle, second: Obstacle): Map<DubinsPath.Type, Tangent> = buildMap {
    for (circle1 in first.circles) {
        for (circle2 in second.circles) {
            for (tangent in dubinsTangentsToCircles(circle1, circle2, first, second)) {
                if (!(tangent.value.intersectObstacle(first))
                    and !(tangent.value.intersectObstacle(second))
                ) {
                    put(
                        tangent.key,
                        tangent.value
                    )
                }
            }
        }
    }
}

private fun arcLength(
    circle: Circle2D,
    point1: DoubleVector2D,
    point2: DoubleVector2D,
    direction: Trajectory2D.Direction,
): Double {
    val phi1 = atan2(point1.y - circle.center.y, point1.x - circle.center.x)
    val phi2 = atan2(point2.y - circle.center.y, point2.x - circle.center.x)
    var angle = 0.0
    when (direction) {
        Trajectory2D.L -> {
            angle = if (phi2 >= phi1) {
                phi2 - phi1
            } else {
                2 * PI + phi2 - phi1
            }
        }

        Trajectory2D.R -> {
            angle = if (phi2 >= phi1) {
                2 * PI - (phi2 - phi1)
            } else {
                -(phi2 - phi1)
            }
        }
    }
    return circle.radius * angle
}

private fun normalVectors(v: DoubleVector2D, r: Double): Pair<DoubleVector2D, DoubleVector2D> {
    return Pair(
        r * vector(v.y / norm(v), -v.x / norm(v)),
        r * vector(-v.y / norm(v), v.x / norm(v))
    )
}

private fun constructTangentCircles(
    point: DoubleVector2D,
    direction: DoubleVector2D,
    r: Double,
): Map<Trajectory2D.Type, Circle2D> {
    val center1 = point + normalVectors(direction, r).first
    val center2 = point + normalVectors(direction, r).second
    val p1 = center1 - point
    return if (atan2(p1.y, p1.x) - atan2(direction.y, direction.x) in listOf(PI / 2, -3 * PI / 2)) {
        mapOf(
            Trajectory2D.L to Circle2D(center1, r),
            Trajectory2D.R to Circle2D(center2, r)
        )
    } else {
        mapOf(
            Trajectory2D.L to Circle2D(center2, r),
            Trajectory2D.R to Circle2D(center1, r)
        )
    }
}

private fun sortedObstacles(
    currentObstacle: Obstacle,
    obstacles: List<Obstacle>,
): List<Obstacle> {
    return obstacles.sortedBy { norm(it.center - currentObstacle.center) }
}

private fun tangentsAlongTheObstacle(
    initialCircle: Circle2D,
    direction: Trajectory2D.Direction,
    finalCircle: Circle2D,
    obstacle: Obstacle,
): List<Tangent> {
    val dubinsTangents = mutableListOf<Tangent>()
    var tangent = obstacle.nextTangent(initialCircle, direction)
    dubinsTangents.add(tangent)
    while (tangent.endCircle != finalCircle) {
        tangent = obstacle.nextTangent(tangent.endCircle, direction)
        dubinsTangents.add(tangent)
    }
    return dubinsTangents
}

private fun allFinished(
    paths: List<TangentPath>,
    finalObstacle: Obstacle,
): Boolean {
    for (path in paths) {
        if (path.last().endObstacle != finalObstacle) {
            return false
        }
    }
    return true
}

private fun LineSegment2D.toTrajectory() = StraightTrajectory2D(begin, end)


private fun TangentPath.toTrajectory(): CompositeTrajectory2D = CompositeTrajectory2D(
    buildList {
        tangents.zipWithNext().forEach { (left, right) ->
            add(left.lineSegment.toTrajectory())
            add(
                CircleTrajectory2D.of(
                    right.startCircle.center,
                    left.lineSegment.end,
                    right.lineSegment.begin,
                    right.startDirection
                )
            )
        }

        add(tangents.last().lineSegment.toTrajectory())
    }
)

internal fun findAllPaths(
    start: DubinsPose2D,
    startingRadius: Double,
    finish: DubinsPose2D,
    finalRadius: Double,
    obstacles: List<Obstacle>,
): List<CompositeTrajectory2D> {
    fun DubinsPose2D.direction() = vector(cos(bearing), sin(bearing))

    val initialCircles = constructTangentCircles(
        start,
        start.direction(),
        startingRadius
    )
    val finalCircles = constructTangentCircles(
        finish,
        finish.direction(),
        finalRadius
    )
    val trajectories = mutableListOf<CompositeTrajectory2D>()
    for (i in listOf(Trajectory2D.L, Trajectory2D.R)) {
        for (j in listOf(Trajectory2D.L, Trajectory2D.R)) {
            val finalCircle = finalCircles[j]!!
            val finalObstacle = Obstacle(listOf(finalCircle))
            var currentPaths: List<TangentPath> = listOf(
                TangentPath(
                    Tangent(
                        initialCircles[i]!!,
                        initialCircles[i]!!,
                        Obstacle(listOf(initialCircles[i]!!)),
                        Obstacle(listOf(initialCircles[i]!!)),
                        LineSegment(start, start),
                        i
                    )
                )
            )
            while (!allFinished(currentPaths, finalObstacle)) {
                val newPaths = mutableListOf<TangentPath>()
                for (tangentPath: TangentPath in currentPaths) {
                    val currentCircle = tangentPath.last().endCircle
                    val currentDirection: Trajectory2D.Direction = tangentPath.last().endDirection
                    val currentObstacle = tangentPath.last().endObstacle
                    var nextObstacle: Obstacle? = null
                    if (currentObstacle != finalObstacle) {
                        val tangentToFinal = outerTangents(currentObstacle, finalObstacle)[DubinsPath.Type(
                            currentDirection,
                            Trajectory2D.S,
                            j
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
                        val nextTangents: Map<DubinsPath.Type, Tangent> = outerTangents(currentObstacle, nextObstacle)
                            .filter { (key, tangent) ->
                                obstacles.none { obstacle -> tangent.intersectObstacle(obstacle) } &&
                                        key.first == currentDirection &&
                                        (nextObstacle != finalObstacle || key.third == j)
                            }

                        var tangentsAlong: List<Tangent>
                        for (tangent in nextTangents.values) {
                            if (tangent.startCircle == tangentPath.last().endCircle) {
                                val lengthMaxPossible = arcLength(
                                    tangent.startCircle,
                                    tangentPath.last().lineSegment.end,
                                    tangent.startObstacle.nextTangent(
                                        tangent.startCircle,
                                        currentDirection
                                    ).lineSegment.begin,
                                    currentDirection
                                )
                                val lengthCalculated = arcLength(
                                    tangent.startCircle,
                                    tangentPath.last().lineSegment.end,
                                    tangent.lineSegment.begin,
                                    currentDirection
                                )
                                tangentsAlong = if (lengthCalculated > lengthMaxPossible) {
                                    tangentsAlongTheObstacle(
                                        currentCircle,
                                        currentDirection,
                                        tangent.startCircle,
                                        currentObstacle
                                    )
                                } else {
                                    emptyList()
                                }
                            } else {
                                tangentsAlong = tangentsAlongTheObstacle(
                                    currentCircle,
                                    currentDirection,
                                    tangent.startCircle,
                                    currentObstacle
                                )
                            }
                            newPaths.add(TangentPath(tangentPath.tangents + tangentsAlong + tangent))
                        }
                    } else {
                        newPaths.add(tangentPath)
                    }
                }
                currentPaths = newPaths
            }

            trajectories += currentPaths.map { tangentPath ->
                val lastDirection: Trajectory2D.Direction = tangentPath.last().endDirection
                val end = finalCircles[j]!!
                TangentPath(
                    tangentPath.tangents +
                            Tangent(
                                end,
                                end,
                                Obstacle(end),
                                Obstacle(end),
                                LineSegment(finish, finish),
                                startDirection = lastDirection,
                                endDirection = j
                            )
                )
            }.map { it.toTrajectory() }
        }
    }
    return trajectories
}


public object Obstacles {
    public fun allPathsAvoiding(
        start: DubinsPose2D,
        finish: DubinsPose2D,
        trajectoryRadius: Double,
        obstaclePolygons: List<Polygon<Double>>,
    ): List<CompositeTrajectory2D> {
        val obstacles: List<Obstacle> = obstaclePolygons.map { polygon ->
            Obstacle(polygon.points.map { point -> Circle2D(point, trajectoryRadius) })
        }
        return findAllPaths(start, trajectoryRadius, finish, trajectoryRadius, obstacles)
    }
}





