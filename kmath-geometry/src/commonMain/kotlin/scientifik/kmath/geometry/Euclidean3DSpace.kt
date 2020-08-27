package scientifik.kmath.geometry

import scientifik.kmath.linear.Point
import scientifik.kmath.operations.SpaceElement
import scientifik.kmath.operations.invoke
import kotlin.math.sqrt


interface Vector3D : Point<Double>, Vector, SpaceElement<Vector3D, Vector3D, Euclidean3DSpace> {
    val x: Double
    val y: Double
    val z: Double
    override val context: Euclidean3DSpace get() = Euclidean3DSpace
    override val size: Int get() = 3

    override operator fun get(index: Int): Double = when (index) {
        1 -> x
        2 -> y
        3 -> z
        else -> error("Accessing outside of point bounds")
    }

    override operator fun iterator(): Iterator<Double> = listOf(x, y, z).iterator()

    override fun unwrap(): Vector3D = this

    override fun Vector3D.wrap(): Vector3D = this
}

@Suppress("FunctionName")
fun Vector3D(x: Double, y: Double, z: Double): Vector3D = Vector3DImpl(x, y, z)

val Vector3D.r: Double get() = Euclidean3DSpace { sqrt(norm()) }

private data class Vector3DImpl(
    override val x: Double,
    override val y: Double,
    override val z: Double
) : Vector3D

object Euclidean3DSpace : GeometrySpace<Vector3D> {
    override val zero: Vector3D = Vector3D(0.0, 0.0, 0.0)

    fun Vector3D.norm(): Double = sqrt(x * x + y * y + z * z)

    override fun Vector3D.distanceTo(other: Vector3D): Double = (this - other).norm()

    override fun add(a: Vector3D, b: Vector3D): Vector3D =
        Vector3D(a.x + b.x, a.y + b.y, a.z + b.z)

    override fun multiply(a: Vector3D, k: Number): Vector3D =
        Vector3D(a.x * k.toDouble(), a.y * k.toDouble(), a.z * k.toDouble())

    override fun Vector3D.dot(other: Vector3D): Double =
        x * other.x + y * other.y + z * other.z
}
