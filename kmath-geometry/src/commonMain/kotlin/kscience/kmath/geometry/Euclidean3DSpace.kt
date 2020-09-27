package kscience.kmath.geometry

import kscience.kmath.linear.Point
import kscience.kmath.operations.SpaceElement
import kscience.kmath.operations.invoke
import kotlin.math.sqrt

public interface Vector3D : Point<Double>, Vector, SpaceElement<Vector3D, Vector3D, Euclidean3DSpace> {
    public val x: Double
    public val y: Double
    public val z: Double
    public override val context: Euclidean3DSpace get() = Euclidean3DSpace
    public override val size: Int get() = 3

    public override operator fun get(index: Int): Double = when (index) {
        1 -> x
        2 -> y
        3 -> z
        else -> error("Accessing outside of point bounds")
    }

    public override operator fun iterator(): Iterator<Double> = listOf(x, y, z).iterator()
    public override fun unwrap(): Vector3D = this
    public override fun Vector3D.wrap(): Vector3D = this
}

@Suppress("FunctionName")
public fun Vector3D(x: Double, y: Double, z: Double): Vector3D = Vector3DImpl(x, y, z)

public val Vector3D.r: Double get() = Euclidean3DSpace { sqrt(norm()) }

private data class Vector3DImpl(
    override val x: Double,
    override val y: Double,
    override val z: Double
) : Vector3D

public object Euclidean3DSpace : GeometrySpace<Vector3D> {
    public override val zero: Vector3D by lazy { Vector3D(0.0, 0.0, 0.0) }

    public fun Vector3D.norm(): Double = sqrt(x * x + y * y + z * z)

    public override fun Vector3D.distanceTo(other: Vector3D): Double = (this - other).norm()

    public override fun add(a: Vector3D, b: Vector3D): Vector3D =
        Vector3D(a.x + b.x, a.y + b.y, a.z + b.z)

    public override fun multiply(a: Vector3D, k: Number): Vector3D =
        Vector3D(a.x * k.toDouble(), a.y * k.toDouble(), a.z * k.toDouble())

    public override fun Vector3D.dot(other: Vector3D): Double =
        x * other.x + y * other.y + z * other.z
}
