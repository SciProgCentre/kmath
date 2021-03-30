package space.kscience.kmath.geometry

import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.operations.SpaceElement
import space.kscience.kmath.operations.invoke
import kotlin.math.sqrt

@OptIn(UnstableKMathAPI::class)
public interface Vector2D : Point<Double>, Vector, SpaceElement<Vector2D, Euclidean2DSpace> {
    public val x: Double
    public val y: Double
    public override val context: Euclidean2DSpace get() = Euclidean2DSpace
    public override val size: Int get() = 2

    public override operator fun get(index: Int): Double = when (index) {
        1 -> x
        2 -> y
        else -> error("Accessing outside of point bounds")
    }

    public override operator fun iterator(): Iterator<Double> = listOf(x, y).iterator()
}

public val Vector2D.r: Double
    get() = Euclidean2DSpace { sqrt(norm()) }

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
    public override val zero: Vector2D by lazy { Vector2D(0.0, 0.0) }

    public fun Vector2D.norm(): Double = sqrt(x * x + y * y)
    override fun Vector2D.unaryMinus(): Vector2D = Vector2D(-x, -y)

    public override fun Vector2D.distanceTo(other: Vector2D): Double = (this - other).norm()
    public override fun add(a: Vector2D, b: Vector2D): Vector2D = Vector2D(a.x + b.x, a.y + b.y)
    public override fun scale(a: Vector2D, value: Double): Vector2D = Vector2D(a.x * value, a.y * value)
    public override fun Vector2D.dot(other: Vector2D): Double = x * other.x + y * other.y
}
