/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.operations.invoke
import kotlin.math.sqrt

@OptIn(UnstableKMathAPI::class)
public interface Vector2D : Point<Double>, Vector {
    public val x: Double
    public val y: Double
    override val size: Int get() = 2

    override operator fun get(index: Int): Double = when (index) {
        0 -> x
        1 -> y
        else -> error("Accessing outside of point bounds")
    }

    override operator fun iterator(): Iterator<Double> = listOf(x, y).iterator()
}

public val Vector2D.r: Double
    get() = Euclidean2DSpace { norm() }

@Suppress("FunctionName")
public fun Vector2D(x: Double, y: Double): Vector2D = Vector2DImpl(x, y)

private data class Vector2DImpl(
    override val x: Double,
    override val y: Double,
) : Vector2D

/**
 * 2D Euclidean space
 */
public object Euclidean2DSpace : GeometrySpace<Vector2D>, ScaleOperations<Vector2D> {
    override val zero: Vector2D by lazy { Vector2D(0.0, 0.0) }

    public fun Vector2D.norm(): Double = sqrt(x * x + y * y)
    override fun Vector2D.unaryMinus(): Vector2D = Vector2D(-x, -y)

    override fun Vector2D.distanceTo(other: Vector2D): Double = (this - other).norm()
    override fun add(a: Vector2D, b: Vector2D): Vector2D = Vector2D(a.x + b.x, a.y + b.y)
    override fun scale(a: Vector2D, value: Double): Vector2D = Vector2D(a.x * value, a.y * value)
    override fun Vector2D.dot(other: Vector2D): Double = x * other.x + y * other.y
}
