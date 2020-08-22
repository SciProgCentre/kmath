package scientifik.kmath.geometry

import scientifik.kmath.linear.Point
import scientifik.kmath.operations.SpaceElement
import scientifik.kmath.operations.invoke
import kotlin.math.sqrt


interface Vector2D : Point<Double>, Vector, SpaceElement<Vector2D, Vector2D, Euclidean2DSpace> {
    val x: Double
    val y: Double
    override val context: Euclidean2DSpace get() = Euclidean2DSpace
    override val size: Int get() = 2

    override operator fun get(index: Int): Double = when (index) {
        1 -> x
        2 -> y
        else -> error("Accessing outside of point bounds")
    }

    override operator fun iterator(): Iterator<Double> = listOf(x, y).iterator()
    override fun unwrap(): Vector2D = this
    override fun Vector2D.wrap(): Vector2D = this
}

val Vector2D.r: Double get() = Euclidean2DSpace { sqrt(norm()) }

@Suppress("FunctionName")
fun Vector2D(x: Double, y: Double): Vector2D = Vector2DImpl(x, y)

private data class Vector2DImpl(
    override val x: Double,
    override val y: Double
) : Vector2D

/**
 * 2D Euclidean space
 */
object Euclidean2DSpace : GeometrySpace<Vector2D> {
    fun Vector2D.norm(): Double = sqrt(x * x + y * y)

    override fun Vector2D.distanceTo(other: Vector2D): Double = (this - other).norm()

    override fun add(a: Vector2D, b: Vector2D): Vector2D =
        Vector2D(a.x + b.x, a.y + b.y)

    override fun multiply(a: Vector2D, k: Number): Vector2D =
        Vector2D(a.x * k.toDouble(), a.y * k.toDouble())

    override val zero: Vector2D = Vector2D(0.0, 0.0)

    override fun Vector2D.dot(other: Vector2D): Double =
        x * other.x + y * other.y
}