/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.linear.Point
import space.kscience.kmath.operations.Norm
import space.kscience.kmath.operations.ScaleOperations
import kotlin.math.pow
import kotlin.math.sqrt

public interface Vector2D<T> : Point<T>, Vector {
    public val x: T
    public val y: T
    override val size: Int get() = 2

    override operator fun get(index: Int): T = when (index) {
        0 -> x
        1 -> y
        else -> error("Accessing outside of point bounds")
    }

    override operator fun iterator(): Iterator<T> = iterator {
        yield(x)
        yield(y)
    }
}


public operator fun <T> Vector2D<T>.component1(): T = x
public operator fun <T> Vector2D<T>.component2(): T = y

public typealias DoubleVector2D = Vector2D<Double>

public val Vector2D<Double>.r: Double get() = Euclidean2DSpace.norm(this)


/**
 * 2D Euclidean space
 */
public object Euclidean2DSpace : GeometrySpace<DoubleVector2D>,
    ScaleOperations<DoubleVector2D>,
    Norm<DoubleVector2D, Double> {

    private data class Vector2DImpl(
        override val x: Double,
        override val y: Double,
    ) : DoubleVector2D

    public fun vector(x: Number, y: Number): DoubleVector2D = Vector2DImpl(x.toDouble(), y.toDouble())

    override val zero: DoubleVector2D by lazy { vector(0.0, 0.0) }

    override fun norm(arg: DoubleVector2D): Double = sqrt(arg.x.pow(2) + arg.y.pow(2))

    override fun DoubleVector2D.unaryMinus(): DoubleVector2D = vector(-x, -y)

    override fun DoubleVector2D.distanceTo(other: DoubleVector2D): Double = norm(this - other)
    override fun add(left: DoubleVector2D, right: DoubleVector2D): DoubleVector2D =
        vector(left.x + right.x, left.y + right.y)

    override fun scale(a: DoubleVector2D, value: Double): DoubleVector2D = vector(a.x * value, a.y * value)
    override fun DoubleVector2D.dot(other: DoubleVector2D): Double = x * other.x + y * other.y

    public val xAxis: DoubleVector2D = vector(1.0, 0.0)
    public val yAxis: DoubleVector2D = vector(0.0, 1.0)
}
